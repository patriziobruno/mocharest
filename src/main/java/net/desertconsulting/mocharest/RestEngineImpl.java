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
package net.desertconsulting.mocharest;

import com.github.ooxi.jdatauri.DataUri;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.Undefined;
import net.desertconsulting.mocharest.request.MochaRequest;
import net.desertconsulting.mocharest.request.MochaRequestHandler;
import net.desertconsulting.mocharest.response.MochaResponse;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class RestEngineImpl implements RestEngine {

    private final Map<String, List<MochaRequestHandler>> handlers;
    private final ServletContext context;

    /**
     * Initialize a new instance of {@link RestEngineImpl}.
     * @param context servlet context for resolving context resources.
     */
    public RestEngineImpl(ServletContext context) {
        handlers = new HashMap<>();
        handlers.put(GET_METHOD, new ArrayList<>());
        handlers.put(POST_METHOD, new ArrayList<>());
        handlers.put(PUT_METHOD, new ArrayList<>());
        handlers.put(HEAD_METHOD, new ArrayList<>());
        handlers.put(OPTIONS_METHOD, new ArrayList<>());
        this.context = context;
    }

    @Override
    public RestEngine get(String url, JSObject... parms) throws
            MalformedURLException {
        register(GET_METHOD, url, parms);
        return this;
    }

    @Override
    public RestEngine post(String url, JSObject... parms) throws
            MalformedURLException {
        register(POST_METHOD, url, parms);
        return this;
    }

    @Override
    public RestEngine put(String url, JSObject... parms) throws
            MalformedURLException {
        register(PUT_METHOD, url, parms);
        return this;
    }

    @Override
    public RestEngine head(String url, JSObject... parms) throws
            MalformedURLException {
        register(HEAD_METHOD, url, parms);
        return this;
    }

    @Override
    public RestEngine options(String url, JSObject... parms) throws
            MalformedURLException {
        register(OPTIONS_METHOD, url, parms);
        return this;
    }

    @Override
    public String getFileContent(String path) throws MalformedURLException,
            IOException, URISyntaxException {
        Reader reader = getContentReader(path);
        if (reader != null) {
            try (BufferedReader breader = new BufferedReader(reader);
                    StringWriter writer = new StringWriter()) {
                String line;
                while ((line = breader.readLine()) != null) {
                    writer.append(line);
                }
                return writer.toString();
            }
        } else {
            throw new FileNotFoundException(path);
        }
    }

    /**
     * Opens a given file. It can either be a context's resource or a data-URI.
     * @param path path to the text-file, relative to context path. It can also be
     * a data-URI.
     * @return {@link FileReader} for files or {@link InputStreamReader} for data-URIs
     * @throws MalformedURLException malformed path/data-URI
     * @throws FileNotFoundException file not found
     * @throws URISyntaxException  malformed path/data-URI
     */
    private Reader getContentReader(String path) throws URISyntaxException,
            MalformedURLException, FileNotFoundException {
        if (path.toLowerCase().startsWith("data:")) {
            DataUri duri = DataUri.parse(path, Charset.defaultCharset());
            return new InputStreamReader(
                    new ByteArrayInputStream(duri.getData()));
        } else {
            String spath;
            if (path.startsWith("/")) {
                spath = path;
            } else {
                spath = "/" + path;
            }
            URL resource = context.getResource(spath);
            if (resource != null) {
                return new FileReader(new File(resource.toURI()));
            }
            return null;
        }
    }

    /**
     * Run handler for a given request.
     * 
     * @param request http servlet request to be handled
     * @param response http servlet restponse to send a response
     */
    public void handle(HttpServletRequest request, HttpServletResponse response) {

        // Retrieving config and initializing MochaRequest will perform
        // all the required request validation
        MochaRequestHandler handler = getConfig(request);
        MochaRequest req = new MochaRequest(request, handler);

        // If the found handler doesn't have an handling function, then just ignore it.
        // The handler has already done data validation and that's enough.
        if (handler.function != null) {
            MochaResponse resp = new MochaResponse(response, req);
            Object val = handler.function.call(null, req, resp, req.
                    getParametersMap(),
                    req.getPathParameterMap());
            if (!(val instanceof Undefined)) {
                try {
                    resp.send(val);
                } catch (IOException ex) {
                    try {
                        Logger.getLogger(RestEngineImpl.class.getName()).log(
                                Level.SEVERE, null, ex);
                        resp.sendError(500, ex.toString());
                    } catch (IOException ex1) {
                        Logger.getLogger(RestEngineImpl.class.getName()).log(
                                Level.SEVERE, null, ex1);
                    }
                }
            } else {
                resp.commit();
            }
        }
    }

    /**
     * Register a new handler for the given method-url pair.
     * @param method one of GET|POST|PUT|HEAD|OPTIONS
     * @param url URL pattern. Path parameters are described by the syntax
     * {parameter_name:type}. Supported types are int, long, string, double,
     * float, hex. hex parameters are converted to {@link byte} arrays.
     * @param parms configuration parameters: 1st parm can be either a function 
     * or a configuration object with the following properties: contentType and 
     * acceptType. 2nd parameter, if present, must follow a configuration object 
     * and be a function If no function is set this handler will be registered 
     * only for URL checking.
     *
     * @throws MalformedURLException {@code url} has an incorrect format
     */
    private void register(String method, String url, JSObject... parms) throws
            MalformedURLException {

        if (parms != null) {
            MochaRequestHandler handler = new MochaRequestHandler(url, parms);
            handlers.get(method).add(handler);
        } else {
            throw new IllegalArgumentException("handler");
        }
    }

    /**
     * Retrieves the first handler matching the given request.
     * 
     * @param request servlet request
     * @return a handler for a given request
     * @throws NotFoundException no handlers found for the given request
     * @throws BadRequestException the request comes with an unsupported method
     */
    private MochaRequestHandler getConfig(HttpServletRequest request) {
        String path = request.getPathInfo();
        String method = request.getMethod();
        List<MochaRequestHandler> methodHandlers = handlers.get(method);
        if (methodHandlers != null) {
            Optional<MochaRequestHandler> h = methodHandlers.stream()
                    .filter(handler -> handler.getPathPattern().matcher(path).matches()).findFirst();
            if (h.isPresent()) {
                return h.get();
            }
            throw new NotFoundException();
        } else {
            throw new BadRequestException(String.format(
                    "method %s not supported", method));
        }
    }

    /**
     * List all available handlers.
     * @return a list of all the available handlers
     */
    Map<String, List<MochaRequestHandler>> getHandlers() {
        return handlers;
    }
}
