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

import jdk.nashorn.api.scripting.JSObject;
import mockit.Mock;
import mockit.MockUp;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class JSTestFunction extends MockUp<JSObject> {

    private final TestFunctionHandler handler;

    public JSTestFunction(TestFunctionHandler handler) {
        this.handler = handler;
    }

    @Mock
    public boolean isFunction() {
        return true;
    }

    @Mock
    public Object call(Object o, Object... os) {
        return handler.call(o, os);
    }

    @FunctionalInterface
    public interface TestFunctionHandler {

        Object call(Object _this, Object... args);
    }
}
