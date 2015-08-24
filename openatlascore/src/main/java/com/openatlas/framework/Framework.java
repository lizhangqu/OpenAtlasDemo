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

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;

import com.openatlas.boot.PlatformConfigure;
import com.openatlas.framework.bundlestorage.BundleArchive;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;
import com.openatlas.log.OpenAtlasMonitor;
import com.openatlas.runtime.ClassNotFoundInterceptorCallback;
import com.openatlas.runtime.RuntimeVariables;
import com.openatlas.util.BundleLock;
import com.openatlas.util.FileUtils;
import com.openatlas.util.OpenAtlasFileLock;
import com.openatlas.util.OpenAtlasUtils;
import com.openatlas.util.StringUtils;

import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public final class Framework {
    private static final AdminPermission ADMIN_PERMISSION = new AdminPermission();
    private static String BASEDIR = null;
    private static String BUNDLE_LOCATION = null;
    static int CLASSLOADER_BUFFER_SIZE = 0;
    static boolean DEBUG_BUNDLES = true;
    static boolean DEBUG_CLASSLOADING = true;
    static boolean DEBUG_PACKAGES = true;
    static boolean DEBUG_SERVICES = true;
    static final String FRAMEWORK_VERSION = "1.0.0";
    private static final String DOWN_GRADE_FILE = "down_grade_list";
    static int LOG_LEVEL;
    static String STORAGE_LOCATION;
    @SuppressWarnings("unused")
    private static boolean STRICT_STARTUP;
    static List<BundleListener> bundleListeners = new ArrayList<BundleListener>();
    static Map<String, Bundle> bundles = new ConcurrentHashMap<String, Bundle>();
    private static ClassNotFoundInterceptorCallback classNotFoundCallback;
    static Map<String, List<ServiceReference>> classes_services = new HashMap<String, List<ServiceReference>>();
    static Map<Package, Package> exportedPackages = new ConcurrentHashMap<Package, Package>();
    static List<FrameworkListener> frameworkListeners = new ArrayList<FrameworkListener>();
    static boolean frameworkStartupShutdown = false;
    static int initStartlevel = 1;
    static final Logger log = LoggerFactory.getInstance("Framework");
    static boolean mIsEnableBundleInstallWhenFindClass = false;
    static Map<String, String> mMapForComAndBundles = new HashMap<String, String>();
    static Properties properties;
    static boolean restart = false;
    static List<ServiceListenerEntry> serviceListeners = new ArrayList<ServiceListenerEntry>();
    static List<ServiceReference> services = new ArrayList<ServiceReference>();
    static int startlevel = 0;
    static List<BundleListener> syncBundleListeners = new ArrayList<BundleListener>();
    static SystemBundle systemBundle;
    static ClassLoader systemClassLoader;
    static List<String> writeAheads = new ArrayList<String>();


    static final class ServiceListenerEntry implements EventListener {
        final Filter filter;
        final ServiceListener listener;

        ServiceListenerEntry(ServiceListener serviceListener, String str) throws InvalidSyntaxException {
            this.listener = serviceListener;
            this.filter = str == null ? null : RFC1960Filter.fromString(str);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ServiceListenerEntry)) {
                return false;
            }
            return this.listener.equals(((ServiceListenerEntry) obj).listener);
        }

        @Override
        public int hashCode() {
            return (this.filter != null ? this.filter.hashCode() >> 8 : 0) + this.listener.hashCode();
        }

        @Override
        public String toString() {
            return this.listener + " " + this.filter;
        }
    }

    private static final class SystemBundle implements Bundle, PackageAdmin, StartLevel {
        private final Dictionary<String, String> props;
        private final ServiceReference[] registeredServices;
        int state;

        class ShutdownThread extends Thread {
            final boolean restart;

            ShutdownThread(boolean restart) {
                this.restart = restart;
            }

            @Override
            public void run() {
                Framework.shutdown(this.restart);
            }
        }

        class UpdateLevelThread extends Thread {
            final int targetLevel;

            UpdateLevelThread(int i) {
                this.targetLevel = i;
            }

            @Override
            public void run() {
                List bundles = Framework.getBundles();
                SystemBundle.this.setLevel((Bundle[]) bundles.toArray(new Bundle[bundles.size()]), this.targetLevel, false);
                Framework.notifyFrameworkListeners(8, Framework.systemBundle, null);
                Framework.storeMetadata();
            }
        }

        // TODO this is Component old version impl
        class RefreshBundlesThread extends Thread {
            final Bundle[] bundleArray;

            RefreshBundlesThread(Bundle[] bundleArr) {
                this.bundleArray = bundleArr;
            }

            @Override
            public void run() {
                synchronized (exportedPackages) {
                    try {
                        List<?> bundles;
                        Bundle[] bundleArr;
                        int i;
                        BundleImpl bundleImpl;
                        if (this.bundleArray == null) {
                            bundles = Framework.getBundles();
                            bundleArr = bundles.toArray(new Bundle[bundles.size()]);
                        } else {
                            bundleArr = this.bundleArray;
                        }
                        List<Bundle> arrayList = new ArrayList<Bundle>(bundleArr.length);
                        for (i = 0; i < bundleArr.length; i++) {
                            if (bundleArr[i] != systemBundle) {
                                bundleImpl = (BundleImpl) bundleArr[i];
                                if (bundleImpl.classloader == null || bundleImpl.classloader.originalExporter != null) {
                                    arrayList.add(bundleArr[i]);
                                }
                            }
                        }
                        if (arrayList.isEmpty()) {
                            return;
                        }
                        int i2;
                        if (DEBUG_PACKAGES && log.isDebugEnabled()) {
                            log.debug("REFRESHING PACKAGES FROM BUNDLES " + arrayList);
                        }
                        Set hashSet = new HashSet();
                        while (!arrayList.isEmpty()) {
                            bundleImpl = (BundleImpl) arrayList.remove(0);
                            if (!hashSet.contains(bundleImpl)) {
                                ExportedPackage[] access$100 = SystemBundle.this.getExportedPackages(bundleImpl, true);
                                if (access$100 != null) {
                                    for (ExportedPackage exportedPackage : access$100) {
                                        Package packageR = (Package) exportedPackage;
                                        if (packageR.importingBundles != null) {
                                            arrayList.addAll(Arrays.asList(packageR.importingBundles.toArray(new Bundle[packageR.importingBundles.size()])));
                                        }
                                    }
                                }
                                if (bundleImpl.classloader != null) {
                                    hashSet.add(bundleImpl);
                                }
                            }
                        }
                        if (DEBUG_PACKAGES && log.isDebugEnabled()) {
                            log.debug("UPDATE GRAPH IS " + hashSet);
                        }
                        Bundle[] bundleArr2 = new Bundle[hashSet.size()];
                        i = -1;
                        bundles = Framework.getBundles();
                        Bundle[] bundleArr3 = bundles.toArray(new Bundle[bundles.size()]);
                        for (i2 = 0; i2 < bundleArr3.length; i2++) {
                            if (hashSet.contains(bundleArr3[i2])) {
                                i++;
                                bundleArr2[i] = bundleArr3[i2];
                            }
                        }
                        i2 = startlevel;
                        SystemBundle.this.setLevel(bundleArr2, 0, true);
                        for (i = 0; i < bundleArr2.length; i++) {
                            ((BundleImpl) bundleArr2[i]).classloader.cleanup(false);
                            ((BundleImpl) bundleArr2[i]).staleExportedPackages = null;
                        }
                        for (Bundle bundle : bundleArr2) {
                            BundleClassLoader bundleClassLoader = ((BundleImpl) bundle).classloader;
                            if (bundleClassLoader.exports.length > 0) {
                                Framework.export(bundleClassLoader, bundleClassLoader.exports, false);
                            }
                        }
                        for (Bundle bundle2 : bundleArr2) {
                            try {
                                ((BundleImpl) bundle2).classloader.resolveBundle(true, new HashSet<BundleClassLoader>());
                            } catch (BundleException e) {
                                e.printStackTrace();
                            }
                        }
                        SystemBundle.this.setLevel(bundleArr2, i2, true);
                        Framework.notifyFrameworkListeners(4, systemBundle, null);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    } catch (Throwable th) {
                    }
                }
            }
        }

        SystemBundle() {
            this.props = new Hashtable<String, String>();
            this.props.put(Constants.BUNDLE_NAME, Constants.SYSTEM_BUNDLE_LOCATION);
            this.props.put(Constants.BUNDLE_VERSION, Framework.FRAMEWORK_VERSION);
            this.props.put(Constants.BUNDLE_VENDOR, "Atlas");
            ServiceReferenceImpl serviceReferenceImpl = new ServiceReferenceImpl(this, this, null, new String[]{StartLevel.class.getName(), PackageAdmin.class.getName()});
            Framework.addValue(Framework.classes_services, StartLevel.class.getName(), serviceReferenceImpl);
            Framework.addValue(Framework.classes_services, PackageAdmin.class.getName(), serviceReferenceImpl);
            Framework.services.add(serviceReferenceImpl);
            this.registeredServices = new ServiceReference[]{serviceReferenceImpl};
        }

        @Override
        public long getBundleId() {
            return 0;
        }

        @Override
        public Dictionary<String, String> getHeaders() {
            return this.props;
        }

        @Override
        public String getLocation() {
            return Constants.SYSTEM_BUNDLE_LOCATION;
        }

        @Override
        public ServiceReference[] getRegisteredServices() {
            return this.registeredServices;
        }

        @Override
        public URL getResource(String name) {
            return getClass().getResource(name);
        }

        @Override
        public ServiceReference[] getServicesInUse() {
            return null;
        }

        @Override
        public int getState() {
            return this.state;
        }

        @Override
        public boolean hasPermission(Object permission) {
            return true;
        }

        @Override
        public void start() throws BundleException {
        }

        @Override
        public void stop() throws BundleException {
            shutdownThread(false);
        }

        @Override
        public void uninstall() throws BundleException {
            throw new BundleException("Cannot uninstall the System Bundle");
        }

        @Override
        public void update() throws BundleException {
            shutdownThread(true);
        }

        private void shutdownThread(boolean z) {
            new ShutdownThread(z).start();
        }

        @Override
        public void update(InputStream inputStream) throws BundleException {
            shutdownThread(true);
        }

        @Override
        public void update(File file) throws BundleException {
            shutdownThread(true);
        }

        @Override
        public int getBundleStartLevel(Bundle bundle) {
            if (bundle == this) {
                return 0;
            }
            BundleImpl bundleImpl = (BundleImpl) bundle;
            if (bundleImpl.state != BundleEvent.INSTALLED) {
                return bundleImpl.currentStartlevel;
            }
            throw new IllegalArgumentException("Bundle " + bundle + " has been uninstalled");
        }

        @Override
        public int getInitialBundleStartLevel() {
            return Framework.initStartlevel;
        }

        @Override
        public int getStartLevel() {
            return Framework.startlevel;
        }

        @Override
        public boolean isBundlePersistentlyStarted(Bundle bundle) {
            if (bundle == this) {
                return true;
            }
            BundleImpl bundleImpl = (BundleImpl) bundle;
            if (bundleImpl.state != BundleEvent.INSTALLED) {
                return bundleImpl.persistently;
            }
            throw new IllegalArgumentException("Bundle " + bundle + " has been uninstalled");
        }

        @Override
        public void setBundleStartLevel(Bundle bundle, int level) {
            if (bundle == this) {
                throw new IllegalArgumentException("Cannot set the start level for the system bundle.");
            }
            BundleImpl bundleImpl = (BundleImpl) bundle;
            if (bundleImpl.state == BundleEvent.INSTALLED) {
                throw new IllegalArgumentException("Bundle " + bundle + " has been uninstalled");
            } else if (level <= 0) {
                throw new IllegalArgumentException("Start level " + level + " is not Component valid level");
            } else {
                bundleImpl.currentStartlevel = level;
                bundleImpl.updateMetadata();
                if (level <= Framework.startlevel && bundle.getState() != BundleEvent.RESOLVED && bundleImpl.persistently) {
                    try {
                        bundleImpl.startBundle();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Framework.notifyFrameworkListeners(BundleEvent.STARTED, bundle, e);
                    }
                } else if (level <= Framework.startlevel) {
                } else {
                    if (bundle.getState() != BundleEvent.STOPPED || bundle.getState() != BundleEvent.STARTED) {
                        try {
                            bundleImpl.stopBundle();
                        } catch (Throwable e2) {
                            Framework.notifyFrameworkListeners(BundleEvent.STARTED, bundle, e2);
                        }
                    }
                }
            }
        }

        @Override
        public void setInitialBundleStartLevel(int i) {
            if (i <= 0) {
                throw new IllegalArgumentException("Start level " + i + " is not Component valid level");
            }
            Framework.initStartlevel = i;
        }

        @Override
        public void setStartLevel(int i) {
            if (i <= 0) {
                throw new IllegalArgumentException("Start level " + i + " is not Component valid level");
            }
            new UpdateLevelThread(i).start();
        }

        @SuppressLint({"UseSparseArrays"})
        private void setLevel(Bundle[] bundleArr, int i, boolean z) {
            if (Framework.startlevel != i) {
                int i2 = i > Framework.startlevel ? 1 : 0;
                int i3 = i2 != 0 ? i - Framework.startlevel : Framework.startlevel - i;
                Map hashMap = new HashMap(0);
                int i4 = 0;
                while (i4 < bundleArr.length) {
                    if (bundleArr[i4] != Framework.systemBundle && (z || ((BundleImpl) bundleArr[i4]).persistently)) {
                        int i5;
                        BundleImpl bundleImpl = (BundleImpl) bundleArr[i4];
                        if (i2 != 0) {
                            i5 = (bundleImpl.currentStartlevel - Framework.startlevel) - 1;
                        } else {
                            i5 = Framework.startlevel - bundleImpl.currentStartlevel;
                        }
                        if (i5 >= 0 && i5 < i3) {
                            Framework.addValue(hashMap, Integer.valueOf(i5), bundleImpl);
                        }
                    }
                    i4++;
                }
                for (int i6 = 0; i6 < i3; i6++) {
                    if (i2 != 0) {
                        Framework.startlevel++;
                    } else {
                        Framework.startlevel--;
                    }
                    List list = (List) hashMap.get(Integer.valueOf(i6));
                    if (list != null) {
                        BundleImpl[] bundleImplArr = (BundleImpl[]) list.toArray(new BundleImpl[list.size()]);
                        for (i4 = 0; i4 < bundleImplArr.length; i4++) {
                            if (i2 != 0) {
                                try {
                                    System.out.println("STARTING " + bundleImplArr[i4].location);
                                    bundleImplArr[i4].startBundle();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    e.printStackTrace();
                                    Framework.notifyFrameworkListeners(2, Framework.systemBundle, e);
                                }
                            } else if (bundleImplArr[i4].getState() != 1) {
                                System.out.println("STOPPING " + bundleImplArr[i4].location);
                                try {
                                    bundleImplArr[(bundleImplArr.length - i4) - 1].stopBundle();
                                } catch (BundleException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                Framework.startlevel = i;
            }
        }

        @Override
        public ExportedPackage[] getExportedPackages(Bundle bundle) {
            return getExportedPackages(bundle, false);
        }

        private ExportedPackage[] getExportedPackages(Bundle bundle, boolean z) {
            synchronized (Framework.exportedPackages) {
                if (bundle != null) {
                    if (bundle != Framework.systemBundle) {
                        BundleImpl bundleImpl = (BundleImpl) bundle;
                        if (bundleImpl.state == 1) {
                            ExportedPackage[] exportedPackageArr;
                            if (z) {
                                exportedPackageArr = bundleImpl.staleExportedPackages;
                            } else {
                                exportedPackageArr = null;
                            }
                            return exportedPackageArr;
                        }
                        String[] strArr = bundleImpl.classloader.exports;
                        if (strArr == null) {
                            return null;
                        }
                        ArrayList arrayList = new ArrayList();
                        for (String str : strArr) {
                            Package packageR = Framework.exportedPackages.get(new Package(str, null, false));
                            if (packageR != null && packageR.classloader == bundleImpl.classloader) {
                                if (packageR.resolved) {
                                    arrayList.add(packageR);
                                } else {
                                    try {
                                        packageR.classloader.resolveBundle(true, new HashSet());
                                        arrayList.add(packageR);
                                    } catch (BundleException e) {
                                    }
                                }
                            }
                        }
                        if (bundleImpl.staleExportedPackages != null) {
                            arrayList.addAll(Arrays.asList(bundleImpl.staleExportedPackages));
                        }
                        System.out.println("\tBundle " + bundleImpl + " has exported packages " + arrayList);
                        return arrayList.isEmpty() ? null : (ExportedPackage[]) arrayList.toArray(new ExportedPackage[arrayList.size()]);
                    }
                }
                return Framework.exportedPackages.keySet().toArray(new ExportedPackage[Framework.exportedPackages.size()]);
            }
        }

        @Override
        public ExportedPackage getExportedPackage(String name) {
            synchronized (exportedPackages) {
                try {
                    Package packageR = exportedPackages.get(new Package(name, null, false));
                    if (packageR == null) {
                        return null;
                    }
                    if (!packageR.resolved) {
                        packageR.classloader.resolveBundle(true, new HashSet());
                    }
                    return packageR;
                } catch (BundleException e) {
                    return null;
                } catch (Throwable th) {
                }
            }
            return null;
        }

        @Override
        public void refreshPackages(Bundle[] bundleArr) {
            new RefreshBundlesThread(bundleArr).start();
        }

        @Override
        public String toString() {
            return "SystemBundle";
        }
    }

    //TODO // FIXME: 7/18/15
    static BundleImpl installNewBundle(String location, File apkFile) throws BundleException {
        BundleImpl bundleImpl;
        File mBundleArchiveFile = null;
        try {
            BundleLock.WriteLock(location);
            bundleImpl = (BundleImpl) Framework.getBundle(location);
            if (bundleImpl != null) {
                BundleLock.WriteUnLock(location);
            } else {
                mBundleArchiveFile = new File(STORAGE_LOCATION, location);

                OpenAtlasFileLock.getInstance().LockExclusive(mBundleArchiveFile);
                if (mBundleArchiveFile.exists()) {
                    bundleImpl = restoreFromExistedBundle(location, mBundleArchiveFile);
                    if (bundleImpl != null) {
                        BundleLock.WriteUnLock(location);
                        if (mBundleArchiveFile != null) {
                            OpenAtlasFileLock.getInstance().unLock(mBundleArchiveFile);
                        }
                    }
                }
                bundleImpl = new BundleImpl(mBundleArchiveFile, location, new BundleContextImpl(), null, apkFile, true);
                storeMetadata();
                BundleLock.WriteUnLock(location);
                if (mBundleArchiveFile != null) {
                    OpenAtlasFileLock.getInstance().unLock(mBundleArchiveFile);
                }
            }
        } catch (Throwable e) {

            e.printStackTrace();
            BundleLock.WriteUnLock(location);
            throw new BundleException(e.getMessage());
        }

        return bundleImpl;
    }

    static boolean restoreBundle(String[] packageNames) {

        try {
            for (String pkgName : packageNames) {
                File archiveFile = new File(STORAGE_LOCATION, pkgName);
                if (!archiveFile.exists() || !BundleArchive.downgradeRevision(archiveFile)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static BundleImpl installNewBundle(String location, InputStream archiveInputStream) throws BundleException {
        BundleImpl bundleImpl = null;
        File mBundleArchiveFile = null;
        try {
            BundleLock.WriteLock(location);
            bundleImpl = (BundleImpl) getBundle(location);
            if (bundleImpl != null) {
                BundleLock.WriteUnLock(location);

            } else {
                mBundleArchiveFile = new File(STORAGE_LOCATION, location);
                OpenAtlasFileLock.getInstance().LockExclusive(mBundleArchiveFile);
                if (mBundleArchiveFile.exists()) {
                    bundleImpl = restoreFromExistedBundle(location, mBundleArchiveFile);
                    if (bundleImpl != null) {
                        BundleLock.WriteUnLock(location);
                        if (location != null) {
                            OpenAtlasFileLock.getInstance().unLock(mBundleArchiveFile);
                        }
                    }
                }
                bundleImpl = new BundleImpl(mBundleArchiveFile, location, new BundleContextImpl(), archiveInputStream, null, true);
                storeMetadata();
                BundleLock.WriteUnLock(location);
                if (mBundleArchiveFile != null) {
                    OpenAtlasFileLock.getInstance().unLock(mBundleArchiveFile);
                }

            }
        } catch (Throwable v0) {
            BundleLock.WriteUnLock(location);
        }

        return bundleImpl;
    }


    private Framework() {
    }

    static void startup(Properties properties) throws BundleException {
        if (properties == null) {
            properties = new Properties();
        }
        Framework.properties = properties;
        startup();
    }

    private static void startup() throws BundleException {
        int i;
        int property;
        frameworkStartupShutdown = true;
        System.out.println("---------------------------------------------------------");
        System.out.println("  OpenAtlas OSGi 1.0.0  Pre-Release on " + Build.MODEL + "/" + Build.CPU_ABI + "/"
                + VERSION.RELEASE + " starting ...");
        System.out.println("---------------------------------------------------------");
        long currentTimeMillis = System.currentTimeMillis();
        initialize();
        Framework.launch();
        boolean property2 = getProperty("osgi.init", false);
        if (property2) {
            i = -1;
        } else {
            i = restoreProfile();
            restart = true;
        }
        if (i == -1) {
            restart = false;
            File file = new File(STORAGE_LOCATION);
            if (property2 && file.exists()) {
                System.out.println("Purging storage ...");
                try {
                    deleteDirectory(file);
                } catch (Throwable e) {
                    throw new RuntimeException("deleteDirectory failed", e);
                }
            }
            try {
                file.mkdirs();
                Integer.getInteger("osgi.maxLevel", Integer.valueOf(1)).intValue();
                initStartlevel = getProperty("osgi.startlevel.bundle", 1);
                property = getProperty("osgi.startlevel.framework", 1);
            } catch (Throwable e2) {
                throw new RuntimeException("mkdirs failed", e2);
            }
        }
        property = i;
        notifyFrameworkListeners(0, systemBundle, null);
        systemBundle.setLevel(getBundles().toArray(new Bundle[bundles.size()]), property, false);
        frameworkStartupShutdown = false;
        if (!restart) {
            try {
                storeProfile();
            } catch (Throwable e22) {
                throw new RuntimeException("storeProfile failed", e22);
            }
        }
        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
        System.out.println("---------------------------------------------------------");
        System.out.println("  Framework " + (restart ? "restarted" : "started") + " in " + currentTimeMillis2 + " milliseconds.");
        System.out.println("---------------------------------------------------------");
        System.out.flush();
        systemBundle.state = BundleEvent.RESOLVED;
        try {
            notifyFrameworkListeners(FrameworkEvent.STARTED, systemBundle, null);
        } catch (Throwable e222) {
            throw new RuntimeException("notifyFrameworkListeners failed", e222);
        }
    }

    public static ClassLoader getSystemClassLoader() {
        return systemClassLoader;
    }

    public static List<Bundle> getBundles() {
        List<Bundle> arrayList = new ArrayList<Bundle>(bundles.size());
        synchronized (bundles) {
            arrayList.addAll(bundles.values());
        }
        return arrayList;
    }

    public static Bundle getBundle(String location) {
        return bundles.get(location);
    }

    public static Bundle getBundle(long j) {
        return null;
    }

    static void shutdown(boolean restart) {
        System.out.println("---------------------------------------------------------");
        System.out.println("  Atlas OSGi shutting down ...");
        System.out.println("  Bye !");
        System.out.println("---------------------------------------------------------");
        systemBundle.state = BundleEvent.UNINSTALLED;
        systemBundle.setLevel(getBundles().toArray(new Bundle[bundles.size()]), 0, true);
        bundles.clear();
        systemBundle.state = BundleEvent.INSTALLED;
        if (restart) {
            try {
                startup();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public static void initialize() {

        File filesDir = RuntimeVariables.androidApplication.getFilesDir();
        if (filesDir == null || !filesDir.exists()) {
            filesDir = RuntimeVariables.androidApplication.getFilesDir();
        }
        BASEDIR = properties.getProperty(PlatformConfigure.OPENATLAS_BASEDIR, filesDir.getAbsolutePath());
        BUNDLE_LOCATION = properties.getProperty(PlatformConfigure.OPENATLAS_BUNDLE_LOCATION, "file:" + BASEDIR);
        CLASSLOADER_BUFFER_SIZE = getProperty(PlatformConfigure.OPENATLAS_CLASSLOADER_BUFFER_SIZE, 1024 * 10);
        LOG_LEVEL = getProperty(PlatformConfigure.OPENATLAS_LOG_LEVEL, 6);
        DEBUG_BUNDLES = getProperty(PlatformConfigure.OPENATLAS_DEBUG_BUNDLES, false);
        DEBUG_PACKAGES = getProperty(PlatformConfigure.OPENATLAS_DEBUG_PACKAGES, false);
        DEBUG_SERVICES = getProperty(PlatformConfigure.OPENATLAS_DEBUG_SERVICES, false);
        DEBUG_CLASSLOADING = getProperty(PlatformConfigure.OPENATLAS_DEBUG_CLASSLOADING, false);
        if (getProperty(PlatformConfigure.OPENATLAS_DEBUG, false)) {
            System.out.println("SETTING ALL DEBUG FLAGS");
            LOG_LEVEL = 3;
            DEBUG_BUNDLES = true;
            DEBUG_PACKAGES = true;
            DEBUG_SERVICES = true;
            DEBUG_CLASSLOADING = true;
        }
        STRICT_STARTUP = getProperty(PlatformConfigure.OPENATLAS_STRICT_STARTUP, false);
        String property = properties.getProperty("org.osgi.framework.system.packages");
        if (property != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            int countTokens = stringTokenizer.countTokens();
            for (int i = 0; i < countTokens; i++) {
                BundleClassLoader.FRAMEWORK_PACKAGES.add(stringTokenizer.nextToken().trim());
            }
        }
        properties.put(Constants.FRAMEWORK_EXECUTIONENVIRONMENT, System.getProperty("java.specification.name") + "/" + System.getProperty("java.specification.version"));
        Properties properties2 = properties;
        String str = Constants.FRAMEWORK_OS_NAME;
        Object property2 = System.getProperty("os.name");
        if (property2 == null) {
            property2 = "undefined";
        }
        properties2.put(str, property2);
        properties2 = properties;
        str = Constants.FRAMEWORK_OS_VERSION;
        property2 = System.getProperty("os.version");
        if (property2 == null) {
            property2 = "undefined";
        }
        properties2.put(str, property2);
        properties2 = properties;
        str = Constants.FRAMEWORK_PROCESSOR;
        property2 = System.getProperty("os.arch");
        if (property2 == null) {
            property2 = "undefined";
        }
        properties2.put(str, property2);
        properties.put(Constants.FRAMEWORK_VERSION, FRAMEWORK_VERSION);
        properties.put(Constants.FRAMEWORK_VENDOR, "Atlas");
        property2 = Locale.getDefault().getLanguage();
        properties2 = properties;
        str = Constants.FRAMEWORK_LANGUAGE;
        if (property2 == null) {
            property2 = "en";
        }
        properties2.put(str, property2);


    }

    private static void launch() {
        STORAGE_LOCATION = properties.getProperty(PlatformConfigure.INSTALL_LOACTION, properties.getProperty("org.osgi.framework.dir", BASEDIR + File.separatorChar + "storage"))
                + File.separatorChar;
        systemBundle = new SystemBundle();
        systemBundle.state = BundleEvent.UPDATED;
    }

    public static boolean getProperty(String key, boolean defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        String str2 = (String) properties.get(key);
        return str2 != null ? Boolean.valueOf(str2).booleanValue() : defaultValue;
    }

    public static int getProperty(String key, int defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        String str2 = (String) properties.get(key);
        return str2 != null ? Integer.parseInt(str2) : defaultValue;
    }

    public static String getProperty(String key) {
        if (properties == null) {
            return null;
        }
        return (String) properties.get(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties == null ? defaultValue : (String) properties.get(key);
    }

    protected static void warning(String str) throws RuntimeException {
        if (getProperty(PlatformConfigure.OPENATLAS_STRICT_STARTUP, false)) {
            throw new RuntimeException(str);
        }
        System.err.println("WARNING: " + str);
    }

    private static void storeProfile() {
        BundleImpl[] bundleImplArr = getBundles().toArray(new BundleImpl[bundles.size()]);
        for (BundleImpl updateMetadata : bundleImplArr) {
            updateMetadata.updateMetadata();
        }
        storeMetadata();
    }

    static void storeMetadata() {

        try {
            File metaFile = new File(STORAGE_LOCATION, "meta");

            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(metaFile));
            dataOutputStream.writeInt(startlevel);
            String join = StringUtils.join(writeAheads.toArray(), ",");
            if (join == null) {
                join = "";
            }
            dataOutputStream.writeUTF(join);
            dataOutputStream.flush();
            dataOutputStream.close();

        } catch (IOException e) {
            OpenAtlasMonitor.getInstance().trace(Integer.valueOf(OpenAtlasMonitor.WRITE_META_FAIL), "", "", "storeMetadata failed ", e);
            log.error("Could not save meta data.", e);
        }
    }


    private static int restoreProfile() {
        try {
            System.out.println("Restoring profile");
            File file = new File(STORAGE_LOCATION, "meta");
            if (file.exists()) {
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
                int readInt = dataInputStream.readInt();
                String[] split = StringUtils.split(dataInputStream.readUTF(), ",");
                if (split != null) {
                    writeAheads.addAll(Arrays.asList(split));
                }
                dataInputStream.close();
                if (!getProperty(PlatformConfigure.OPENATLAS_AUTO_LOAD, true)) {
                    return readInt;
                }
                File file2 = new File(STORAGE_LOCATION);
                mergeWalsDir(new File(STORAGE_LOCATION, "wal"), file2);
                MergeWirteAheads(file2);
                File[] listFiles = file2.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String str) {
                        return !str.matches("^[0-9]*");
                    }
                });
                int i = 0;
                while (i < listFiles.length) {
                    if (listFiles[i].isDirectory() && new File(listFiles[i], "meta").exists()) {
                        try {
                            System.out.println("RESTORED BUNDLE " + new BundleImpl(listFiles[i], new BundleContextImpl()).location);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e.getCause());
                        }
                    }
                    i++;
                }
                return readInt;
            }
            System.out.println("Profile not found, performing clean start ...");
            return -1;
        } catch (Exception e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    private static void mergeWalsDir(File file, File file2) {
        if (writeAheads != null && writeAheads.size() > 0) {
            for (int i = 0; i < writeAheads.size(); i++) {
                if (writeAheads.get(i) != null) {
                    File file3 = new File(file, writeAheads.get(i));
                    if (file3 != null) {
                        try {
                            if (file3.exists()) {
                                File[] listFiles = file3.listFiles();
                                if (listFiles != null) {
                                    for (File file4 : listFiles) {
                                        if (file4.isDirectory()) {
                                            File file5 = new File(file2, file4.getName());
                                            if (file5.exists()) {
                                                File[] listFiles2 = file4.listFiles(new FilenameFilter() {
                                                    @Override
                                                    public boolean accept(File file, String str) {
                                                        return str.startsWith(BundleArchive.REVISION_DIRECTORY);
                                                    }
                                                });
                                                if (listFiles2 != null) {
                                                    for (File file6 : listFiles2) {
                                                        if (new File(file6, "meta").exists()) {
                                                            file6.renameTo(new File(file5, file6.getName()));
                                                        }
                                                    }
                                                }
                                            } else {
                                                file4.renameTo(file5);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Throwable e) {
                            log.error("Error while merge wal dir", e);
                        }
                    }
                    writeAheads.set(i, null);
                }
            }
        }
        if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteDirectory(File mDirectory) {
        File[] listFiles = mDirectory.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                deleteDirectory(listFiles[i]);
            } else {
                listFiles[i].delete();
            }
        }
        mDirectory.delete();
    }

    static void checkAdminPermission() {
        AccessController.checkPermission(ADMIN_PERMISSION);
    }

    static BundleImpl installNewBundle(String bundleName) throws BundleException {
        try {
            String str2 = bundleName.indexOf(":") > -1 ? bundleName : BUNDLE_LOCATION + File.separatorChar + bundleName;
            return installNewBundle(str2, new URL(str2).openConnection().getInputStream());
        } catch (Throwable e) {
            throw new BundleException("Cannot retrieve bundle from " + bundleName, e);
        }
    }

    private static BundleImpl restoreFromExistedBundle(String location, File file) {

        try {
            return new BundleImpl(file, new BundleContextImpl());
        } catch (Throwable e) {
            OpenAtlasMonitor.getInstance().trace(Integer.valueOf(-1), "", "", "restore bundle failed " + location + e);
            log.error("restore bundle failed" + location, e);
            return null;
        }
    }

    static void installOrUpdate(String[] locations, File[] archiveFiles) throws BundleException {
        if (locations == null || archiveFiles == null || locations.length != archiveFiles.length) {
            throw new IllegalArgumentException("locations and files must not be null and must be same length");
        }
        String valueOf = String.valueOf(System.currentTimeMillis());
        File file = new File(new File(STORAGE_LOCATION, "wal"), valueOf);
        file.mkdirs();
        int i = 0;
        while (i < locations.length) {
            if (!(locations[i] == null || archiveFiles[i] == null)) {
                try {
                    BundleLock.WriteLock(locations[i]);
                    Bundle bundle = getBundle(locations[i]);
                    if (bundle != null) {
                        bundle.update(archiveFiles[i]);
                    } else {
                        BundleImpl bundleImpl = new BundleImpl(new File(file, locations[i]), locations[i], new BundleContextImpl(), null, archiveFiles[i], false);
                    }
                    BundleLock.WriteUnLock(locations[i]);
                } catch (Throwable th) {
                    BundleLock.WriteUnLock(locations[i]);
                }
            }
            i++;
        }
        writeAheads.add(valueOf);
        storeMetadata();
    }

    static void unregisterService(ServiceReference serviceReference) {
        services.remove(serviceReference);
        removeValue(classes_services, (String[]) serviceReference.getProperty(Constants.OBJECTCLASS), serviceReference);
        BundleImpl bundleImpl = (BundleImpl) serviceReference.getBundle();
        bundleImpl.registeredServices.remove(serviceReference);
        if (bundleImpl.registeredServices.isEmpty()) {
            bundleImpl.registeredServices = null;
        }
        notifyServiceListeners(BundleEvent.STOPPED, serviceReference);
        if (DEBUG_SERVICES && log.isInfoEnabled()) {
            log.info("Framework: UNREGISTERED SERVICE " + serviceReference);
        }
    }

    static void notifyBundleListeners(int event, Bundle bundle) {

        if (!syncBundleListeners.isEmpty() || !bundleListeners.isEmpty()) {
            BundleEvent bundleEvent = new BundleEvent(event, bundle);
            BundleListener[] bundleListenerArr = syncBundleListeners.toArray(new BundleListener[syncBundleListeners.size()]);
            for (BundleListener bundleChanged : bundleListenerArr) {
                bundleChanged.bundleChanged(bundleEvent);
            }
            if (!bundleListeners.isEmpty()) {
                bundleListenerArr = bundleListeners.toArray(new BundleListener[bundleListeners.size()]);
                for (BundleListener bundleListener : bundleListenerArr) {
                    bundleListener.bundleChanged(bundleEvent);
                }

            }
        }
    }

    static void addFrameworkListener(FrameworkListener frameworkListener) {
        frameworkListeners.add(frameworkListener);
    }

    static void removeFrameworkListener(FrameworkListener frameworkListener) {
        frameworkListeners.remove(frameworkListener);
    }

    private static void restoreBundles() throws IOException {
        File file = new File(STORAGE_LOCATION, DOWN_GRADE_FILE);
        for (String pkg : FileUtils.getStrings(file)) {

            File locationFolder = new File(STORAGE_LOCATION, pkg);
            if (locationFolder.exists()) {
                String[] list = locationFolder.list();
                String version = null;
                if (list != null) {
                    for (String string : list) {
                        if (string.startsWith("version")
                                || Long.parseLong(StringUtils.substringAfter(string, ".")) <= 0) {
                            version = string;
                        }
                    }

                }
                if (version == null) {
                    FileUtils.deleteFile(locationFolder.getAbsolutePath());
                } else {
                    File tmp = new File(locationFolder, version);
                    if (tmp.exists()) {
                        FileUtils.deleteFile(tmp.getAbsolutePath());
                    }
                }
            }
        }
        if (file.exists()) {
            FileUtils.deleteFile(file.getAbsolutePath());
        }
    }

    private static void MergeWirteAheads(File file) {
        try {
            File file2 = new File(STORAGE_LOCATION, "wal");
            String curProcessName = OpenAtlasUtils.getProcessNameByPID(Process.myPid());
            log.debug("restoreProfile in process " + curProcessName);
            String packageName = RuntimeVariables.androidApplication.getPackageName();
            if (curProcessName != null && packageName != null && curProcessName.equals(packageName)) {
                mergeWalsDir(file2, file);
            }
        } catch (Throwable th) {
            if (Build.MODEL == null || !Build.MODEL.equals("HTC 802w")) {
                log.error(th.getMessage(), th.getCause());
                return;
            }
            RuntimeException runtimeException = new RuntimeException(th);
        }
    }

    static void addBundleListener(BundleListener bundleListener) {
        bundleListeners.add(bundleListener);
    }

    static void removeBundleListener(BundleListener bundleListener) {
        bundleListeners.remove(bundleListener);
    }

    static void notifyFrameworkListeners(int event, Bundle bundle, Throwable th) {
        if (!frameworkListeners.isEmpty()) {
            FrameworkEvent frameworkEvent = new FrameworkEvent(event, bundle, th);
            FrameworkListener[] frameworkListenerArr = frameworkListeners.toArray(new FrameworkListener[frameworkListeners.size()]);
            for (FrameworkListener frameworkListener : frameworkListenerArr) {
                frameworkListener.frameworkEvent(frameworkEvent);
            }
        }
    }

    static void notifyServiceListeners(int event, ServiceReference serviceReference) {
        if (!serviceListeners.isEmpty()) {
            ServiceEvent serviceEvent = new ServiceEvent(event, serviceReference);
            ServiceListenerEntry[] serviceListenerEntryArr = serviceListeners.toArray(new ServiceListenerEntry[serviceListeners.size()]);
            for (int i = 0; i < serviceListenerEntryArr.length; i++) {
                if (serviceListenerEntryArr[i].filter == null || serviceListenerEntryArr[i].filter.match(((ServiceReferenceImpl) serviceReference).properties)) {
                    serviceListenerEntryArr[i].listener.serviceChanged(serviceEvent);
                }
            }

        }
    }

    static void clearBundleTrace(BundleImpl bundleImpl) {

        if (bundleImpl.registeredFrameworkListeners != null) {
            frameworkListeners.removeAll(bundleImpl.registeredFrameworkListeners);
            bundleImpl.registeredFrameworkListeners = null;
        }
        if (bundleImpl.registeredServiceListeners != null) {
            serviceListeners.removeAll(bundleImpl.registeredServiceListeners);
            bundleImpl.registeredServiceListeners = null;
        }
        if (bundleImpl.registeredBundleListeners != null) {
            bundleListeners.removeAll(bundleImpl.registeredBundleListeners);
            syncBundleListeners.removeAll(bundleImpl.registeredBundleListeners);
            bundleImpl.registeredBundleListeners = null;
        }
        ServiceReference[] registeredServices = bundleImpl.getRegisteredServices();
        if (registeredServices != null) {
            for (ServiceReference serviceReference : registeredServices) {
                unregisterService(serviceReference);
                ((ServiceReferenceImpl) serviceReference).invalidate();
            }

            bundleImpl.registeredServices = null;
        }
        ServiceReference[] servicesInUse = bundleImpl.getServicesInUse();
        for (ServiceReference serviceReference : servicesInUse) {
            ((ServiceReferenceImpl) serviceReference).ungetService(bundleImpl);
        }

    }

    static void addValue(Map map, Object obj, Object obj2) {
        List list = (List) map.get(obj);
        if (list == null) {
            list = new ArrayList();
        }
        list.add(obj2);
        map.put(obj, list);
    }

    static void removeValue(Map map, Object[] objArr, Object obj) {
        for (int i = 0; i < objArr.length; i++) {
            List list = (List) map.get(objArr[i]);
            if (list != null) {
                list.remove(obj);
                if (list.isEmpty()) {
                    map.remove(objArr[i]);
                } else {
                    map.put(objArr[i], list);
                }
            }
        }
    }

    static void export(BundleClassLoader bundleClassLoader, String[] packageNames, boolean resolved) {
        synchronized (exportedPackages) {
            if (DEBUG_PACKAGES && log.isDebugEnabled()) {
                log.debug("Bundle " + bundleClassLoader.bundle + " registers " + (resolved ? "resolved" : "unresolved") + " packages " + Arrays.asList(packageNames));
            }
            for (String packageName : packageNames) {
                Package packageR = new Package(packageName, bundleClassLoader, resolved);
                Package packageR2 = exportedPackages.get(packageR);
                if (packageR2 == null) {
                    exportedPackages.put(packageR, packageR);
                    if (DEBUG_PACKAGES && log.isDebugEnabled()) {
                        log.debug("REGISTERED PACKAGE " + packageR);
                    }
                } else if (packageR2.importingBundles == null && packageR.updates(packageR2)) {
                    exportedPackages.remove(packageR2);
                    exportedPackages.put(packageR, packageR);
                    if (DEBUG_PACKAGES && log.isDebugEnabled()) {
                        log.debug("REPLACED PACKAGE " + packageR2 + " WITH " + packageR);
                    }
                }
            }
        }
    }

    static BundleClassLoader getImport(BundleImpl bundleImpl, String packageName, boolean resolve, HashSet<BundleClassLoader> hashSet) {
        if (DEBUG_PACKAGES && log.isDebugEnabled()) {
            log.debug("Bundle " + bundleImpl + " requests package " + packageName);
        }
        synchronized (exportedPackages) {
            try {
                Package packageR = exportedPackages.get(new Package(packageName, null, false));
                if (packageR == null || !(packageR.resolved || resolve)) {
                    return null;
                }
                BundleClassLoader bundleClassLoader = packageR.classloader;
                if (bundleClassLoader == bundleImpl.classloader) {
                    return bundleClassLoader;
                }
                if (!(!resolve || packageR.resolved || hashSet.contains(packageR.classloader))) {
                    hashSet.add(bundleImpl.classloader);
                    packageR.classloader.resolveBundle(true, hashSet);
                }
                if (packageR.importingBundles == null) {
                    packageR.importingBundles = new ArrayList();
                }
                if (!packageR.importingBundles.contains(bundleImpl)) {
                    packageR.importingBundles.add(bundleImpl);
                }
                if (DEBUG_PACKAGES && log.isDebugEnabled()) {
                    log.debug("REQUESTED PACKAGE " + packageName + ", RETURNED DELEGATION TO " + bundleClassLoader.bundle);
                }
                return bundleClassLoader;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } catch (Throwable th) {
            }
        }
        return null;
    }

    public static boolean isFrameworkStartupShutdown() {
        return frameworkStartupShutdown;
    }

    public static ClassNotFoundInterceptorCallback getClassNotFoundCallback() {
        return classNotFoundCallback;
    }

    public static void setClassNotFoundCallback(ClassNotFoundInterceptorCallback classNotFoundInterceptorCallback) {
        classNotFoundCallback = classNotFoundInterceptorCallback;
    }


}
