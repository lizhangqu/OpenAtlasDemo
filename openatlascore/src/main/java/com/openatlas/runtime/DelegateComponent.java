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
package com.openatlas.runtime;

import android.app.Application;

import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/***Component Delegate,Bundle Component Manger*******/
public class DelegateComponent {
    static Map<String, Application> apkApplications;
    private static Map<String, PackageLite> apkPackages;
    static final Logger log;

    static {
        log = LoggerFactory.getInstance("DelegateComponent");
        apkPackages = new ConcurrentHashMap<String, PackageLite>();
        apkApplications = new HashMap<String, Application>();
    }

    /**
     * 从缓存中获取特定包名的PackageLite(可能为空)
     * @param mLocation Bundle包名
     * ******/
    public static PackageLite getPackage(String mLocation) {
        return apkPackages.get(mLocation);
    }

    /**从DelegateComponent放入新的Bundle
     * @param mLocation Bundle包名
     * @param packageLite 从Bundle解析出的包信息
     * *****/
    public static void putPackage(String mLocation, PackageLite packageLite) {
        apkPackages.put(mLocation, packageLite);
    }

    /******从DelegateComponent移除特定的Bundle*****/
    public static void removePackage(String mLocation) {
        apkPackages.remove(mLocation);
    }

    /****验证Component是否有效(指已安装)
     * @param mComponent 组件名称
     * *****/
    public static String locateComponent(String mComponent) {
        for (Entry<String, PackageLite> entry : apkPackages.entrySet()) {
            if (entry.getValue().components.contains(mComponent)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
