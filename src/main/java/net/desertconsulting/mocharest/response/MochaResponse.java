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
package net.desertconsulting.mocharest.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.core.MediaType;
import net.desertconsulting.mocharest.request.MochaRequest;

/**
 * A {@link javax.servlet.httpHttpServletReponse} wrapper to handle response from
 * a MochaREST javascript application server.
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaResponse extends HttpServletResponseWrapper {

    String cType;
    private final static XmlMapper XML_MAPPER = new XmlMapper();
    private final static ObjectMapper JSON_MAPPER = new ObjectMapper();

    public MochaResponse(HttpServletResponse response) {
        super(response);
        cType = MediaType.APPLICATION_JSON;
    }

    public MochaResponse(HttpServletResponse response, MochaRequest request) {
        this(response);

        String contentType = request.getHeader("accept");

        if (contentType == null) {
            contentType = request.getContentType();
        }

        if (contentType != null && contentType.contains(",")) {
            contentType = contentType.split(",")[0].trim();
        }

        if (!MediaType.APPLICATION_JSON.equals(contentType)
                && !MediaType.APPLICATION_XML.equals(contentType)) {
            contentType = MediaType.APPLICATION_JSON;
        }

        if ("OPTIONS".equals(request.getMethod())) {
            cType = null;
            response.setContentType(null);
        } else {
            cType = contentType;
        }
    }

    public void commit() {
        if (!isCommitted() && cType != null) {
            setContentType(cType);
        }
    }

    public void send(Object val) throws IOException {
        ObjectMapper mapper = null;
        if (cType != null) {
            switch (cType) {
                case MediaType.APPLICATION_JSON:
                    mapper = JSON_MAPPER;
                    break;
                case MediaType.APPLICATION_XML:
                    mapper = XML_MAPPER;
                    break;
            }
        }

        commit();

        try (OutputStream out = getOutputStream()) {
            if (mapper != null) {
                mapper.writeValue(out, val);
            } else {
                out.write(val.toString().getBytes());
            }
        }
    }
}
