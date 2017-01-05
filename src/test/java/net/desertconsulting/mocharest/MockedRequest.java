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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import mockit.Mock;
import mockit.MockUp;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MockedRequest extends MockUp<HttpServletRequest> {

    private final String method;
    private final String path;
    private final String testBody;
    private final String query;
    private final Map<String, String[]> parameterMap;
    private final boolean throwIOExceptionOnOpen;
    private String contentType;
    private final boolean throwIOExceptionOnRead;
    private String acceptType;

    public MockedRequest(String method, String testBody, String mimeType,
            String path, String query, boolean throwIOExceptionOnOpen, boolean throwIOExceptionOnRead,
            String acceptType, String contentType) {
        this.method = method;
        this.contentType = contentType;
        this.acceptType = acceptType;
        if (mimeType != null) {
            this.contentType = this.acceptType = mimeType;
        }
        this.path = path;
        this.testBody = testBody;
        this.query = query;
        if (query != null && query.length() > 0) {
            parameterMap = Arrays.stream(query.split("&")).map((elem) -> {
                String key = elem;
                String value = null;
                if (elem.contains("=")) {
                    String[] splits = elem.split("=");
                    key = splits[0];
                    value = splits[1];
                }
                return new AbstractMap.SimpleEntry<>(key,
                        new String[]{value});
            }).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.
                    getValue(), (a, b) -> {
                List<String> vals = new ArrayList<>();
                vals.addAll(Arrays.asList(a));
                vals.addAll(Arrays.asList(b));
                return vals.toArray(new String[0]);
            }));
        } else {
            parameterMap = new HashMap<>();
        }
        this.throwIOExceptionOnOpen = throwIOExceptionOnOpen;
        this.throwIOExceptionOnRead = throwIOExceptionOnRead;
    }

    public static Builder create() {
        return new Builder();
    }

    @Mock
    public String getMethod() {
        return method;
    }

    @Mock
    public String getContentType() {
        return contentType;
    }

    @Mock
    public String getHeader(String name) {
        if ("accept".equals(name)) {
            return acceptType;
        }
        return null;
    }

    @Mock
    public String getPathInfo() {
        return path;
    }

    @Mock
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Mock
    public Enumeration<String> getParameterNames() {
        return new Vector<>(parameterMap.keySet()).elements();
    }

    @Mock
    public int getContentLength() {
        return testBody != null ? testBody.length() : 0;
    }

    @Mock
    public String getParameter(String name) {
        String[] rv = parameterMap.get(name);
        return rv != null ? rv[0] : null;
    }

    @Mock
    public ServletInputStream getInputStream() throws IOException {

        if (throwIOExceptionOnOpen) {
            throw new IOException("open");
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                testBody.getBytes());
        return new ServletInputStream() {

            ReadListener readListener;

            @Override
            public int read() throws IOException {

                if (throwIOExceptionOnRead) {
                    throw new IOException("read");
                }

                if (isFinished() && readListener != null) {
                    readListener.onAllDataRead();
                }
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() > 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                this.readListener = readListener;
                try {
                    readListener.onDataAvailable();
                } catch (IOException ex) {
                    Logger.getLogger(RestEngineImplTest.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        };
    }

    public static class Builder {

        private String method;
        private String path;
        private String mimeType;
        private String testBody;
        private String query;
        private boolean throwIOExceptionOnOpen;
        private String acceptType;
        private String contentType;
        private boolean throwIOExceptionOnRead;

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Builder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder withTestBody(String testBody) {
            this.testBody = testBody;
            return this;
        }

        public Builder withQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder withIOExceptionOnOpen() {
            this.throwIOExceptionOnOpen = true;
            return this;
        }

        public Builder withIOExceptionOnRead() {
            this.throwIOExceptionOnRead = true;
            return this;
        }

        public Builder withAcceptType(String acceptType) {
            this.acceptType = acceptType;
            return this;
        }

        public Builder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public MockedRequest build() {
            return new MockedRequest(method, testBody, mimeType, path, query
                    != null ? query : "", throwIOExceptionOnOpen, throwIOExceptionOnRead,
                    acceptType, contentType);
        }
    }
}
