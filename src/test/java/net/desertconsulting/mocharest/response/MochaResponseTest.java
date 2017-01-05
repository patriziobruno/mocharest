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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import net.desertconsulting.mocharest.MockedRequest;
import net.desertconsulting.mocharest.MockedResponse;
import net.desertconsulting.mocharest.RestEngine;
import net.desertconsulting.mocharest.request.MochaRequest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaResponseTest {

    @Test
    public void testCommit() {
        System.out.println("commit");
        HttpServletResponse response = MockedResponse.create().build().getMockInstance();
        MochaResponse instance = new MochaResponse(response);
        instance.commit();
    }

    @Test
    public void testSend() throws Exception {
        System.out.println("send");
        Object val = "test";
        String exptectedResult = String.format("\"%s\"", val);
        MockedResponse mockedResponse = MockedResponse.create().build();
        HttpServletResponse response = mockedResponse.getMockInstance();
        MochaResponse instance = new MochaResponse(response);
        instance.send(val);
        assertTrue(response.isCommitted());
        assertEquals(exptectedResult, new String(mockedResponse.bos.toByteArray()));
    }

    @Test(expected = IOException.class)
    public void testSendThrowsIOExceptionOnWrite() throws Exception {
        System.out.println("sendThrowsIOExceptionOnWrite");
        Object val = "test";
        String exptectedResult = String.format("\"%s\"", val);
        MockedResponse mockedResponse = MockedResponse.create().withIOExceptionOnWrite().build();
        HttpServletResponse response = mockedResponse.getMockInstance();
        MochaResponse instance = new MochaResponse(response);
        instance.send(val);
        assertTrue(response.isCommitted());
        assertEquals(exptectedResult, new String(mockedResponse.bos.toByteArray()));
    }

    @Test(expected = IOException.class)
    public void testSendThrowsIOExceptionOnOpen() throws Exception {
        System.out.println("sendThrowsIOExceptionOnOpen");
        Object val = "test";
        String exptectedResult = String.format("\"%s\"", val);
        MockedResponse mockedResponse = MockedResponse.create().withIOExceptionOnOpen().build();
        HttpServletResponse response = mockedResponse.getMockInstance();
        MochaResponse instance = new MochaResponse(response);
        instance.send(val);
        assertTrue(response.isCommitted());
        assertEquals(exptectedResult, new String(mockedResponse.bos.toByteArray()));
    }

    @Test
    public void testSendWithSerializationToString() throws Exception {
        System.out.println("sendWithSerializationToString");
        Object val = "test";
        String exptectedResult = (String) val;
        MockedResponse mockedResponse = MockedResponse.create().build();
        HttpServletResponse response = mockedResponse.getMockInstance();
        HttpServletRequest request = MockedRequest.create()
                .withMimeType(MediaType.TEXT_PLAIN)
                .withMethod(RestEngine.OPTIONS_METHOD)
                .withTestBody("")
                .build().getMockInstance();
        MochaResponse instance = new MochaResponse(response, new MochaRequest(request));
        instance.send(val);
        assertTrue(response.isCommitted());
        assertEquals(exptectedResult, new String(mockedResponse.bos.toByteArray()));
    }

    @Test
    public void testSendWithoutAcceptType() throws Exception {
        System.out.println("sendWithSerializationToString");
        Object val = "test";
        String exptectedResult = (String) val;
        MockedResponse mockedResponse = MockedResponse.create().build();
        HttpServletResponse response = mockedResponse.getMockInstance();
        HttpServletRequest request = MockedRequest.create()
                .withMimeType(null)
                .withMethod(RestEngine.OPTIONS_METHOD)
                .withTestBody("")
                .build().getMockInstance();
        MochaResponse instance = new MochaResponse(response, new MochaRequest(request));
        instance.send(val);
        assertTrue(response.isCommitted());
        assertEquals(exptectedResult, new String(mockedResponse.bos.toByteArray()));
    }

    @Test
    public void testSendWithMultipleAcceptType() throws Exception {
        System.out.println("sendWithSerializationToString");
        Object val = "test";
        String exptectedResult = String.format("\"%s\"", val);
        MockedResponse mockedResponse = MockedResponse.create().build();
        HttpServletResponse response = mockedResponse.getMockInstance();
        HttpServletRequest request = MockedRequest.create()
                .withAcceptType(String.join(",", new String[]{MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML}))
                .withMethod(RestEngine.POST_METHOD)
                .withTestBody("")
                .build().getMockInstance();
        MochaResponse instance = new MochaResponse(response, new MochaRequest(request));
        instance.send(val);
        assertTrue(response.isCommitted());
        assertEquals(exptectedResult, new String(mockedResponse.bos.toByteArray()));
    }
}
