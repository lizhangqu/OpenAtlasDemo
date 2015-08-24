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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.openatlas.framework.Atlas;
import com.openatlas.framework.AtlasConfig;
import com.openatlas.runtime.RuntimeVariables;

import org.osgi.framework.Bundle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class BundlesInstaller {
    private static boolean autoStart;
    private static BundlesInstaller mBundlesInstaller;

    private Application mApplication;

    private boolean isinitialized;
    private boolean isInstalled;

    BundlesInstaller() {
    }

    void init(Application application, boolean isAppPkg) {
        this.mApplication = application;


        autoStart = isAppPkg;
        this.isinitialized = true;
    }

    static synchronized BundlesInstaller getInstance() {


        if (mBundlesInstaller != null) {
            return mBundlesInstaller;
        }
        synchronized (BundlesInstaller.class) {
            if (mBundlesInstaller == null) {
                mBundlesInstaller = new BundlesInstaller();
            }

        }
        return mBundlesInstaller;
    }

    public synchronized void process(boolean installAuto, boolean updatePackageVersion) {
        if (!this.isinitialized) {
            Log.e("BundlesInstaller", "Bundle Installer not initialized yet, process abort!");
        } else if (!this.isInstalled || updatePackageVersion) {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(this.mApplication.getApplicationInfo().sourceDir);
                List<String> bundleList = fetchBundleFileList(zipFile, "lib/" + AtlasConfig.PRELOAD_DIR + "/libcom_", ".so");
                if (bundleList != null && bundleList.size() > 0 && getAvailableSize() < (((bundleList.size() * 2) * 4096) * 4096)) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RuntimeVariables.androidApplication, "Ops 可用空间不足！", 1).show();


                        }
                    });
                }
                if (installAuto) {
                    List<String> arrayList = new ArrayList<String>();
                    for (String str : bundleList) {
                        for (String replace : AtlasConfig.AUTO) {
                            if (str.contains(replace.replace(".", "_"))) {
                                arrayList.add(str);
                            }
                        }
                    }
                    processAutoStartBundles(zipFile, arrayList, this.mApplication);
                } else {
                    installDelayBundles(zipFile, bundleList, this.mApplication);
                }
                if (!updatePackageVersion) {
                    Utils.UpdatePackageVersion(this.mApplication);
                }
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            } catch (IOException e5) {
                //isInstalled = e5;

                Log.e("BundlesInstaller", "IOException while processLibsBundles >>>", e5);

                if (updatePackageVersion) {
                    this.isInstalled = true;
                }
            } catch (Throwable th2) {
                th2.printStackTrace();

                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

            }
            if (updatePackageVersion) {
                this.isInstalled = true;
            }
        }
    }

    private List<String> fetchBundleFileList(ZipFile zipFile, String prefix, String suffix) {
        List<String> arrayList = new ArrayList<String>();
        try {
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String name = ((ZipEntry) entries.nextElement()).getName();
                if (name.startsWith(prefix) && name.endsWith(suffix)) {
                    arrayList.add(name);
                }
            }
        } catch (Throwable e) {
            Log.e("BundlesInstaller", "Exception while get bundles in assets or lib", e);
        }
        return arrayList;
    }

    private long getAvailableSize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public void processAutoStartBundles(ZipFile zipFile, List<String> list, Application application) {
        for (String a : list) {
            installBundle(zipFile, a, application);
        }
        if (autoStart) {
            for (String bundle : AtlasConfig.AUTO) {
                Bundle bundle2 = Atlas.getInstance().getBundle(bundle);
                if (bundle2 != null) {
                    try {
                        bundle2.start();
                    } catch (Throwable e) {
                        Log.e("BundlesInstaller", "Could not auto start bundle: " + bundle2.getLocation(), e);
                    }
                }
            }
        }
    }

    private void installDelayBundles(ZipFile zipFile, List<String> bundleList, Application application) {
        int i = 0;
        for (String replace : AtlasConfig.DELAY) {
            String replace2 = contains(bundleList, replace.replace(".", "_"));
            if (replace2 != null && replace2.length() > 0) {
                installBundle(zipFile, replace2, application);
                bundleList.remove(replace2);
            }
        }
        for (String a : bundleList) {
            installBundle(zipFile, a, application);
        }
        if (autoStart) {
            String[] strArr = AtlasConfig.DELAY;
            int length = strArr.length;
            while (i < length) {
                Bundle bundle = Atlas.getInstance().getBundle(strArr[i]);
                if (bundle != null) {
                    try {
                        bundle.start();
                    } catch (Throwable e) {
                        Log.e("BundlesInstaller", "Could not auto start bundle: " + bundle.getLocation(), e);
                    }
                }
                i++;
            }
        }
    }

    private String contains(List<String> list, String pkgName) {
        if (list == null || pkgName == null) {
            return null;
        }
        for (String bundleName : list) {
            if (bundleName.contains(pkgName)) {
                return bundleName;
            }
        }
        return null;
    }

    private boolean installBundle(ZipFile zipFile, String packageName, Application application) {
        System.out.println("processLibsBundle entryName " + packageName);
        //this.a.a(str);
        String fileNameFromEntryName = Utils.getFileNameFromEntryName(packageName);
        String packageNameFromEntryName = Utils.getPackageNameFromEntryName(packageName);
        if (packageNameFromEntryName == null || packageNameFromEntryName.length() <= 0) {
            return false;
        }
        File file = new File(new File(application.getFilesDir().getParentFile(), "lib"), fileNameFromEntryName);
        if (Atlas.getInstance().getBundle(packageNameFromEntryName) != null) {
            return false;
        }
        try {
            if (file.exists()) {
                Atlas.getInstance().installBundle(packageNameFromEntryName, file);
            } else {
                Atlas.getInstance().installBundle(packageNameFromEntryName, zipFile.getInputStream(zipFile.getEntry(packageName)));
            }
            System.out.println("Succeed to install bundle " + packageNameFromEntryName);
            return true;
        } catch (Throwable e) {
            Log.e("BundlesInstaller", "Could not install bundle.", e);
            return false;
        }
    }
}
