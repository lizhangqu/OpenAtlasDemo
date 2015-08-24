/**
 * OpenAtlasForAndroid Project
 * The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author BunnyBlue
 **/
package com.openatlas.dexopt;

import android.os.Build;

import com.openatlas.framework.AtlasConfig;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class InitExecutor {
    static final Logger log;
    private static boolean sDexOptLoaded;
    static boolean isART = false;
    private static Map<String, String> ABI_TO_INSTRUCTION_SET_MAP = new HashMap<String, String>();
    static String defaultInstruction;


    static {

        String vm = System.getProperty("java.vm.version");//. If ART is in use, the property's value is "2.0.0" or higher.
        isART = Character.getNumericValue(vm.charAt(0)) >= 2;


        ABI_TO_INSTRUCTION_SET_MAP.put("armeabi", "arm");
        ABI_TO_INSTRUCTION_SET_MAP.put("armeabi-v7a", "arm");
        ABI_TO_INSTRUCTION_SET_MAP.put("mips", "mips");
        ABI_TO_INSTRUCTION_SET_MAP.put("mips64", "mips64");
        ABI_TO_INSTRUCTION_SET_MAP.put("x86", "x86");
        ABI_TO_INSTRUCTION_SET_MAP.put("x86_64", "x86_64");
        ABI_TO_INSTRUCTION_SET_MAP.put("arm64-v8a", "arm64");
        defaultInstruction = ABI_TO_INSTRUCTION_SET_MAP.get(Build.CPU_ABI);
        log = LoggerFactory.getInstance("InitExecutor");
        ABI_TO_INSTRUCTION_SET_MAP.clear();
        ABI_TO_INSTRUCTION_SET_MAP = null;
        sDexOptLoaded = false;
        try {
            System.loadLibrary("dexopt");
            sDexOptLoaded = true;
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("JniMissingFunction")
    private static native void dexopt(String srcZipPath, String oDexFilePath, boolean runtime, String defaultInstruction);


    /****
     * 在低于Android 4.4的系统上调用dexopt进行优化Bundle
     ****/
    public static boolean optDexFile(String srcDexPath, String oDexFilePath) {
        try {
            if (sDexOptLoaded) {
                if (isART&& AtlasConfig.optART) {
                    dexopt(srcDexPath, oDexFilePath, true, defaultInstruction);
                } else {
                    dexopt(srcDexPath, oDexFilePath, false, "");
                }


                return true;
            }
        } catch (Throwable e) {
            log.error("Exception while try to call native dexopt >>>", e);
        }
        return false;
    }
}
