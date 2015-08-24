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
package com.openatlas.framework;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.openatlas.hack.AndroidHack;
import com.openatlas.hack.OpenAtlasHacks;
import com.openatlas.log.ILog;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;
import com.openatlas.log.OpenAtlasLog;
import com.openatlas.runtime.BundleLifecycleHandler;
import com.openatlas.runtime.ClassLoadFromBundle;
import com.openatlas.runtime.ClassNotFoundInterceptorCallback;
import com.openatlas.runtime.DelegateClassLoader;
import com.openatlas.runtime.DelegateComponent;
import com.openatlas.runtime.FrameworkLifecycleHandler;
import com.openatlas.runtime.InstrumentationHook;
import com.openatlas.runtime.PackageLite;
import com.openatlas.runtime.RuntimeVariables;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class Atlas {
    protected static Atlas instance;
    static final Logger log;
    private BundleLifecycleHandler bundleLifecycleHandler;
    private FrameworkLifecycleHandler frameworkLifecycleHandler;

    static {
        log = LoggerFactory.getInstance("Atlas");
    }


    public static Atlas getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (Atlas.class) {
            if (instance == null) {
                instance = new Atlas();
            }

        }
        return instance;
    }

    public void init(Application application)
            throws Exception {
        String packageName = application.getPackageName();
        OpenAtlasHacks.defineAndVerify();
        ClassLoader classLoader = Atlas.class.getClassLoader();
        DelegateClassLoader delegateClassLoader = new DelegateClassLoader(classLoader);
        Framework.systemClassLoader = classLoader;
        RuntimeVariables.delegateClassLoader = delegateClassLoader;
        RuntimeVariables.delegateResources = initResources(application);
        RuntimeVariables.androidApplication = application;
        AndroidHack.injectClassLoader(packageName, delegateClassLoader);
        AndroidHack.injectInstrumentationHook(new InstrumentationHook(AndroidHack
                        .getInstrumentation(), application.getBaseContext()));
        injectApplication(application, packageName);
        this.bundleLifecycleHandler = new BundleLifecycleHandler();
        Framework.syncBundleListeners.add(this.bundleLifecycleHandler);
        this.frameworkLifecycleHandler = new FrameworkLifecycleHandler();
        Framework.frameworkListeners.add(this.frameworkLifecycleHandler);
        AndroidHack.hackH();
        // Framework.initialize(properties);
    }

    /**
     *@since 1.0.0
     * **/
    private Resources initResources(Application application) throws Exception {
        Resources resources = application.getResources();
        if (resources != null) {
            return resources;
        }
        log.error(" !!! Failed to get init resources.");
        return application.getPackageManager().getResourcesForApplication(application.getApplicationInfo());
    }

    public void injectApplication(Application application, String packageName)
            throws Exception {
        OpenAtlasHacks.defineAndVerify();
        AndroidHack.injectApplication(packageName, application);
    }

    public void startup(Properties properties) throws BundleException {
        Framework.startup(properties);
    }

