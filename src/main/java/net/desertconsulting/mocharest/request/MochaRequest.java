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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import net.desertconsulting.mocharest.BadRequestMissingQueryParamException;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaRequest extends HttpServletRequestWrapper {

    private MochaRequestHandler handler;
    private final Map<String, Object> pathParameterMap;
    private Object body;
    private final String cType;

    public MochaRequest(HttpServletRequest request) {
        super(request);
        this.pathParameterMap = new HashMap<>();

        String contentType = request.getContentType();

        if (!MediaType.APPLICATION_JSON.equals(contentType)
                && !MediaType.APPLICATION_XML.equals(contentType)) {
            contentType = MediaType.APPLICATION_JSON;
        }

        cType = contentType;
    }

    public MochaRequest(HttpServletRequest request, MochaRequestHandler handler) {
        this(request);
        this.handler = handler;
        validateQueryString();
        parsePathInfo();
        parseBody();
    }

    public Object getPathParameter(String name) {
        return pathParameterMap.containsKey(name) ? pathParameterMap.get(
                name) : null;
    }

    public Map<String, Object> getPathParameterMap() {
        return pathParameterMap;
    }

    public Object getBody() {
        return body;
    }

    public Map<String, Object> getParametersMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        Map<String, Object> parametersMap = new HashMap<>(parameterMap.size());
        parameterMap.forEach((String key, String[] value) -> {
            Object val = null;
            if (value == null || value.length != 1) {
                val = value;
            } else if (value.length == 1) {
                val = value[0];
            }
            parametersMap.put(key, val);
        });
        return parametersMap;
    }

    private void validateQueryString() {

        List<String> parameterNames = Collections.list(getParameterNames());
        Optional<String> res = handler.queryParameters.stream().parallel().filter((p) -> !parameterNames.contains(p)).findAny();
        if (res.isPresent()) {
            throw new BadRequestMissingQueryParamException(res.get());
        }
    }

    private void parsePathInfo() {

        String pathInfo = getPathInfo();
        Matcher matcher = handler.getPathPattern().matcher(pathInfo);
        final AtomicInteger index = new AtomicInteger(1);
        if (matcher.find()) {
            pathParameterMap.putAll(handler.pathParameters.entrySet().stream().collect(Collectors.toMap((pp) -> pp.getKey(),
                     (pp) -> {
                        try {
                            return pp.getValue().convert.apply(matcher.group(index.getAndIncrement()));
                        } catch (Exception ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                                    null, ex);
                            throw new BadRequestParamException(pp.getKey(), ex);
                        }
                    })));
        } else {
            throw new BadRequestException("path parameters don't match");
        }
    }

    private void parseBody() {
        if (getContentLength() > 0) {
            try (InputStream input
                    = getInputStream()) {
                ObjectMapper mapper;
                switch (cType) {
                    case MediaType.APPLICATION_XML:
                        mapper = new XmlMapper();
                        break;
                    case MediaType.APPLICATION_JSON:
                    default:
                        mapper = new ObjectMapper();
                        break;
                }

                this.body = (Object) mapper.readValue(input, HashMap.class);

            } catch (IOException ex) {
                Logger.getLogger(MochaRequest.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
    }
}
