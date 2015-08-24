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
 * @author BunnyBlue
 */
/**
 * @author BunnyBlue
 */
package com.openatlas.android.initializer;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.openatlas.boot.PlatformConfigure;
import com.openatlas.framework.Atlas;
import com.openatlas.framework.AtlasConfig;
import com.openatlas.framework.BundleImpl;
import com.openatlas.framework.bundlestorage.BundleArchiveRevision.DexLoadException;

import org.osgi.framework.Bundle;

public class OptDexProcess {
    private static OptDexProcess mOptDexProcess;
    private Application mApplication;
    private boolean isInitialized;
    private boolean isExecuted;

    private OptDexProcess() {
    }

    public static synchronized OptDexProcess getInstance() {
        if (mOptDexProcess != null) {

            return mOptDexProcess;
        }
        synchronized (OptDexProcess.class) {
            if (mOptDexProcess == null) {
                mOptDexProcess = new OptDexProcess();
            }

        }
        return mOptDexProcess;
    }

    /*** 初始化OptDexProcess ***/
    void init(Application application) {
        this.mApplication = application;
        this.isInitialized = true;
    }

    /**
     * 处理Bundles
     *
     * @param optAuto
     *            是否只处理安装方式为AUTO的Bundle
     * @param notifyResult
     *            通知UI安装结果
     * ******/
    public synchronized void processPackages(boolean optAuto, boolean notifyResult) {
        if (!this.isInitialized) {
            Log.e("OptDexProcess", "Bundle Installer not initialized yet, process abort!");
        } else if (!this.isExecuted || notifyResult) {
            long currentTimeMillis;
            if (optAuto) {
                currentTimeMillis = System.currentTimeMillis();
                optAUTODex();
                if (!notifyResult) {
                    finishInstalled();
                }
                Log.e("debug", "dexopt auto start bundles cost time = " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
            } else {
                currentTimeMillis = System.currentTimeMillis();
                optStoreDex();
                Log.e("debug", "dexopt bundles not delayed cost time = " + (System.currentTimeMillis() - currentTimeMillis) + " ms");

                if (!notifyResult) {
                    finishInstalled();
                }
                currentTimeMillis = System.currentTimeMillis();
                getInstance().optStoreDex2();
                Log.e("debug", "dexopt delayed bundles cost time = " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
            }
            if (!notifyResult) {
                this.isExecuted = true;
            }
        }
    }

    /** 通知UI安装完成 **/
    private void finishInstalled() {
        Utils.saveAtlasInfoBySharedPreferences(this.mApplication);
        System.setProperty("BUNDLES_INSTALLED", "true");
        this.mApplication.sendBroadcast(new Intent(PlatformConfigure.ACTION_BROADCAST_BUNDLES_INSTALLED));
    }

    /**** 对已安装并且安装方式为STORE的Bundle进行dexopt操作 ****/
    private void optStoreDex() {
        for (Bundle bundle : Atlas.getInstance().getBundles()) {
            if (!(bundle == null || contains(AtlasConfig.STORE, bundle.getLocation()))) {
                try {
                    ((BundleImpl) bundle).optDexFile();
                } catch (Throwable e) {
                    if (e instanceof DexLoadException) {
                        throw ((RuntimeException) e);
                    }
                    Log.e("OptDexProcess", "Error while dexopt >>>", e);
                }
            }
        }
    }

    /**** 对全部安装方式为Store的Bundle进行dexopt操作 ***/
    private void optStoreDex2() {
        for (String bundle : AtlasConfig.STORE) {
            Bundle bundle2 = Atlas.getInstance().getBundle(bundle);
            if (bundle2 != null) {
                try {
                    ((BundleImpl) bundle2).optDexFile();
                } catch (Throwable e) {
                    if (e instanceof DexLoadException) {
                        throw ((RuntimeException) e);
                    }
                    Log.e("OptDexProcess", "Error while dexopt >>>", e);
                }
            }
        }
    }

    /** 对随宿主启动的插件进行dexopt操作 ****/
    private void optAUTODex() {
        for (String bundleName : AtlasConfig.AUTO) {
            Bundle bundle = Atlas.getInstance().getBundle(bundleName);
            if (bundle != null) {
                try {
                    ((BundleImpl) bundle).optDexFile();
                } catch (Throwable e) {
                    if (e instanceof DexLoadException) {
                        throw ((RuntimeException) e);
                    }
                    Log.e("OptDexProcess", "Error while dexopt >>>", e);
                }
            }
        }
    }

    private boolean contains(String[] bundleNames, String location) {
        if (bundleNames == null || location == null) {
            return false;
        }
        for (String bundleName : bundleNames) {
            if (bundleName != null && bundleName.equals(location)) {
                return true;
            }
        }
        return false;
    }
}