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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import mockit.Mock;
import mockit.MockUp;

/**
 *
 * @author Patrizio Bruno {@literal <desertconsulting@gmail.com>}
 */
public class MockedResponse extends MockUp<HttpServletResponse> {

    public boolean committed = false;
    public ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private final boolean throwIOExceptionOnOpen;
    private final boolean throwIOExceptionOnWrite;
    private final boolean throwIOExceptionOnSendError;
    private int statusCode;
    private String statusMessage;
    private String contentType;

    public MockedResponse(boolean throwIOExceptionOnOpen, boolean throwIOExceptionOnWrite, boolean throwIOExceptionOnSendError) {
        this.throwIOExceptionOnOpen = throwIOExceptionOnOpen;
        this.throwIOExceptionOnWrite = throwIOExceptionOnWrite;
        this.throwIOExceptionOnSendError = throwIOExceptionOnSendError;
    }

    public static Builder create() {
        return new Builder();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    @Mock
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Mock
    public String getContentType() {
        return contentType;
    }

    @Mock
    public boolean isCommitted() {
        return committed;
    }

    @Mock
    public void sendError(int statusCode, String message) throws IOException {
        if (this.throwIOExceptionOnSendError) {
            throw new IOException("sendError");
        }
        this.statusCode = statusCode;
        this.statusMessage = message;
    }

    @Mock
    public ServletOutputStream getOutputStream() throws IOException {

        if (throwIOExceptionOnOpen) {
            throw new IOException("open");
        }

        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) throws IOException {

                if (throwIOExceptionOnWrite) {
                    throw new IOException("write");
                }

                if (!committed) {
                    committed = true;
                }
                bos.write(b);
            }
        };
    }

    public static class Builder {

        private boolean throwIOExceptionOnOpen;
        private boolean throwIOExceptionOnWrite;
        private boolean throwIOExceptionOnSendError;

        public Builder withIOExceptionOnOpen() {
            this.throwIOExceptionOnOpen = true;
            return this;
        }

        public Builder withIOExceptionOnWrite() {
            this.throwIOExceptionOnWrite = true;
            return this;
        }

        public Builder withIOExceptionOnSendError() {
            this.throwIOExceptionOnSendError = true;
            return this;
        }

        public MockedResponse build() {
            return new MockedResponse(throwIOExceptionOnOpen, throwIOExceptionOnWrite, throwIOExceptionOnSendError);
        }
    }
}
