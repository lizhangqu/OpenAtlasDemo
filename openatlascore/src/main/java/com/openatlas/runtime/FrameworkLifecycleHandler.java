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
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.openatlas.framework.Framework;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;
import com.openatlas.util.StringUtils;

import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

public class FrameworkLifecycleHandler implements FrameworkListener {
    static final Logger log;

    static {
        log = LoggerFactory.getInstance("FrameworkLifecycleHandler");
    }

    @Override
    public void frameworkEvent(FrameworkEvent frameworkEvent) {
        switch (frameworkEvent.getType()) {
            case FrameworkEvent.STARTING:
                starting();
            case FrameworkEvent.STARTED:
                started();
            default:
        }
    }

    private void starting() {
        Bundle bundle;
        long currentTimeMillis = System.currentTimeMillis();
        try {
            bundle = RuntimeVariables.androidApplication.getPackageManager().getApplicationInfo(RuntimeVariables.androidApplication.getPackageName(), 128).metaData;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            bundle = null;
        }
        if (bundle != null) {
            String string = bundle.getString("application");
            if (StringUtils.isNotEmpty(string)) {
                if (log.isDebugEnabled()) {
                    log.debug("Found extra application: " + string);
                }
                String[] split = StringUtils.split(string, ",");
                if (split == null || split.length == 0) {
                    split = new String[]{string};
                }
                for (String str : split) {
                    try {
                        Application newApplication = BundleLifecycleHandler.newApplication(str, Framework.getSystemClassLoader());
                        newApplication.onCreate();
                        DelegateComponent.apkApplications.put("system:" + str, newApplication);
                    } catch (Throwable e2) {
                        log.error("Error to start application", e2);
                    }
                }
            }
        }
        log.info("starting() spend " + (System.currentTimeMillis() - currentTimeMillis) + " milliseconds");
    }

    private void started() {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            DelegateResources.newDelegateResources(
                    RuntimeVariables.androidApplication,
                    RuntimeVariables.delegateResources, null);
        } catch (Throwable e) {
            log.error("Failed to newDelegateResources", e);
        }
        log.info("started() spend "
                + (System.currentTimeMillis() - currentTimeMillis)
                + " milliseconds");
    }
}
