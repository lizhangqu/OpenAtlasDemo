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

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.openatlas.framework.Atlas;
import com.openatlas.runtime.RuntimeVariables;
import com.openatlas.util.ApkUtils;
import com.openatlas.util.PackageValidate;
import com.openatlas.util.StringUtils;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

import java.io.File;
import java.util.List;


public class SecurityFrameListener implements FrameworkListener {
    //PUT Your Public Key here
    public static final String PUBLIC_KEY = "";

    ProcessHandler mHandler;


    private class SecurityFrameAsyncTask extends AsyncTask<String, Void, Boolean> {
        final SecurityFrameListener mSecurityFrameListener;

        private SecurityFrameAsyncTask(SecurityFrameListener mSecurityFrameListener) {
            this.mSecurityFrameListener = mSecurityFrameListener;
        }

        @Override
        protected Boolean doInBackground(String... args) {
            return process(args);
        }

        @Override
        protected void onPostExecute(Boolean obj) {
            postResult(obj);
        }

        /******验证签名和公钥是否有效*******/
        protected Boolean process(String... args) {
            if (TextUtils.isEmpty(SecurityFrameListener.PUBLIC_KEY)) {
                return Boolean.valueOf(true);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
            List<Bundle> bundles = Atlas.getInstance().getBundles();
            if (bundles != null) {
                for (Bundle bundle : bundles) {
                    File bundleFile = Atlas.getInstance().getBundleFile(bundle.getLocation());
                    if (!this.mSecurityFrameListener.validBundleCert(bundleFile.getAbsolutePath())) {
                        return Boolean.valueOf(false);
                    }
                    String[] apkPublicKey = ApkUtils.getApkPublicKey(bundleFile.getAbsolutePath());
                    if (StringUtils.contains(apkPublicKey, SecurityFrameListener.PUBLIC_KEY)) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e2) {
                        }
                    } else {
                        Log.e("SecurityFrameListener", "Security check failed. " + bundle.getLocation());
                        if (apkPublicKey == null || apkPublicKey.length == 0) {
                            this.mSecurityFrameListener.storeBadSIG(bundle.getLocation() + ": NULL");
                        } else {
                            this.mSecurityFrameListener.storeBadSIG(bundle.getLocation() + ": " + apkPublicKey[0]);
                        }
                        return Boolean.valueOf(false);
                    }
                }
            }
            return Boolean.valueOf(true);
        }

        protected void postResult(Boolean bool) {
            if (bool != null && !bool.booleanValue()) {
                Toast.makeText(RuntimeVariables.androidApplication, "Public Key error，PLZ update your  public key", 1).show();
                this.mSecurityFrameListener.mHandler.sendEmptyMessageDelayed(0, 5000);
            }
        }
    }


    public static class ProcessHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            Process.killProcess(Process.myPid());
        }
    }

    public SecurityFrameListener() {
        this.mHandler = new ProcessHandler();
    }

    @Override
    @SuppressLint({"NewApi"})
    public void frameworkEvent(FrameworkEvent frameworkEvent) {
        switch (frameworkEvent.getType()) {
            case FrameworkEvent.STARTED:
                if (VERSION.SDK_INT >= 11) {
                    new SecurityFrameAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new SecurityFrameAsyncTask(this).execute();
                }
            default:
        }
    }

    /*****程序公钥不匹配******/
    private void storeBadSIG(String errPublicKey) {
        Editor edit = RuntimeVariables.androidApplication.getSharedPreferences("atlas_configs", 0).edit();
        edit.putString("BadSignature", errPublicKey);
        edit.commit();
    }

    /*****验证apk的签名是否有效****/
    private boolean validBundleCert(String archiveSourcePath) {
        PackageValidate packageValidate = new PackageValidate(archiveSourcePath);
        return packageValidate.collectCertificates();

        // return true;
    }
}