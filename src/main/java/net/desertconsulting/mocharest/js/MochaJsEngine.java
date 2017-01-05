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

import java.io.Reader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.desertconsulting.mocharest.RestEngine;
import net.desertconsulting.mocharest.RestEngineImpl;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaJsEngine {

    private final ScriptEngine scriptEngine;
    private final RestEngineImpl restEngine;
    
    public final static String MOCHA_RESTENGINE_GLOBALNAME = "$mr";

    public MochaJsEngine(ServletContext context) throws ScriptException {

        restEngine = new RestEngineImpl(context);

        scriptEngine = new ScriptEngineManager().getEngineByName("js");

        scriptEngine.put(MOCHA_RESTENGINE_GLOBALNAME, (RestEngine) restEngine);

        // nodejs-style module loading
        scriptEngine.put("_module_cache", "{}");
        scriptEngine.put("require", scriptEngine.eval("function require(path) {"
                + "var module;"
                + "if(_module_cache[path]) {"
                + " module = _module_cache[path];"
                + "} else {"
                + " module = {exports:null};"
                + " loadWithNewGlobal({"
                + "  name: path"
                + "  , script:'module=arguments[0];' + $mr.getFileContent(path)"
                + " }, module); "
                + " _module_cache[path] = module;"
                + "}"
                + "return module.exports; "
                + "}"));
        
        //jQuery.Deferred-like interface
        scriptEngine.eval("var scope = new JavaImporter(Packages.net.desertconsulting.mocharest.js);\n"
                + "Deferred = scope.Deferred;");
    }

    public Object eval(Reader reader) throws ScriptException {
        return scriptEngine.eval(reader);
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) {
        restEngine.handle(request, response);
    }

    public RestEngineImpl getRestEngine() {
        return restEngine;
    }
}
