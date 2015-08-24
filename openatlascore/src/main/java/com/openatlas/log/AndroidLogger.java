/**
 * OpenAtlasForAndroid Project
 * The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies
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

import android.util.Log;

public class AndroidLogger implements Logger {
    private final String category;

    public AndroidLogger(String str) {
        this.category = str;
    }

    public AndroidLogger(Class<?> cls) {
        this(cls.getSimpleName());
    }

    @Override
    public void verbose(String str) {
        Log.v(category, str);
    }

    @Override
    public void debug(String str) {
        Log.d(category, str);
    }

    @Override
    public void info(String str) {
        Log.i(category, str);
    }

    @Override
    public void warn(String str) {
        Log.w(category, str);
    }

    @Override
    public void warn(String str, Throwable th) {
        Log.w(str, th);
    }

    @Override
    public void warn(StringBuffer stringBuffer, Throwable th) {
        warn(stringBuffer.toString(), th);
    }

    @Override
    public void error(String str) {
        Log.e(this.category, str);
    }

    @Override
    public void error(String str, Throwable th) {
        Log.e(this.category, str, th);
    }

    @Override
    public void error(StringBuffer stringBuffer, Throwable th) {
        error(stringBuffer.toString(), th);
    }

    @Override
    public void fatal(String str) {
        error(str);
    }

    @Override
    public void fatal(String str, Throwable th) {
        error(str, th);
    }

    @Override
    public boolean isVerboseEnabled() {
        return LoggerFactory.logLevel <= 2;
    }

    @Override
    public boolean isDebugEnabled() {
        return LoggerFactory.logLevel <= 3;
    }

    @Override
    public boolean isInfoEnabled() {
        return LoggerFactory.logLevel <= 4;
    }

    @Override
    public boolean isWarnEnabled() {
        return LoggerFactory.logLevel <= 5;
    }

    @Override
    public boolean isErrorEnabled() {
        return LoggerFactory.logLevel <= 6;
    }

    @Override
    public boolean isFatalEnabled() {
        return LoggerFactory.logLevel <= 6;
    }
}
