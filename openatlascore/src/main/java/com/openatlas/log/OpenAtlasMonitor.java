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


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class OpenAtlasMonitor implements IMonitor {
    public static final int BUNDLE_INSTALL_FAIL = -1;
    public static final int DELETE_STORAGE_FAIL = -2;
    public static final int RESOURCES_FAIL = -4;
    public static final int WRITE_META_FAIL = -3;
    private static IMonitor externalMonitor;
    private static OpenAtlasMonitor singleton;

    public static OpenAtlasMonitor getInstance() {

        if (singleton == null) {
            singleton = new OpenAtlasMonitor();
        }
        return singleton;
    }

    public static void setExternalMonitor(IMonitor iMonitor) {

        externalMonitor = iMonitor;
    }


    public void trace(String monitorType, String bundleName, String voidTag, String info, Throwable th) {
        if (th != null) {
            Writer stringWriter = new StringWriter();
            th.printStackTrace(new PrintWriter(stringWriter));
            String stringWriter2 = stringWriter.toString();
            Map hashMap = new HashMap();
            hashMap.put("errorStr", stringWriter2);
            trace(monitorType, bundleName, voidTag, info + " " + hashMap.toString());
            return;
        }
        trace(monitorType, bundleName, voidTag, info);
    }

    public void trace(Integer monitorType, String bundleName, String voidTag, String info, Throwable th) {

        trace(monitorType.toString(), bundleName, voidTag, info, th);
    }

    @Override
    public void trace(String monitorType, String bundleName, String voidTag, String info) {

        if (externalMonitor != null) {
            externalMonitor.trace(monitorType, bundleName, voidTag, info);
        }
    }

    @Override
    public void trace(Integer monitorType, String bundleName, String voidTag, String info) {

        if (externalMonitor != null) {
            externalMonitor.trace(monitorType, bundleName, voidTag, info);
        }
    }
}