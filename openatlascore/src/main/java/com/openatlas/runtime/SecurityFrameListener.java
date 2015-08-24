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

import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.openatlas.boot.PlatformConfigure;
import com.openatlas.framework.Atlas;
import com.openatlas.framework.Framework;
import com.openatlas.util.ApkUtils;
import com.openatlas.util.StringUtils;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

import java.util.List;

public class SecurityFrameListener implements FrameworkListener {
    static final String TAG = "SecurityFrameListener";
    ShutdownProcessHandler shutdownProcessHandler;

    private class SecurityTask extends AsyncTask<String, Void, Boolean> {
        final String PUBLIC_KEY;

        private SecurityTask() {
            this.PUBLIC_KEY = Framework
                    .getProperty(PlatformConfigure.OPENATLAS_PUBLIC_KEY);
        }

        @Override
        protected Boolean doInBackground(String... strArr) {
            if (this.PUBLIC_KEY == null || this.PUBLIC_KEY.isEmpty()) {
                return Boolean.valueOf(true);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            List<Bundle> bundles = Atlas.getInstance().getBundles();
            if (bundles != null) {
                for (Bundle bundle : bundles) {
                    if (StringUtils.contains(
                            ApkUtils.getApkPublicKey(Atlas.getInstance()
                                    .getBundleFile(bundle.getLocation())
                                    .getAbsolutePath()), this.PUBLIC_KEY)) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e2) {
                        }
                    } else {
                        Log.e(SecurityFrameListener.TAG,
                                "Security check failed. "
                                        + bundle.getLocation());
                        return Boolean.valueOf(false);
                    }
                }
            }
            return Boolean.valueOf(true);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool != null && !bool.booleanValue()) {
                Toast.makeText(
                        RuntimeVariables.androidApplication,
                        "检测到安装文件被损坏，请卸载后重新安装！",
                        1).show();
                SecurityFrameListener.this.shutdownProcessHandler
                        .sendEmptyMessageDelayed(0, 5000);
            }
        }
    }

    public class ShutdownProcessHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            Process.killProcess(Process.myPid());
        }
    }

    public SecurityFrameListener() {
        this.shutdownProcessHandler = new ShutdownProcessHandler();
    }

    @Override
    public void frameworkEvent(FrameworkEvent frameworkEvent) {
        switch (frameworkEvent.getType()) {
            case 1 /* 1 */:
                if (VERSION.SDK_INT >= 11) {
                    new SecurityTask().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new SecurityTask().execute();
                }
            default:
        }
    }
}
