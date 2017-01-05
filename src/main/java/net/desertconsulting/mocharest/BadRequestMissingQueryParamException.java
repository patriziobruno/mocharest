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

/**
 * Thrown when a request misses a mandatory query_string parameter.
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class BadRequestMissingQueryParamException extends BadRequestMissingParamException {

    /**
     * Construct a new {@link BadRequestMissingQueryParamException} setting the 
     * message property to "missing param 'query:{@code param}'".
     * @param param name of the missing query_string param
     */
    public BadRequestMissingQueryParamException(String param) {
        super(String.format("query:%s", param));
    }
}