//    public void startup() throws BundleException {
//        Framework.startup();
//    }

    public void shutdown() throws BundleException {
        Framework.shutdown(false);
    }

    public Bundle getBundle(String pkgName) {
        return Framework.getBundle(pkgName);
    }

    public Bundle getBundleOnDemand(String pkgName) {
        if (pkgName == null || pkgName.length() == 0) {
            return null;
        }
        if (Framework.getBundle(pkgName) == null) {
            ClassLoadFromBundle.checkInstallBundleAndDependency(pkgName);
        }
        return Framework.getBundle(pkgName);
    }

    public Bundle installBundle(String location, InputStream inputStream)
            throws BundleException {
        return Framework.installNewBundle(location, inputStream);
    }

    public Bundle installBundle(String location, File apkFile) throws BundleException {
        return Framework.installNewBundle(location, apkFile);
    }

    public void updateBundle(String pkgName, InputStream inputStream)
            throws BundleException {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            bundle.update(inputStream);
            return;
        }
        throw new BundleException("Could not update bundle " + pkgName
                + ", because could not find it");
    }

    public void updateBundle(String pkgName, File mBundleFile) throws BundleException {
        if (!mBundleFile.exists()) {
            throw new BundleException("file not  found" + mBundleFile.getAbsolutePath());
        }
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            bundle.update(mBundleFile);
            return;
        }
        throw new BundleException("Could not update bundle " + pkgName
                + ", because could not find it");
    }

    public boolean restoreBundle(String[] packageNames) {

        return Framework.restoreBundle(packageNames);
    }

    public void installOrUpdate(String[] packageNames, File[] bundleFiles)
            throws BundleException {
        Framework.installOrUpdate(packageNames, bundleFiles);
    }

    public void uninstallBundle(String pkgName) throws BundleException {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            BundleImpl bundleImpl = (BundleImpl) bundle;
            try {
                File archiveFile = bundleImpl.getArchive().getArchiveFile();
                if (archiveFile.canWrite()) {
                    archiveFile.delete();
                }
                bundleImpl.getArchive().purge();
                File revisionDir = bundleImpl.getArchive().getCurrentRevision()
                        .getRevisionDir();
                bundle.uninstall();
                if (revisionDir != null) {
                    Framework.deleteDirectory(revisionDir);
                    return;
                }
                return;
            } catch (Exception e) {
                log.error("uninstall bundle error: " + pkgName + e.getMessage());
                return;
            }
        }
        throw new BundleException("Could not uninstall bundle " + pkgName + ", because could not find it");
    }

    public List<Bundle> getBundles() {
        return Framework.getBundles();
    }

    public Resources getDelegateResources() {
        return RuntimeVariables.delegateResources;
    }

    public ClassLoader getDelegateClassLoader() {
        return RuntimeVariables.delegateClassLoader;
    }

    public Class<?> getComponentClass(String pkgName) throws ClassNotFoundException {
        return RuntimeVariables.delegateClassLoader.loadClass(pkgName);
    }

    public ClassLoader getBundleClassLoader(String pkgName) {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            return ((BundleImpl) bundle).getClassLoader();
        }
        return null;
    }

    public PackageLite getBundlePackageLite(String pkgName) {
        return DelegateComponent.getPackage(pkgName);
    }

    public File getBundleFile(String pkgName) {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            return ((BundleImpl) bundle).archive.getArchiveFile();
        }
        return null;
    }

    public InputStream openAssetInputStream(String packageName, String assetName)
            throws IOException {
        Bundle bundle = Framework.getBundle(packageName);
        if (bundle != null) {
            return ((BundleImpl) bundle).archive.openAssetInputStream(assetName);
        }
        return null;
    }

    public InputStream openNonAssetInputStream(String packageName, String assetName)
            throws IOException {
        Bundle bundle = Framework.getBundle(packageName);
        if (bundle != null) {
            return ((BundleImpl) bundle).archive.openNonAssetInputStream(assetName);
        }
        return null;
    }

    public void addFrameworkListener(FrameworkListener frameworkListener) {
        Framework.addFrameworkListener(frameworkListener);
    }

    public void removeFrameworkListener(FrameworkListener frameworkListener) {
        Framework.removeFrameworkListener(frameworkListener);
    }

    public void addBundleListener(BundleListener bundleListener) {
        Framework.addBundleListener(bundleListener);
    }

    public void removeBundleListener(BundleListener bundleListener) {
        Framework.removeBundleListener(bundleListener);
    }

    public void onLowMemory() {
        this.bundleLifecycleHandler.handleLowMemory();
    }

    public void enableComponent(String componentName) {
        PackageLite packageLite = DelegateComponent.getPackage(componentName);
        if (packageLite != null && packageLite.disableComponents != null) {
            for (String disableComponent : packageLite.disableComponents) {
                PackageManager packageManager = RuntimeVariables.androidApplication
                        .getPackageManager();
                ComponentName componentName2 = new ComponentName(
                        RuntimeVariables.androidApplication.getPackageName(),
                        disableComponent);
                try {
                    packageManager.setComponentEnabledSetting(componentName2, 1,
                            1);
                    log.debug("enableComponent: "
                            + componentName2.getClassName());
                } catch (Exception e) {
                    log.error("enableComponent error: "
                            + componentName2.getClassName() + e.getMessage());
                }
            }
        }
    }

    public void setLogger(ILog iLog) {
        OpenAtlasLog.setExternalLogger(iLog);
    }


    public void setClassNotFoundInterceptorCallback(
            ClassNotFoundInterceptorCallback classNotFoundInterceptorCallback) {
        Framework.setClassNotFoundCallback(classNotFoundInterceptorCallback);
    }

}
