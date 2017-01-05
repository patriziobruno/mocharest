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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.Undefined;
import mockit.Mock;
import mockit.MockUp;
import net.desertconsulting.mocharest.request.MochaRequest;
import net.desertconsulting.mocharest.response.MochaResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class RestEngineImplTest {

    static ServletContext context;
    private final static String JSON_CONTENT_TYPE = "application/json";
    private JSObject functionParam;
    private JSObject jsonConfigurationParam;

    public RestEngineImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        context = new MockUp<ServletContext>() {
            @Mock
            public URL getResource(String path) throws MalformedURLException {

                if (path.contains("foo")) {
                    return new URL(path);
                }

                if (new File(path).exists()) {
                    try {
                        return new URL("file", "", URLEncoder.encode(path, Charset.defaultCharset().name()).replace("%2F", "/"));
                    } catch (UnsupportedEncodingException ex) {
                        return null;
                    }
                }

                return null;
            }
        }.getMockInstance();

    }

    @Before
    public void setUp() {
        functionParam = new MockUp<JSObject>() {
            @Mock
            public boolean isFunction() {
                return true;
            }
        }.getMockInstance();
        final String jsonContentType = JSON_CONTENT_TYPE;
        jsonConfigurationParam = new MockUp<JSObject>() {
            @Mock
            public Object getMember(String name) {
                switch (name) {
                    case "contentType":
                        return jsonContentType;
                    case "acceptType":
                        return jsonContentType;
                }
                return null;
            }

            @Mock
            public boolean isFunction() {
                return false;
            }
        }.getMockInstance();
    }

    @Test
    public void testGetReturnsThis() throws Exception {
        System.out.println("getReturnsThis");
        String url = "/test/{foo:int}";
        JSObject[] parms = new JSObject[0];
        RestEngineImpl instance = new RestEngineImpl(context);
        RestEngine expResult = instance;
        RestEngine result = instance.get(url, parms);
        assertEquals(expResult, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNullParameters() throws Exception {
        System.out.println("getNullParameters");
        String url = "/test/{foo:int}";
        JSObject[] parms = null;
        RestEngineImpl instance = new RestEngineImpl(context);
        RestEngine expResult = instance;
        RestEngine result = instance.get(url, parms);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIllegalParameterList() throws Exception {
        System.out.println("getIllegalParameterList");
        testRegisterHandlerIllegalArgument(RestEngine.GET_METHOD);
    }

    @Test
    public void testGet2Parameters() throws Exception {
        System.out.println("get2Parameters");
        testRegisterHandler2Parameters(RestEngine.GET_METHOD);
    }

    @Test
    public void testGet1FunctionParameter() throws Exception {
        System.out.println("testGet1FunctionParameter");
        testRegisterHandler1FunctionParameter(RestEngine.GET_METHOD);
    }

    @Test
    public void testPostReturnsThis() throws Exception {
        System.out.println("postReturnsThis");
        String url = "/test/{foo:int}";
        JSObject[] parms = new JSObject[0];
        RestEngineImpl instance = new RestEngineImpl(context);
        RestEngine expResult = instance;
        RestEngine result = instance.post(url, parms);
        assertEquals(expResult, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostNullParameters() throws Exception {
        System.out.println("postNullParameters");
        String url = "/test/{foo:int}";
        JSObject[] parms = null;
        RestEngineImpl instance = new RestEngineImpl(context);
        RestEngine expResult = instance;
        RestEngine result = instance.post(url, parms);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostIllegalParameterList() throws Exception {
        System.out.println("postIllegalParameterList");
        testRegisterHandlerIllegalArgument(RestEngine.POST_METHOD);
    }

    @Test
    public void testPost2Parameters() throws Exception {
        System.out.println("post2Parameters");
        testRegisterHandler2Parameters(RestEngine.POST_METHOD);
    }

    @Test
    public void testPost1FunctionParameter() throws Exception {
        System.out.println("post1FunctionParameter");
        testRegisterHandler1FunctionParameter(RestEngine.POST_METHOD);
    }

    @Test
    public void testPutReturnsThis() throws Exception {
        System.out.println("put");
        String url = "/test/{foo:int}";
        JSObject[] parms = new JSObject[0];
        RestEngineImpl instance = new RestEngineImpl(context);
        RestEngine expResult = instance;
        RestEngine result = instance.put(url, parms);
        assertEquals(expResult, result);
    }

    @Test
    public void testHeadReturnsThis() throws Exception {
        System.out.println("head");
        String url = "/test/{foo:int}";
        JSObject[] parms = new JSObject[0];
        RestEngineImpl instance = new RestEngineImpl(context);
        RestEngine expResult = instance;
        RestEngine result = instance.head(url, parms);
        assertEquals(expResult, result);
    }

    @Test
    public void testOptionsReturnsThis() throws Exception {
        System.out.println("options");
        String url = "/test/{foo:int}";
        JSObject[] parms = new JSObject[0];
        RestEngineImpl instance = new RestEngineImpl(context);
        RestEngine expResult = instance;
        RestEngine result = instance.options(url, parms);
        assertEquals(expResult, result);
    }

    @Test(expected = MalformedURLException.class)
    public void testGetFileContentWithMalformedURLException() throws Exception {
        System.out.println("getFileContentWithMalformedURLException");
        String path = "foo:\\foo.txt";
        RestEngineImpl instance = new RestEngineImpl(context);
        instance.getFileContent(path);
    }

    @Test
    public void testGetFileContentWithDataURI() throws Exception {
        System.out.println("getFileContentWithDataURI");
        String path = "data:text/plain;charset=utf-8;base64,dGVzdA==";
        String exptectedResult = "test";
        RestEngineImpl instance = new RestEngineImpl(context);
        String result = instance.getFileContent(path);
        assertEquals(exptectedResult, result);
    }

    @Test
    public void testGetFileContent() throws Exception {
        System.out.println("getFileContent");
        File f = File.createTempFile(Double.toHexString(Math.random()), Double.toHexString(Math.random()));
        f.deleteOnExit();
        String path = f.getPath();
        String exptectedResult = "";
        RestEngineImpl instance = new RestEngineImpl(context);
        String result = instance.getFileContent(path);
        assertEquals(exptectedResult, result);
    }

    @Test(expected = FileNotFoundException.class)
    public void testGetFileContentWithNotExistingFile() throws Exception {
        System.out.println("getFileContentWithNotExistingFile");
        String path = String.format("/notExistingPath-%f/notExistingfile-%f.rand", Math.random(), Math.random());
        String exptectedResult = "test";
        RestEngineImpl instance = new RestEngineImpl(context);
        String result = instance.getFileContent(path);
        assertEquals(exptectedResult, result);
    }

    @Test
    public void testHandleWithADavalidationOnlyHandler() throws MalformedURLException {
        System.out.println("handleWithADavalidationOnlyHandler");
        String testUrl = "/test/{test:int}?test1";
        HttpServletRequest request = new MockUp<HttpServletRequest>() {
            @Mock
            public String getMethod() {
                return RestEngine.GET_METHOD;
            }

            @Mock
            public String getPathInfo() {
                return "/test/1";
            }

            @Mock
            public Map<String, String[]> getParameterMap() {
                Map<String, String[]> rv = new HashMap<>();
                rv.put("test1", new String[]{"test"});
                return rv;
            }

            @Mock
            public Enumeration<String> getParameterNames() {
                return new Vector<String>() {
                    {
                        add("test1");
                    }
                }.elements();
            }
        }.getMockInstance();
        HttpServletResponse response = new MockUp<HttpServletResponse>() {
        }.getMockInstance();
        RestEngineImpl instance = new RestEngineImpl(context);
        instance.get(testUrl);
        instance.handle(request, response);
    }

    @Test
    public void testHandlePostJson() throws MalformedURLException {
        System.out.println("handlePostJson");
        String testUrl = "/test/{test:int}";
        final String testBody = "{\"test\":\"test\"}";
        HttpServletRequest request = MockedRequest.create()
                .withMethod(RestEngine.POST_METHOD)
                .withTestBody(testBody)
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1")
                .build().getMockInstance();
        MockedResponse sr = getTestHandleResponse();
        RestEngineImpl instance = new RestEngineImpl(context);
        JSObject parm = new MockUp<JSObject>() {
            @Mock
            public boolean isFunction() {
                return true;
            }

            @Mock
            public Object call(Object o, Object... os) {
                if (os[0] instanceof MochaRequest && os[0] != null) {
                    Object body = ((MochaRequest) os[0]).getBody();
                    if (os[1] instanceof MochaResponse && os[1] != null) {
                        if (os[2] instanceof Map && os[2] != null) {
                            if (os[3] instanceof Map && os[3] != null) {
                                if ((int) ((Map<String, Object>) os[3]).get("test") == 1) {
                                    return body;
                                } else {
                                    throw new IllegalArgumentException("request.PathParameters['test']");
                                }
                            } else {
                                throw new IllegalArgumentException("request.PathParameters");
                            }
                        } else {
                            throw new IllegalArgumentException("request.Parameters");
                        }
                    } else {
                        throw new IllegalArgumentException("response");
                    }
                } else {
                    throw new IllegalArgumentException("request");
                }
            }
        }.getMockInstance();
        instance.post(testUrl, parm);
        instance.handle(request, sr.getMockInstance());
        String result = new String(sr.bos.toByteArray());
        assertEquals(testBody, result);
    }

    @Test
    public void testHandlePostJsonHandlerReturnsUndefined() throws MalformedURLException {
        System.out.println("handlePostJsonHandlerReturnsUndefined");
        String testUrl = "/test/{test:int}";
        final String testBody = "{\"test\":\"test\"}";
        HttpServletRequest request = MockedRequest.create()
                .withMethod(RestEngine.POST_METHOD)
                .withTestBody(testBody)
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1")
                .build().getMockInstance();
        MockedResponse sr = getTestHandleResponse();
        RestEngineImpl instance = new RestEngineImpl(context);
        JSObject parm = new MockUp<JSObject>() {
            @Mock
            public boolean isFunction() {
                return true;
            }

            @Mock
            public Object call(Object o, Object... os) {
                if (os[0] instanceof MochaRequest && os[0] != null) {
                    Object body = ((MochaRequest) os[0]).getBody();
                    if (os[1] instanceof MochaResponse && os[1] != null) {
                        if (os[2] instanceof Map && os[2] != null) {
                            if (os[3] instanceof Map && os[3] != null) {
                                if ((int) ((Map<String, Object>) os[3]).get("test") == 1) {
                                    return Undefined.getUndefined();
                                } else {
                                    throw new IllegalArgumentException("request.PathParameters['test']");
                                }
                            } else {
                                throw new IllegalArgumentException("request.PathParameters");
                            }
                        } else {
                            throw new IllegalArgumentException("request.Parameters");
                        }
                    } else {
                        throw new IllegalArgumentException("response");
                    }
                } else {
                    throw new IllegalArgumentException("request");
                }
            }
        }.getMockInstance();
        instance.post(testUrl, parm);
        HttpServletResponse response = sr.getMockInstance();
        instance.handle(request, response);
        String result = new String(sr.bos.toByteArray());
        assertEquals("", result);
        assertEquals(response.getContentType(), MediaType.APPLICATION_JSON);
    }

    @Test
    public void testHandlePostJsonThrowsIOExceptionOnSend() throws MalformedURLException {
        System.out.println("handlePostJsonThrowsIOExceptionOnSend");
        String testUrl = "/test/{test:int}";
        final String testBody = "{\"test\":\"test\"}";
        HttpServletRequest request = MockedRequest.create()
                .withMethod(RestEngine.POST_METHOD)
                .withTestBody(testBody)
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1")
                .build().getMockInstance();
        MockedResponse sr = MockedResponse.create()
                .withIOExceptionOnWrite()
                .build();
        RestEngineImpl instance = new RestEngineImpl(context);
        JSObject parm = new MockUp<JSObject>() {
            @Mock
            public boolean isFunction() {
                return true;
            }

            @Mock
            public Object call(Object o, Object... os) {
                if (os[0] instanceof MochaRequest && os[0] != null) {
                    Object body = ((MochaRequest) os[0]).getBody();
                    if (os[1] instanceof MochaResponse && os[1] != null) {
                        if (os[2] instanceof Map && os[2] != null) {
                            if (os[3] instanceof Map && os[3] != null) {
                                if ((int) ((Map<String, Object>) os[3]).get("test") == 1) {
                                    return body;
                                } else {
                                    throw new IllegalArgumentException("request.PathParameters['test']");
                                }
                            } else {
                                throw new IllegalArgumentException("request.PathParameters");
                            }
                        } else {
                            throw new IllegalArgumentException("request.Parameters");
                        }
                    } else {
                        throw new IllegalArgumentException("response");
                    }
                } else {
                    throw new IllegalArgumentException("request");
                }
            }
        }.getMockInstance();
        instance.post(testUrl, parm);
        instance.handle(request, sr.getMockInstance());
        String result = new String(sr.bos.toByteArray());
        assertEquals("", result);
        assertEquals(500, sr.getStatusCode());
        assertTrue(sr.getStatusMessage().contains(": write"));
    }

    @Test
    public void testHandlePostJsonThrowsIOExceptionOnSendError() throws MalformedURLException {
        System.out.println("handlePostJsonThrowsIOExceptionOnSendError");
        String testUrl = "/test/{test:int}";
        final String testBody = "{\"test\":\"test\"}";
        HttpServletRequest request = MockedRequest.create()
                .withMethod(RestEngine.POST_METHOD)
                .withTestBody(testBody)
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1")
                .build().getMockInstance();
        MockedResponse sr = MockedResponse.create()
                .withIOExceptionOnWrite()
                .withIOExceptionOnSendError()
                .build();
        RestEngineImpl instance = new RestEngineImpl(context);
        JSObject parm = new MockUp<JSObject>() {
            @Mock
            public boolean isFunction() {
                return true;
            }

            @Mock
            public Object call(Object o, Object... os) {
                if (os[0] instanceof MochaRequest && os[0] != null) {
                    Object body = ((MochaRequest) os[0]).getBody();
                    if (os[1] instanceof MochaResponse && os[1] != null) {
                        if (os[2] instanceof Map && os[2] != null) {
                            if (os[3] instanceof Map && os[3] != null) {
                                if ((int) ((Map<String, Object>) os[3]).get("test") == 1) {
                                    return body;
                                } else {
                                    throw new IllegalArgumentException("request.PathParameters['test']");
                                }
                            } else {
                                throw new IllegalArgumentException("request.PathParameters");
                            }
                        } else {
                            throw new IllegalArgumentException("request.Parameters");
                        }
                    } else {
                        throw new IllegalArgumentException("response");
                    }
                } else {
                    throw new IllegalArgumentException("request");
                }
            }
        }.getMockInstance();
        instance.post(testUrl, parm);
        instance.handle(request, sr.getMockInstance());
        String result = new String(sr.bos.toByteArray());
        assertEquals("", result);
        assertEquals(0, sr.getStatusCode());
        assertNull(sr.getStatusMessage());
    }

    @Test
    public void testHandlePutJson() throws MalformedURLException {
        System.out.println("handlePutJson");
        String testUrl = "/test/{test:string}/{id:int}";
        final String testBody = "{\"test\":\"test\"}";
        HttpServletRequest request = MockedRequest.create()
                .withMethod(RestEngine.PUT_METHOD)
                .withTestBody(testBody)
                .withMimeType(MediaType.APPLICATION_JSON)
                .withPath("/test/1/1")
                .build().getMockInstance();

        MockedResponse sr = getTestHandleResponse();
        RestEngineImpl instance = new RestEngineImpl(context);
        JSObject parm = new MockUp<JSObject>() {
            @Mock
            public boolean isFunction() {
                return true;
            }

            @Mock
            public Object call(Object o, Object... os) {
                if (os[0] instanceof MochaRequest && os[0] != null) {
                    Object body = ((MochaRequest) os[0]).getBody();
                    if (os[1] instanceof MochaResponse && os[1] != null) {
                        if (os[2] instanceof Map && os[2] != null) {
                            if (os[3] instanceof Map && os[3] != null) {
                                if ("1".equals(((Map<String, Object>) os[3]).get("test"))) {

                                    if ((int) ((Map<String, Object>) os[3]).get("id") == 1) {
                                        return body;
                                    } else {
                                        throw new IllegalArgumentException("request.PathParameters['id']");
                                    }
                                } else {
                                    throw new IllegalArgumentException("request.PathParameters['test']");
                                }
                            } else {
                                throw new IllegalArgumentException("request.PathParameters");
                            }
                        } else {
                            throw new IllegalArgumentException("request.Parameters");
                        }
                    } else {
                        throw new IllegalArgumentException("response");
                    }
                } else {
                    throw new IllegalArgumentException("request");
                }
            }
        }.getMockInstance();
        instance.put(testUrl, parm);
        instance.handle(request, sr.getMockInstance());
        String result = new String(sr.bos.toByteArray());
        assertEquals(testBody, result);
    }

    @Test
    public void testHandlePutXml() throws MalformedURLException {
        System.out.println("handlePutXml");
        String testUrl = "/test/{test:string}/{id:int}";
        final String testBody = "<HashMap><test>test</test></HashMap>";
        HttpServletRequest request = MockedRequest.create()
                .withMethod(RestEngine.PUT_METHOD)
                .withTestBody(testBody)
                .withMimeType(MediaType.APPLICATION_XML)
                .withPath("/test/1/1")
                .build().getMockInstance();

        MockedResponse sr = getTestHandleResponse();
        RestEngineImpl instance = new RestEngineImpl(context);
        JSObject parm = new MockUp<JSObject>() {
            @Mock
            public boolean isFunction() {
                return true;
            }

            @Mock
            public Object call(Object o, Object... os) {
                if (os[0] instanceof MochaRequest && os[0] != null) {
                    Object body = ((MochaRequest) os[0]).getBody();
                    if (os[1] instanceof MochaResponse && os[1] != null) {
                        if (os[2] instanceof Map && os[2] != null) {
                            if (os[3] instanceof Map && os[3] != null) {
                                if ("1".equals(((Map<String, Object>) os[3]).get("test"))) {

                                    if ((int) ((Map<String, Object>) os[3]).get("id") == 1) {
                                        return body;
                                    } else {
                                        throw new IllegalArgumentException("request.PathParameters['id']");
                                    }
                                } else {
                                    throw new IllegalArgumentException("request.PathParameters['test']");
                                }
                            } else {
                                throw new IllegalArgumentException("request.PathParameters");
                            }
                        } else {
                            throw new IllegalArgumentException("request.Parameters");
                        }
                    } else {
                        throw new IllegalArgumentException("response");
                    }
                } else {
                    throw new IllegalArgumentException("request");
                }
            }
        }.getMockInstance();
        instance.put(testUrl, parm);
        instance.handle(request, sr.getMockInstance());
        String result = new String(sr.bos.toByteArray());
        assertEquals(testBody, result);
    }

    @Test(expected = NotFoundException.class)
    public void testHandleWithoutHandlers() {
        System.out.println("handleWithoutHandlers");
        HttpServletRequest request = new MockUp<HttpServletRequest>() {
            @Mock
            public String getMethod() {
                return RestEngine.GET_METHOD;
            }
        }.getMockInstance();
        HttpServletResponse response = new MockUp<HttpServletResponse>() {
        }.getMockInstance();
        RestEngineImpl instance = new RestEngineImpl(context);
        instance.handle(request, response);
    }

    @Test(expected = NotFoundException.class)
    public void testHandleWithoutMatchingHandler() throws MalformedURLException {
        System.out.println("handleWithoutHandlers");
        HttpServletRequest request = new MockUp<HttpServletRequest>() {
            @Mock
            public String getMethod() {
                return RestEngine.GET_METHOD;
            }

            @Mock
            public String getPathInfo() {
                return "/nothandled";
            }
        }.getMockInstance();
        HttpServletResponse response = new MockUp<HttpServletResponse>() {
        }.getMockInstance();
        RestEngineImpl instance = new RestEngineImpl(context);
        instance.get("/test");
        instance.handle(request, response);
    }

    private void testRegisterHandler2Parameters(String method) throws Exception {
        String url = "/test/{foo:int}";

        JSObject[] parms = new JSObject[]{jsonConfigurationParam, functionParam};

        RestEngineImpl instance = new RestEngineImpl(context);
        testRegister(instance, method, url, parms);
        JSObject expectedResult = functionParam;
        assertEquals(expectedResult, instance.getHandlers().get(method).get(0).function);
        assertEquals(JSON_CONTENT_TYPE, instance.getHandlers().get(method).get(0).getContentType());
    }

    private void testRegisterHandler1FunctionParameter(String method) throws Exception {
        String url = "/test/{foo:int}";

        JSObject[] parms = new JSObject[]{functionParam};

        RestEngineImpl instance = new RestEngineImpl(context);
        testRegister(instance, method, url, parms);
        JSObject expectedResult = functionParam;
        assertEquals(expectedResult, instance.getHandlers().get(method).get(0).function);
    }

    private void testRegisterHandlerIllegalArgument(String method) throws Exception {
        String url = "/test/{foo:int}";

        JSObject[] parms = new JSObject[]{functionParam, jsonConfigurationParam};

        RestEngineImpl instance = new RestEngineImpl(context);
        testRegister(instance, method, url, parms);
    }

    private RestEngine testRegister(RestEngineImpl instance, String method, String url, JSObject[] parms) throws MalformedURLException {
        switch (method) {
            case RestEngine.GET_METHOD:
                return instance.get(url, parms);
            case RestEngine.POST_METHOD:
                return instance.post(url, parms);
            case RestEngine.PUT_METHOD:
                return instance.put(url, parms);
            case RestEngine.OPTIONS_METHOD:
                return instance.options(url, parms);
            case RestEngine.HEAD_METHOD:
                return instance.head(url, parms);
        }
        return null;
    }

    private MockedResponse getTestHandleResponse() {
        return MockedResponse.create().build();
    }
}
