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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import jdk.nashorn.api.scripting.JSObject;

/**
 * It defines RESTful engines.
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public interface RestEngine {

    /**
     * HTTP GET Method
     */
    final static String GET_METHOD = "GET";
    /**
     * HTTP POST Method
     */
    final static String POST_METHOD = "POST";
    /**
     * HTTP PUT Method
     */
    final static String PUT_METHOD = "PUT";
    /**
     * HTTP HEAD Method
     */
    final static String HEAD_METHOD = "HEAD";
    /**
     * HTTP OPTIONS Method
     */
    final static String OPTIONS_METHOD = "OPTIONS";

    /**
     * Registers a Javascript function as a handler for GET requests of URLs
     * described by the pattern {@code url}.
     *
     * @param url URL pattern. Path parameters are described by the syntax
     * {parameter_name:type}. Supported types are int, long, string, double,
     * float, hex. hex parameters are converted to {@link byte} arrays.
     * @param parms configuration parameters: 1st parm can be either a function 
     * or a configuration object with the following properties: contentType and 
     * acceptType. 2nd parameter, if present, must follow a configuration object 
     * and be a function If no function is set this handler will be registered 
     * only for URL checking.
     *
     * @return it will return the object's instance for chaining.
     * @throws MalformedURLException {@code url} has an incorrect format
     */
    RestEngine get(String url, JSObject... parms) throws MalformedURLException;

    /**
     * Registers a Javascript function as a handler for POST requests of URLs
     * described by the pattern {@code url}.
     *
     * @param url URL pattern. Path parameters are described by the syntax
     * {parameter_name:type}. Supported types are int, long, string, double,
     * float, hex. hex parameters are converted to {@link byte} arrays.
     * @param parms configuration parameters: 1st parm can be either a function 
     * or a configuration object with the following properties: contentType and 
     * acceptType. 2nd parameter, if present, must follow a configuration object 
     * and be a function If no function is set this handler will be registered 
     * only for URL checking.
     *
     * @return it will return the object's instance for chaining.
     * @throws MalformedURLException {@code url} has an incorrect format
     */
    RestEngine post(String url, JSObject... parms) throws MalformedURLException;

    /**
     * Registers a Javascript function as a handler for PUT requests of URLs
     * described by the pattern {@code url}.
     *
     * @param url URL pattern. Path parameters are described by the syntax
     * {parameter_name:type}. Supported types are int, long, string, double,
     * float, hex. hex parameters are converted to {@link byte} arrays.
     * @param parms configuration parameters: 1st parm can be either a function 
     * or a configuration object with the following properties: contentType and 
     * acceptType. 2nd parameter, if present, must follow a configuration object 
     * and be a function If no function is set this handler will be registered 
     * only for URL checking.
     *
     * @return it will return the object's instance for chaining.
     * @throws MalformedURLException {@code url} has an incorrect format
     */
    RestEngine put(String url, JSObject... parms) throws MalformedURLException;

    /**
     * Registers a Javascript function as a handler for HEAD requests of URLs
     * described by the pattern {@code url}.
     *
     * @param url URL pattern. Path parameters are described by the syntax
     * {parameter_name:type}. Supported types are int, long, string, double,
     * float, hex. hex parameters are converted to {@link byte} arrays.
     * @param parms configuration parameters: 1st parm can be either a function 
     * or a configuration object with the following properties: contentType and 
     * acceptType. 2nd parameter, if present, must follow a configuration object 
     * and be a function If no function is set this handler will be registered 
     * only for URL checking.
     *
     * @return it will return the object's instance for chaining.
     * @throws MalformedURLException {@code url} has an incorrect format
     */
    RestEngine head(String url, JSObject... parms) throws MalformedURLException;

    /**
     * Registers a Javascript function as a handler for OPTIONS requests of URLs
     * described by the pattern {@code url}.
     *
     * @param url URL pattern. Path parameters are described by the syntax
     * {parameter_name:type}. Supported types are int, long, string, double,
     * float, hex. hex parameters are converted to {@link byte} arrays.
     * @param parms configuration parameters: 1st parm can be either a function 
     * or a configuration object with the following properties: contentType and 
     * acceptType. 2nd parameter, if present, must follow a configuration object 
     * and be a function If no function is set this handler will be registered 
     * only for URL checking.
     *
     * @return it will return the object's instance for chaining.
     * @throws MalformedURLException {@code url} has an incorrect format
     */
    RestEngine options(String url, JSObject... parms) throws
            MalformedURLException;

    /**
     * Reads the content of a context's resource or a data-URI as {@link String}.
     * 
     * @param path path to a context's resource or a data-URI
     * @return content of a text-file
     * @throws MalformedURLException malformed path
     * @throws IOException IO error loading the file
     * @throws URISyntaxException  malformed path
     */
    String getFileContent(String path) throws MalformedURLException, IOException, URISyntaxException;
}
