/**
 * OpenAtlasForAndroid Project
 * <p>
 * The MIT License (MIT)
 * Copyright (c) 2015 Bunny Blue
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author BunnyBlue
 **/
package com.openatlas.log;

/**
 * @author BunnyBlue
 *
 */


import android.util.Log;

public class OpenAtlasLog {
    private static ILog externalLogger;

    public static void setExternalLogger(ILog iLog) {
        externalLogger = iLog;
    }

    public static void v(String tag, String msg) {
        if (externalLogger != null) {
            externalLogger.v(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (externalLogger != null) {
            externalLogger.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (externalLogger != null) {
            externalLogger.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (externalLogger != null) {
            externalLogger.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (externalLogger != null) {
            externalLogger.e(tag, msg);
        } else {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable th) {
        if (externalLogger != null) {
            externalLogger.e(tag, msg, th);
        } else {
            Log.e(tag, msg, th);
        }
    }
}