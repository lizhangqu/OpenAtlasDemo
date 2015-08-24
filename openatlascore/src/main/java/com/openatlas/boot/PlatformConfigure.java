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
package com.openatlas.boot;


/**
 * App  Platform configuration
 * @author BunnyBlue
 *
 */
public class PlatformConfigure {
    /****闪屏activity****/
    public static final String BOOT_ACTIVITY = "com.openAtlas.welcome.Welcome";
    public static final String BOOT_ACTIVITY_DEFAULT = "com.openAtlas.launcher.welcome";
    public static final String ACTION_BROADCAST_BUNDLES_INSTALLED = "com.openAtlas.action.BUNDLES_INSTALLED";
    public static final String ATLAS_APP_DIRECTORY = "com.openAtlas.AppDirectory";
    public static final String INSTALL_LOACTION = "com.openAtlas.storage";
    public static final String COM_OPENATLAS_DEBUG_BUNDLES = "com.openAtlas.debug.bundles";
    public static final String OPENATLAS_PUBLIC_KEY = "om.openAtlas.publickey";
    public static final String OPENATLAS_BASEDIR = "com.openAtlas.basedir";
    public static final String OPENATLAS_BUNDLE_LOCATION = "com.openAtlas.jars";
    public static final String OPENATLAS_CLASSLOADER_BUFFER_SIZE = "com.openAtlas.classloader.buffersize";
    public static final String OPENATLAS_LOG_LEVEL = "com.openAtlas.log.level";
    public static final String OPENATLAS_DEBUG_BUNDLES = "com.openAtlas.debug.bundles";
    public static final String OPENATLAS_DEBUG_PACKAGES = "com.openAtlas.debug.packages";
    public static final String OPENATLAS_DEBUG_SERVICES = "com.openAtlas.debug.services";
    public static final String OPENATLAS_DEBUG_CLASSLOADING = "com.openAtlas.debug.classloading";
    public static final String OPENATLAS_DEBUG = "com.openAtlas.debug";
    public static final String OPENATLAS_FRAMEWORK_PACKAGE = "com.openAtlas.framework";

    public static final String OPENATLAS_STRICT_STARTUP = "com.openAtlas.strictStartup";
    public static final String OPENATLAS_AUTO_LOAD = "com.openAtlas.auto.load";
    public static Class<?> BundleNotFoundActivity = null;


}
