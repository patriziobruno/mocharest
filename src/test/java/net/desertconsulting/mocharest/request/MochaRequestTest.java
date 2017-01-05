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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import net.desertconsulting.mocharest.BadRequestMissingQueryParamException;
import net.desertconsulting.mocharest.MockedRequest;
import net.desertconsulting.mocharest.RestEngine;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaRequestTest {

    @Test
    public void testGetPathParameter() throws MalformedURLException {
        System.out.println("getPathParameter");
        String name = "test";
        MochaRequest instance = new MochaRequest(MockedRequest.create()
                .withMethod(RestEngine.GET_METHOD)
                .withTestBody("")
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1").build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Object expResult = 1;
        Object result = instance.getPathParameter(name);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNotExistingPathParameter() throws MalformedURLException {
        System.out.println("getPathParameter");
        String name = "test1";
        MochaRequest instance = new MochaRequest(MockedRequest.create()
                .withMethod(RestEngine.GET_METHOD)
                .withTestBody("")
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1")
                .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Object expResult = null;
        Object result = instance.getPathParameter(name);
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructorThrowingIOException() throws MalformedURLException {
        System.out.println("getConstructorThrowingIOException");
        MochaRequest instance = new MochaRequest(MockedRequest.create()
                .withMethod(RestEngine.POST_METHOD)
                .withTestBody("test")
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1")
                .withIOExceptionOnOpen()
                .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Object expResult = null;
        Object result = instance.getBody();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetPathParameterMap() throws MalformedURLException {
        System.out.println("getPathParameterMap");
        String name = "test";
        MochaRequest instance = new MochaRequest(MockedRequest.create()
                .withMethod(RestEngine.GET_METHOD)
                .withTestBody("")
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1").build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Map<String, Object> result = instance.getPathParameterMap();
        assertTrue(result.containsKey(name));
    }

    @Test
    public void testGetBody() throws MalformedURLException {
        System.out.println("getBody");
        String body = "{\"test\":1}";
        MochaRequest instance = new MochaRequest(MockedRequest.create()
                .withMethod(RestEngine.POST_METHOD)
                .withTestBody(body)
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1").build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Object expResult = 1;
        Object result = instance.getBody();
        assertTrue(result instanceof HashMap);
        assertEquals(expResult, ((HashMap) result).get("test"));
    }

    @Test
    public void testGetParametersMap() throws MalformedURLException,
            URISyntaxException {
        System.out.println("getParametersMap");
        MochaRequest instance = new MochaRequest(
                MockedRequest.create()
                        .withMethod(RestEngine.POST_METHOD)
                        .withTestBody("")
                        .withMimeType(MediaType.APPLICATION_JSON)
                        .withPath("/test/1")
                        .withQuery("test=test")
                        .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Map<String, Object> result = instance.getParametersMap();
        assertTrue(result.containsKey("test"));
    }

    @Test
    public void testGetParametersMapWithValuelessParameter() throws
            MalformedURLException, URISyntaxException {
        System.out.println("getParametersMapWithValuelessParameter");
        MochaRequest instance = new MochaRequest(
                MockedRequest.create()
                        .withMethod(RestEngine.POST_METHOD)
                        .withTestBody("")
                        .withMimeType(MediaType.APPLICATION_JSON)
                        .withPath("/test/1")
                        .withQuery("test")
                        .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Map<String, Object> result = instance.getParametersMap();
        assertTrue(result.containsKey("test"));
        assertNull(result.get("test"));
    }

    @Test
    public void testGetParametersMapWithRepeatedParameter() throws
            MalformedURLException, URISyntaxException {
        System.out.println("getParametersMapWithRepeatedParameter");
        MochaRequest instance = new MochaRequest(
                MockedRequest.create()
                        .withMethod(RestEngine.POST_METHOD)
                        .withTestBody("")
                        .withMimeType(MediaType.APPLICATION_JSON)
                        .withPath("/test/1")
                        .withQuery("test=1&test=2")
                        .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Map<String, Object> result = instance.getParametersMap();
        assertTrue(result.containsKey("test"));
        assertArrayEquals(new String[]{"1", "2"}, (String[]) result.get("test"));
    }

    @Test(expected = BadRequestMissingQueryParamException.class)
    public void testGetParametersMapWithMissingQueryParameter() throws
            MalformedURLException, URISyntaxException {
        System.out.println("getParametersMapWithMissingQueryParameter");
        MochaRequest instance = new MochaRequest(
                MockedRequest.create()
                        .withMethod(RestEngine.POST_METHOD)
                        .withTestBody("")
                        .withMimeType(MediaType.APPLICATION_JSON)
                        .withPath("/test/1")
                        .withQuery("test=1&test=2")
                        .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}?test1")
        );
        Map<String, Object> result = instance.getParametersMap();
        assertTrue(result.containsKey("test"));
        assertArrayEquals(new String[]{"1", "2"}, (String[]) result.get("test"));
    }

    @Test(expected = BadRequestException.class)
    public void testGetParametersMapWithBadPathParameter() throws
            MalformedURLException, URISyntaxException {
        System.out.println("getParametersMapWithMissingQueryParameter");
        MochaRequest instance = new MochaRequest(
                MockedRequest.create()
                        .withMethod(RestEngine.POST_METHOD)
                        .withTestBody("")
                        .withMimeType(MediaType.APPLICATION_JSON)
                        .withPath("/test/ciao")
                        .withQuery("test=1&test=2")
                        .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Map<String, Object> result = instance.getParametersMap();
        assertTrue(result.containsKey("test"));
        assertArrayEquals(new String[]{"1", "2"}, (String[]) result.get("test"));
    }

    @Test(expected = BadRequestParamException.class)
    public void testGetParametersMapWithBadPathParameterValue() throws
            MalformedURLException, URISyntaxException {
        System.out.println("getParametersMapWithMissingQueryParameter");
        MochaRequest instance = new MochaRequest(
                MockedRequest.create()
                        .withMethod(RestEngine.POST_METHOD)
                        .withTestBody("")
                        .withMimeType(MediaType.APPLICATION_JSON)
                        .withPath("/test/115123123123")
                        .withQuery("test=1&test=2")
                        .build().getMockInstance(),
                new MochaRequestHandler("/test/{test:int}")
        );
        Map<String, Object> result = instance.getParametersMap();
        assertTrue(result.containsKey("test"));
        assertArrayEquals(new String[]{"1", "2"}, (String[]) result.get("test"));
    }
}
