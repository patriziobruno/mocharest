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
 * A {@link javax.servlet.httpHttpServletRequest} wrapper to handle requests
 * to a MochaREST javascript application server.
 * 
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaRequest extends HttpServletRequestWrapper {

    private MochaRequestHandler handler;
    private final Map<String, Object> pathParameterMap;
    private Object body;
    private final String cType;

    /**
     * Initialize a new instance of {@link MochaRequest} retrieving the request's
     * content-type.
     * @param request servlet request to be wrapped.
     */
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


    /**
     * Initialize a new instance of {@link MochaRequest} retrieving the request's
     * content-type and parsing parameters and body of the request.
     * @param request servlet request to be wrapped.
     * @param handler handler for the request
     */
    public MochaRequest(HttpServletRequest request, MochaRequestHandler handler) {
        this(request);
        this.handler = handler;
        validateQueryString();
        parsePathInfo();
        parseBody();
    }

    /**
     * Retrieves the value of a given path-parameter.
     * @param name name of the path-parameter to be retrieved.
     * @return value of a given path-parameter
     */
    public Object getPathParameter(String name) {
        return pathParameterMap.containsKey(name) ? pathParameterMap.get(
                name) : null;
    }

    /**
     * Returns a map of all the request's path-parameters.
     * @return a map of all the request's path-parameters
     */
    public Map<String, Object> getPathParameterMap() {
        return pathParameterMap;
    }

    /**
     * Body of the request.
     * @return body of the request
     */
    public Object getBody() {
        return body;
    }

    /**
     * The default behavior of this method is to return getParameterMap() on the
     * wrapped request object.
     * @return an immutable java.util.Map containing parameter names as keys and 
     * parameter values as map values. The keys in the parameter map are of type String.
     * The values in the parameter map are of type String or String array.
     */
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

    /**
     * Check for the presence of all the mandatory query-string parameters. If
     * a parameter is missing a {@link BadRequestMissingQueryParamException} is thrown.
     * @throws BadRequestMissingQueryParamException if a required query parameter
     * is missing.
     */
    private void validateQueryString() {

        List<String> parameterNames = Collections.list(getParameterNames());
        Optional<String> res = handler.queryParameters.stream().parallel().filter((p) -> !parameterNames.contains(p)).findAny();
        if (res.isPresent()) {
            throw new BadRequestMissingQueryParamException(res.get());
        }
    }

    /**
     * Parse all the path parameters in the request.
     * @throws BadRequestException if a required path parameters is not found
     */
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

    /**
     * Parse a request's body. Only supported formats are application/xml
     * and application/json. Any other format will be treated as JSON and may 
     * result in errors.
     */
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
