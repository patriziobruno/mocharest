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

import javax.ws.rs.BadRequestException;

/**
 * Thrown when a request has a bad value for a path parameter.
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class BadRequestParamException extends BadRequestException {

    /**
     * Construct a new {@link BadRequestParamException} setting the 
     * message property to "bad parameter value for '{@code param}'" to report a bad value
     * for a path parameter.
     * @param param name of the bad valued parameter
     * @param ex exception thrown by the value converter
     */
    public BadRequestParamException(String param, Exception ex) {
        super(String.format("bad parameter value for '%s'", param), ex);
    }
}
