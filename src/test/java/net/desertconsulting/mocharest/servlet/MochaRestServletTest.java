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
package net.desertconsulting.mocharest.servlet;

import java.io.ByteArrayInputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import mockit.Expectations;
import mockit.Mocked;
import net.desertconsulting.mocharest.MockedRequest;
import net.desertconsulting.mocharest.MockedResponse;
import net.desertconsulting.mocharest.RestEngine;
import org.junit.Test;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaRestServletTest {

    @Test
    public void testInit(@Mocked final ServletConfig config, @Mocked final ServletContext context) throws Exception {

        final ByteArrayInputStream stream = new ByteArrayInputStream("print('test');".getBytes());

        new Expectations() {
            {
                context.getResourceAsStream("/index.js");
                returns(stream);

                config.getServletContext();
                returns(context);
            }
        };
        System.out.println("init");
        MochaRestServlet instance = new MochaRestServlet();
        instance.init(config);
    }

    @Test(expected = ServletException.class)
    public void testInitWithScriptError(@Mocked final ServletConfig config, @Mocked final ServletContext context) throws Exception {

        final ByteArrayInputStream stream = new ByteArrayInputStream("nonexistingfunction('test');".getBytes());

        new Expectations() {
            {
                context.getResourceAsStream("/index.js");
                returns(stream);

                config.getServletContext();
                returns(context);
            }
        };
        System.out.println("initWithScriptError");
        MochaRestServlet instance = new MochaRestServlet();
        instance.init(config);
    }

    @Test
    public void testService(@Mocked final HttpServletRequest req, @Mocked final HttpServletResponse resp,
            @Mocked final ServletConfig config, @Mocked final ServletContext context) throws Exception {
        System.out.println("serviceNotHandledRequest");

        final ByteArrayInputStream stream = new ByteArrayInputStream("print('test');".getBytes());

        new Expectations() {
            {
                context.getResourceAsStream("/index.js");
                returns(stream);

                config.getServletContext();
                returns(context);
            }
        };

        MochaRestServlet instance = new MochaRestServlet();
        instance.init(config);
        instance.service(req, resp);
    }

    @Test
    public void testService(
            @Mocked final ServletConfig config, @Mocked final ServletContext context) throws Exception {
        System.out.println("service");

        final ByteArrayInputStream stream = new ByteArrayInputStream("print('test');".getBytes());

        new Expectations() {
            {
                context.getResourceAsStream("/index.js");
                returns(stream);

                config.getServletContext();
                returns(context);
            }
        };

        MochaRestServlet instance = new MochaRestServlet();

        instance.init(config);

        HttpServletRequest req = MockedRequest.create()
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test")
                .withMethod(RestEngine.GET_METHOD)
                .build().getMockInstance();
        HttpServletResponse resp = MockedResponse.create()
                .build().getMockInstance();
        instance.engine.getRestEngine().get("/test");
        instance.service(req, resp);
    }

}
