/* 
 * Copyright 2017 Patrizio Bruno <desertconsulting@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.desertconsulting.mocharest.request;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaRequestHandler {

    List<String> queryParameters;
    Map<String, PathParam> pathParameters;
    public JSObject function;

    private final String contentType;
    private final String acceptType;

    private final static Pattern PP_PATTERN = Pattern.compile(
            "(/\\{([a-zA-Z_]+):([^}]*)\\})");

    private final static Map<String, Function<String, Object>> CONVERTERS
            = new HashMap<>();

    static {
        CONVERTERS.put("string", s -> s);
        CONVERTERS.put("int", s -> Integer.parseInt(s));
        CONVERTERS.put("long", s -> Long.parseLong(s));
        CONVERTERS.put("float", s -> Float.parseFloat(s));
        CONVERTERS.put("double", s -> Double.parseDouble(s));
        CONVERTERS.put("hex", s -> new BigInteger(s.toUpperCase(), 16).
                toByteArray());
    }

    private final static Map<String, Pattern> PATTERNS = new HashMap<>();

    static {
        PATTERNS.put("string", Pattern.compile("[^/]*"));
        PATTERNS.put("int", Pattern.compile("[0-9]+", 0));
        PATTERNS.put("long", Pattern.compile("[0-9]+", 0));
        PATTERNS.put("float", Pattern.compile("[0-9]*(\\.[0-9]+)"));
        PATTERNS.put("double", Pattern.compile("[0-9]*(\\.[0-9]+)"));
        PATTERNS.put("hex", Pattern.compile("[\\da-f]{2,}",
                Pattern.CASE_INSENSITIVE));
    }
    private Pattern pathPattern;

    public MochaRequestHandler(String url, JSObject... parms) throws
            MalformedURLException {
        if (parms.length == 1 && parms[0] != null && parms[0].isFunction()) {
            function = parms[0];
            contentType = null;
            acceptType = null;
        } else if (parms.length == 1 && parms[0] != null && !parms[0].
                isFunction()) {
            contentType = (String) parms[0].getMember("contentType");
            if (parms[0].hasMember("acceptType")) {
                acceptType = (String) parms[0].getMember("acceptType");
            } else {
                acceptType = null;
            }
        } else if (parms.length > 1) {
            if (parms[1].isFunction()) {
                contentType = (String) parms[0].getMember("contentType");
                if (parms[0].hasMember("acceptType")) {
                    acceptType = (String) parms[0].getMember("acceptType");
                } else {
                    acceptType = null;
                }
                function = parms[1];
            } else {
                throw new IllegalArgumentException(
                        "the second parameter is expected to be a function");
            }
        } else {
            contentType = null;
            acceptType = null;
            function = null;
        }

        pathParameters = new HashMap<>();
        URL u = new URL("file", "", url);
        parseUrl(u);
        parseQueryString(u);
    }

    public Pattern getPathPattern() {
        return pathPattern;
    }

    private void parseUrl(URL url) {

        StringBuffer sb = new StringBuffer();
        Matcher matcher = PP_PATTERN.matcher(url.getPath());
        boolean found = false;
        while (matcher.find()) {
            found = true;
            String name = matcher.group(2);
            String type = matcher.group(3);
            if (CONVERTERS.containsKey(type)) {
                PathParam p = new PathParam();
                p.name = name;
                p.convert = CONVERTERS.get(type);
                pathParameters.put(name, p);
                matcher.appendReplacement(sb, String.format("/(%s)", PATTERNS.
                        get(type).pattern()));
            } else {
                throw new IllegalArgumentException(String.format(
                        "'%s' is of an unknown type '%s'", name, type));
            }
        }

        if (found) {
            matcher.appendTail(sb);
            pathPattern = Pattern.compile(sb.toString());
        } else {
            pathPattern = Pattern.compile(url.getPath());
        }
    }

    private void parseQueryString(URL url) {
        String queryString = url.getQuery();
        if (queryString != null && queryString.length() > 0 && queryString.
                trim().length() > 0) {

            queryParameters = Arrays.stream(queryString.split("&"))
                    .map(it -> {
                        final int idx = it.indexOf("=");
                        final String key = idx > 0 ? it.substring(0, idx) : it;
                        return key;
                    })
                    .collect(Collectors.toList());
        } else {
            queryParameters = Collections.emptyList();
        }
    }

    public String getContentType() {
        return contentType;
    }
}
