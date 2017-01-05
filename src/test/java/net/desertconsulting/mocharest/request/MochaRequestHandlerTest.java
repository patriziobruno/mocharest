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
import java.util.regex.Pattern;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.ws.rs.core.MediaType;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.desertconsulting.mocharest.JSTestFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaRequestHandlerTest {
    @Test
    public void testGetPathPattern() throws MalformedURLException,
            ScriptException {
        System.out.println("getPathPattern");
        String testUrl = "/test/{test:int}";
        MochaRequestHandler instance = new MochaRequestHandler(testUrl);
        Pattern expResult = Pattern.compile(testUrl.replace("{test:int}",
                "([0-9]+)"));
        Pattern result = instance.getPathPattern();
        assertEquals(expResult.toString(), result.toString());
    }

    @Test
    public void testGetContentType() throws ScriptException,
            MalformedURLException {
        System.out.println("getContentType");
        ScriptObjectMirror tmp = (ScriptObjectMirror) new ScriptEngineManager().
                getEngineByName("js").eval(String.format(
                "(function(){ return {contentType:'%s'};})()",
                MediaType.APPLICATION_JSON));
        JSObject map = (JSObject) new ScriptEngineManager().
                getEngineByName("js").eval(String.format(
                "(function(){ return {contentType:'%s'};})()",
                MediaType.APPLICATION_JSON));
        MochaRequestHandler instance = new MochaRequestHandler(
                "/test/{test:int}", map);
        String expResult = MediaType.APPLICATION_JSON;
        String result = instance.getContentType();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetContentType2() throws ScriptException,
            MalformedURLException {
        System.out.println("getContentType");
        JSObject map = (ScriptObjectMirror) new ScriptEngineManager().
                getEngineByName("js").eval(String.format(
                "(function(){ return {contentType:'%s', acceptType:'%s'};})()",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON
        ));
        MochaRequestHandler instance = new MochaRequestHandler(
                "/test/{test:int}", map);
        String expResult = MediaType.APPLICATION_JSON;
        String result = instance.getContentType();
        assertEquals(expResult, result);
    }

    @Test
    public void testConstructorWithConfigAndHandler() throws
            MalformedURLException, ScriptException {
        System.out.println("constructorWithConfigAndHandler");
        String testUrl = "/test/{test:int}";
        JSObject map = (ScriptObjectMirror) new ScriptEngineManager().
                getEngineByName("js").eval(String.format(
                "(function(){ return {contentType:'%s', acceptType:'%s'};})()",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON
        ));
        MochaRequestHandler instance = new MochaRequestHandler(
                "/test/{test:int}", map, new JSTestFunction((Object _this,
                        Object... args) -> {
                    return null;
                }).getMockInstance());
        assertEquals(MediaType.APPLICATION_JSON, instance.getContentType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithIllegalHandler() throws
            MalformedURLException, ScriptException {
        System.out.println("constructorWithIllegalHandler");
        String testUrl = "/test/{test:int}";
        JSObject map = (ScriptObjectMirror) new ScriptEngineManager().
                getEngineByName("js").eval(String.format(
                "(function(){ return {contentType:'%s', acceptType:'%s'};})()",
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_JSON
        ));
        MochaRequestHandler instance = new MochaRequestHandler(
                "/test/{test:int}", map, new AbstractJSObject() {
        });
        assertEquals(MediaType.APPLICATION_JSON, instance.getContentType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithIllegalURL() throws
            MalformedURLException, ScriptException {
        System.out.println("constructorWithIllegalURL");
        String testUrl = "/test/{test:notsupported}";
        MochaRequestHandler instance = new MochaRequestHandler(testUrl);
        System.out.println("dad");
    }

    @Test
    public void testConstructorWithTemplatelessUrl() throws
            MalformedURLException, ScriptException {
        System.out.println("constructorWithIllegalURL");
        String testUrl = "/test";
        MochaRequestHandler instance = new MochaRequestHandler(testUrl);
        assertEquals(testUrl, instance.getPathPattern().toString());
    }

    @Test
    public void testConstructorWithEverySupportedPathParameterType() throws
            MalformedURLException, ScriptException {
        System.out.println("constructorWithEverySupportedPathParameterType");

        MochaRequestHandler instance;

        instance = new MochaRequestHandler("/test/{test:string}");
        assertEquals("1", instance.pathParameters.get("test").convert.apply("1"));

        instance = new MochaRequestHandler("/test/{test:int}");
        assertEquals(1, instance.pathParameters.get("test").convert.apply("1"));

        instance = new MochaRequestHandler("/test/{test:long}");
        assertEquals(1L, instance.pathParameters.get("test").convert.apply("1"));

        instance = new MochaRequestHandler("/test/{test:float}");
        assertEquals(1.1F, instance.pathParameters.get("test").convert.apply("1.1"));

        instance = new MochaRequestHandler("/test/{test:double}");
        assertEquals(1.1, instance.pathParameters.get("test").convert.apply("1.1"));

        instance = new MochaRequestHandler("/test/{test:hex}");
        assertArrayEquals(new byte[] {(byte)0x0F, (byte)0xFE, (byte)0xFF}, (byte[])instance.pathParameters.get("test").convert.apply("0FFEFF"));
    }
}
