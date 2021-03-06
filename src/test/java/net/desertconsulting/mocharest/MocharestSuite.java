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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({net.desertconsulting.mocharest.response.ResponseSuite.class, net.desertconsulting.mocharest.BadRequestMissingQueryParamExceptionTest.class, net.desertconsulting.mocharest.request.RequestSuite.class, net.desertconsulting.mocharest.js.JsSuite.class, net.desertconsulting.mocharest.RestEngineImplTest.class, net.desertconsulting.mocharest.servlet.ServletSuite.class, net.desertconsulting.mocharest.BadRequestMissingParamExceptionTest.class})
public class MocharestSuite {
}
