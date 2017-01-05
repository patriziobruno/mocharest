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
package net.desertconsulting.mocharest.js;

import jdk.nashorn.api.scripting.AbstractJSObject;
import net.desertconsulting.mocharest.JSTestFunction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class DeferredTest {
    @Test
    public void testResolve() {
        System.out.println("resolve");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.done(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
        instance.resolve(expextedResult);
    }

    @Test
    public void testResolveWithExceptionInHandler() {
        System.out.println("resolveWithExceptionInHandler");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.done(new JSTestFunction((Object _this, Object... args) -> {
            throw new IllegalArgumentException((String) expextedResult);
        }).getMockInstance()).fail(new JSTestFunction((Object _this,
                Object... args) -> {
            assertEquals(expextedResult, ((Exception) args[0]).getMessage());
            return null;
        }).getMockInstance());
        instance.resolve(expextedResult);
    }
    @Test
    public void testResolveOnADeferred() {
        System.out.println("resolve");
        Object expextedResult = "test";
        Object expextedResult1 = "test1";
        Deferred instance = new Deferred();
        instance.done(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            Deferred rv = new Deferred();
            rv.resolve(expextedResult1);
            return rv;
        }).getMockInstance())
                .done(new JSTestFunction((Object _this1, Object... args1) -> {
                    assertEquals(expextedResult1, args1[0]);
                    return null;
                }).getMockInstance());
        instance.resolve(expextedResult);
    }

    @Test
    public void testResolveOnAHandledDeferred() {
        System.out.println("resolve");
        Object expextedResult = "test";
        Object expextedResult1 = "test1";
        Object expextedResult2 = "test2";
        Deferred instance = new Deferred();
        instance.done(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            Deferred rv = new Deferred();
            rv.done(new JSTestFunction((Object _this1, Object... args1) -> {
                assertEquals(expextedResult1, args1[0]);
                return null;
            }).getMockInstance());
            rv.resolve(expextedResult1);
            return rv;
        }).getMockInstance())
                .done(new JSTestFunction((Object _this2, Object... args2) -> {
                    assertEquals(expextedResult2, args2[0]);
                    return null;
                }).getMockInstance());
        instance.resolve(expextedResult);
    }

    @Test
    public void testDoneAfterResolve() {
        System.out.println("doneAfterResolve");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.resolve(expextedResult);
        instance.done(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
    }

    @Test
    public void testAlwaysAfterResolve() {
        System.out.println("alwaysAfterResolve");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.resolve(expextedResult);
        instance.always(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
    }

    @Test
    public void testAlwaysAfterReject() {
        System.out.println("alwaysAfterReject");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.reject(expextedResult);
        instance.always(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
    }

    @Test
    public void testReject() {
        System.out.println("reject");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.fail(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
        instance.reject(expextedResult);
    }

    @Test
    public void testFailAfterReject() {
        System.out.println("failAfterReject");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.reject(expextedResult);
        instance.fail(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
    }

    @Test
    public void testAlwaysWithReject() {
        System.out.println("alwaysWithReject");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.always(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
        instance.reject(expextedResult);
    }

    @Test
    public void testAlwaysWithResolve() {
        System.out.println("alwaysWithResolve");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.always(new JSTestFunction((Object _this, Object... args) -> {
            assertEquals(expextedResult, args[0]);
            return null;
        }).getMockInstance());
        instance.resolve(expextedResult);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoneWithIllegalArgument() {
        System.out.println("doneWithIllegalArgument");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.done(new AbstractJSObject() {
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailWithIllegalArgument() {
        System.out.println("failWithIllegalArgument");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.fail(new AbstractJSObject() {
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAlwaysWithIllegalArgument() {
        System.out.println("alwaysWithIllegalArgument");
        Object expextedResult = "test";
        Deferred instance = new Deferred();
        instance.always(new AbstractJSObject() {
        });
    }
}
