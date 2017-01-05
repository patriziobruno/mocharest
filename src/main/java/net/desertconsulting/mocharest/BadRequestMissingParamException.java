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

import javax.ws.rs.BadRequestException;

/**
 * Thrown when a request misses a mandatory path parameter.
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class BadRequestMissingParamException extends BadRequestException {

    /**
     * Construct a new {@link BadRequestMissingParamException} setting the 
     * message property to "missing param '{@code param}'" to report a missing path
     * parameter.
     * @param param name of the missing param
     */
    public BadRequestMissingParamException(String param) {
        super(String.format("missing required param '%s'", param));
    }
}
