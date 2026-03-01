/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package utilities;

/**
 * Configuration constants for the BlueSeer server.
 * Centralizes all server-related configuration values.
 * @author terryva
 */
public final class ServerConfig {

    /** Maximum number of threads in the server thread pool */
    public static final int MAX_THREADS = 500;

    /** Output buffer size in bytes */
    public static final int OUTPUT_BUFFER_SIZE = 32768;

    /** Request header size in bytes */
    public static final int REQUEST_HEADER_SIZE = 8192;

    /** Response header size in bytes */
    public static final int RESPONSE_HEADER_SIZE = 8192;

    /** Idle timeout in milliseconds */
    public static final int IDLE_TIMEOUT_MS = 30000;

    /** Default HTTP port */
    public static final int DEFAULT_HTTP_PORT = 8088;

    /** Default HTTPS port */
    public static final int DEFAULT_HTTPS_PORT = 8443;

    /** Private constructor to prevent instantiation */
    private ServerConfig() {
        throw new AssertionError("ServerConfig should not be instantiated");
    }
}