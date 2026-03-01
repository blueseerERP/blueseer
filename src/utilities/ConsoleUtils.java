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
 * Utility class for console operations.
 * Provides common console manipulation methods.
 * @author terryva
 */
public final class ConsoleUtils {

    /** ANSI escape code to clear screen */
    private static final String ANSI_CLS = "\u001b[2J";

    /** ANSI escape code to move cursor to home position */
    private static final String ANSI_HOME = "\u001b[H";

    /** Private constructor to prevent instantiation */
    private ConsoleUtils() {
        throw new AssertionError("ConsoleUtils should not be instantiated");
    }

    /**
     * Clears the console screen using ANSI escape codes.
     */
    public static void clearScreen() {
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
    }
}