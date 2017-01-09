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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import net.desertconsulting.mocharest.js.MochaJsEngine;

/**
 * Servlet passing over requests to {@link MochaJsEngine} and handling errors.
 * 
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MochaRestServlet extends HttpServlet {

    MochaJsEngine engine;

    @Override
    public void init() throws ServletException {
        super.init();

        try (InputStreamReader stream = new InputStreamReader(
                getServletContext().getResourceAsStream(
                        "/index.js"))) {
            engine = new MochaJsEngine(getServletContext());
            engine.eval(stream);
        } catch (IOException | ScriptException ex) {
            Logger.getLogger(MochaRestServlet.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new ServletException("error initializing servlet", ex);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            engine.handle(req, resp);
        } catch (WebApplicationException ex) {

            Response response = ex.getResponse();

            try (StringWriter sw = new StringWriter();
                    PrintWriter ostream = new PrintWriter(sw)) {
                ex.printStackTrace(ostream);
                resp.sendError(response.getStatus(), sw.toString());
            }
        }
    }
}
