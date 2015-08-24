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
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.openatlas.android.task.Coordinator;
import com.openatlas.android.task.Coordinator.TaggedRunnable;
import com.openatlas.boot.Globals;
import com.openatlas.boot.PlatformConfigure;
import com.openatlas.bundleInfo.BundleInfoList;
import com.openatlas.bundleInfo.BundleInfoList.BundleInfo;
import com.openatlas.bundleInfo.BundleListing;
import com.openatlas.framework.Atlas;
import com.openatlas.util.ApkUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OpenAtlasInitializer {
    private static long time;
    private static boolean isAppPkg;
    private Application mApp;
    private String pkgName;

    private boolean tryInstall;

    private boolean isMiniPackage;
    private boolean init;

    static {
        time = 0;
    }

    public OpenAtlasInitializer(Application application, String packageName, Context context) {

        this.init = true;
        this.mApp = application;
        this.pkgName = packageName;

        if (application.getPackageName().equals(packageName)) {
            isAppPkg = true;
        }
    }

    public void injectApplication() {
        try {
            Atlas.getInstance().injectApplication(this.mApp, this.mApp.getPackageName());
        } catch (Exception e) {
            throw new RuntimeException("atlas inject mApplication fail" + e.getMessage());
        }
    }

    public void init() {
        time = System.currentTimeMillis();
        try {
            Atlas.getInstance().init(this.mApp);
            System.out.println("Atlas framework inited end " + this.pkgName + " " + (System.currentTimeMillis() - time) + " ms");
        } catch (Throwable e) {
            Log.e("AtlasInitializer", "Could not init atlas framework !!!", e);
            throw new RuntimeException("atlas initialization fail" + e.getMessage());
        }
    }

    public void startUp() {
        this.init = isMatchVersion();
        if (this.init) {
            killMe();
            ensureBaselineInfo();
        }
        Properties properties = new Properties();
        properties.put(PlatformConfigure.BOOT_ACTIVITY, PlatformConfigure.BOOT_ACTIVITY);
        properties.put(PlatformConfigure.COM_OPENATLAS_DEBUG_BUNDLES, "true");
        properties.put(PlatformConfigure.ATLAS_APP_DIRECTORY, this.mApp.getFilesDir().getParent());

        try {
            Field declaredField = Globals.class.getDeclaredField("sApplication");
            declaredField.setAccessible(true);
            declaredField.set(null, this.mApp);
            declaredField = Globals.class.getDeclaredField("sClassLoader");
            declaredField.setAccessible(true);
            declaredField.set(null, Atlas.getInstance().getDelegateClassLoader());
            //  this.d = new AwbDebug();
            if (this.mApp.getPackageName().equals(this.pkgName)) {
                if (verifyRumtime() || !ApkUtils.isRootSystem()) {
                    properties.put(PlatformConfigure.OPENATLAS_PUBLIC_KEY, SecurityFrameListener.PUBLIC_KEY);
                    Atlas.getInstance().addFrameworkListener(new SecurityFrameListener());
                }
                if (this.init) {
                    properties.put("osgi.init", "true");
                }
            }
            BundlesInstaller mBundlesInstaller = BundlesInstaller.getInstance();
            OptDexProcess mOptDexProcess = OptDexProcess.getInstance();
            if (this.mApp.getPackageName().equals(this.pkgName) && (this.init)) {
                mBundlesInstaller.init(this.mApp, isAppPkg);
                mOptDexProcess.init(this.mApp);
            }
            System.out.println("Atlas framework prepare starting in process " + this.pkgName + " " + (System.currentTimeMillis() - time) + " ms");
            Atlas.getInstance().setClassNotFoundInterceptorCallback(new ClassNotFoundInterceptor());
            try {
                Atlas.getInstance().startup(properties);
                installBundles(mBundlesInstaller, mOptDexProcess);
                System.out.println("Atlas framework end startUp in process " + this.pkgName + " " + (System.currentTimeMillis() - time) + " ms");
            } catch (Throwable e) {
                Log.e("AtlasInitializer", "Could not start up atlas framework !!!", e);
                throw new RuntimeException(e);
            }
        } catch (Throwable e2) {
            e2.printStackTrace();
            throw new RuntimeException("Could not set Globals !!!", e2);
        }
    }

    private void ensureBaselineInfo() {
        File file = new File(this.mApp.getFilesDir() + File.separator + "bundleBaseline" + File.separator + "baselineInfo");
        if (file.exists()) {
            file.delete();
        }
    }

    private void killMe() {
        if (!this.mApp.getPackageName().equals(this.pkgName)) {
            Process.killProcess(Process.myPid());
        }
    }

    private void installBundles(final BundlesInstaller mBundlesInstaller, final OptDexProcess mOptDexProcess) {
        if (this.mApp.getPackageName().equals(this.pkgName)) {
            if (!Utils.searchFile(this.mApp.getFilesDir().getParentFile() + "/lib", "libcom_")) {
                InstallSolutionConfig.install_when_oncreate = true;
            }
            if (InstallSolutionConfig.install_when_findclass && !initBundle()) {
                InstallSolutionConfig.install_when_oncreate = true;
                this.tryInstall = true;
            }
            if (this.init) {

                if (InstallSolutionConfig.install_when_oncreate_auto) {
                    Coordinator.postTask(new TaggedRunnable("AtlasStartup") {

                        @Override
                        public void run() {
                            mBundlesInstaller.process(true, false);
                            mOptDexProcess.processPackages(false, false);

                        }
                    });
                }
                if (InstallSolutionConfig.install_when_oncreate) {
                    Coordinator.postTask(new TaggedRunnable("AtlasStartup") {

                        @Override
                        public void run() {

                            mBundlesInstaller.process(true, false);
                            mOptDexProcess.processPackages(true, false);


                        }
                    });
                } else {
                    Utils.notifyBundleInstalled(this.mApp);
                    Utils.UpdatePackageVersion(this.mApp);
                    Utils.saveAtlasInfoBySharedPreferences(this.mApp);
                }
            } else if (!this.init) {
                if (this.tryInstall) {
                    Coordinator.postTask(new TaggedRunnable("AtlasStartup") {

                        @Override
                        public void run() {
                            mBundlesInstaller.process(false, false);
                            mOptDexProcess.processPackages(false, false);

                        }
                    });
                } else {
                    Utils.notifyBundleInstalled(this.mApp);
                }
            }

        }
    }


    private boolean initBundle() {
        ArrayList<BundleInfo> e = genBundleListInfo();
        if (e == null) {
            return false;
        }
        BundleInfoList.getInstance().init(e);
        return true;
    }

    private ArrayList<BundleInfo> genBundleListInfo() {
        BundleListing bundleListing = BundleListing.getInstance();//=BundleListing.instance().getBundleListing();
        if (bundleListing == null || bundleListing.getBundles() == null) {
            return null;
        }
        ArrayList<BundleInfo> arrayList = new ArrayList<BundleInfo>();
        for (BundleListing.Component aVar : bundleListing.getBundles()) {
            if (aVar != null) {
                BundleInfo bundleInfo = new BundleInfo();
                List<String> arrayList2 = new ArrayList<String>();
                if (aVar.getActivities() != null) {
                    arrayList2.addAll(aVar.getActivities());
                }
                if (aVar.getServices() != null) {
                    arrayList2.addAll(aVar.getServices());
                }
                if (aVar.getReceivers() != null) {
                    arrayList2.addAll(aVar.getReceivers());
                }
                if (aVar.getContentProviders() != null) {
                    arrayList2.addAll(aVar.getContentProviders());
                }
                bundleInfo.hasSO = aVar.isHasSO();
                bundleInfo.bundleName = aVar.getPkgName();
                bundleInfo.Components = arrayList2;
                bundleInfo.DependentBundles = aVar.getDependency();
                arrayList.add(bundleInfo);
            }
        }
        return arrayList;
    }

    @SuppressLint({"DefaultLocale"})
    private boolean verifyRumtime() {
        return !((Build.BRAND == null || !Build.BRAND.toLowerCase().contains("xiaomi") || Build.HARDWARE == null || !Build.HARDWARE.toLowerCase().contains("mt65")) && VERSION.SDK_INT >= 14);
    }

    private boolean isMatchVersion() {
        try {
            PackageInfo packageInfo = this.mApp.getPackageManager().getPackageInfo(this.mApp.getPackageName(), 0);
            SharedPreferences sharedPreferences = this.mApp.getSharedPreferences("atlas_configs", 0);
            int last_version_code = sharedPreferences.getInt("last_version_code", 0);
            CharSequence last_version_name = sharedPreferences.getString("last_version_name", "");
            SharedPreferences sharedPreferences2 = this.mApp.getSharedPreferences("atlas_configs", 0);
            CharSequence string2 = sharedPreferences2.getString("isMiniPackage", "");
            this.isMiniPackage = false;
            System.out.println("resetForOverrideInstall = " + this.isMiniPackage);
            if (TextUtils.isEmpty(string2) || this.isMiniPackage) {
                Editor edit = sharedPreferences2.edit();
                edit.clear();
                edit.putString("isMiniPackage", "false");
                edit.commit();
            }
            return packageInfo.versionCode > last_version_code || ((packageInfo.versionCode == last_version_code && !TextUtils.equals(Globals.getInstalledVersionName(), last_version_name)) || this.isMiniPackage);
        } catch (Throwable e) {
            Log.e("AtlasInitializer", "Error to get PackageInfo >>>", e);
            throw new RuntimeException(e);
        }
    }
}