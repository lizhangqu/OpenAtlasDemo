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

import com.openatlas.framework.bundlestorage.Archive;
import com.openatlas.framework.bundlestorage.BundleArchive;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public final class BundleImpl implements Bundle {
    static final Logger log;
    Archive archive;
    final File bundleDir;
    BundleClassLoader classloader;
    private final BundleContextImpl context;
    int currentStartlevel;
    ProtectionDomain domain;
    Hashtable<String, String> headers = new Hashtable<String, String>();
    final String location;
    boolean persistently;
    List<BundleListener> registeredBundleListeners;
    List<FrameworkListener> registeredFrameworkListeners;
    List<ServiceListener> registeredServiceListeners;
    List<ServiceReference> registeredServices;
    Package[] staleExportedPackages;
    int state = 0;


    static {
        log = LoggerFactory.getInstance("BundleImpl");
    }

    BundleImpl(File bundleDir, String location, BundleContextImpl bundleContextImpl,
               InputStream archiveInputStream, File archiveFile, boolean isInstall)
            throws BundleException, IOException {
        this.persistently = false;
        this.domain = null;
        this.registeredServices = null;
        this.registeredFrameworkListeners = null;
        this.registeredBundleListeners = null;
        this.registeredServiceListeners = null;
        this.staleExportedPackages = null;
        long currentTimeMillis = System.currentTimeMillis();
        this.location = location;
        bundleContextImpl.bundle = this;
        this.context = bundleContextImpl;
        this.currentStartlevel = Framework.initStartlevel;
        this.bundleDir = bundleDir;
        if (archiveInputStream != null) {
            //  try {
            this.archive = new BundleArchive(location, bundleDir, archiveInputStream);
//            } catch (Throwable e) {
//                Framework.deleteDirectory(bundleDir);
//                throw new BundleException("Could not install bundle " + location, e);
//            }
        } else if (archiveFile != null) {
            try {
                this.archive = new BundleArchive(location, bundleDir, archiveFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.state = BundleEvent.STARTED;

        updateMetadata();
        if (isInstall) {
            Framework.bundles.put(location, this);
            resolveBundle(false);
            Framework.notifyBundleListeners(1, this);
        }

        if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
            log.info("Framework: Bundle " + toString() + " created. "
                    + (System.currentTimeMillis() - currentTimeMillis) + " ms");
        }
    }

    BundleImpl(File file, BundleContextImpl bundleContextImpl) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(new File(file, "meta")));
        this.location = dataInputStream.readUTF();
        this.currentStartlevel = dataInputStream.readInt();
        this.persistently = dataInputStream.readBoolean();
        dataInputStream.close();
        bundleContextImpl.bundle = this;
        this.context = bundleContextImpl;
        this.bundleDir = file;
        this.state = BundleEvent.STARTED;
        try {
            this.archive = new BundleArchive(this.location, file);
            resolveBundle(false);
            Framework.bundles.put(this.location, this);
            Framework.notifyBundleListeners(1, this);
            if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                log.info("Framework: Bundle " + toString() + " loaded. " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
            }
        } catch (Exception e) {
            throw new BundleException("Could not load bundle " + this.location, e.getCause());
        }
    }


    private synchronized void resolveBundle(boolean recursive) throws BundleException {
        if (this.state != 4) {
            if (this.classloader == null) {
                this.classloader = new BundleClassLoader(this);
            }
            if (recursive) {
                this.classloader.resolveBundle(true, new HashSet(0));
                this.state = 4;
            } else if (this.classloader.resolveBundle(false, null)) {
                this.state = 4;
            }
            Framework.notifyBundleListeners(0, this);
        }
    }

    @Override
    public long getBundleId() {
        return 0;
    }

    @Override
    public Dictionary<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    public Archive getArchive() {
        return this.archive;
    }

    public ClassLoader getClassLoader() {
        return this.classloader;
    }

    @Override
    public ServiceReference[] getRegisteredServices() {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Bundle " + toString()
                    + "has been unregistered.");
        } else if (this.registeredServices == null) {
            return null;
        } else {
            return this.registeredServices
                    .toArray(new ServiceReference[this.registeredServices
                            .size()]);
        }
    }

    @Override
    public URL getResource(String name) {
        if (this.state != BundleEvent.INSTALLED) {
            return this.classloader.getResource(name);
        }
        throw new IllegalStateException("Bundle " + toString()
                + " has been uninstalled");
    }

    @Override
    public ServiceReference[] getServicesInUse() {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Bundle " + toString()
                    + "has been unregistered.");
        }
        ArrayList<ServiceReferenceImpl> arrayList = new ArrayList<ServiceReferenceImpl>();
        ServiceReferenceImpl[] serviceReferenceImplArr = Framework.services
                .toArray(new ServiceReferenceImpl[Framework.services.size()]);
        int i = 0;
        while (i < serviceReferenceImplArr.length) {
            synchronized (serviceReferenceImplArr[i].useCounters) {
                if (serviceReferenceImplArr[i].useCounters.get(this) != null) {
                    arrayList.add(serviceReferenceImplArr[i]);
                }
            }
            i++;
        }
        return arrayList
                .toArray(new ServiceReference[arrayList.size()]);
    }

    @Override
    public int getState() {
        return this.state;
    }

    @Override
    public boolean hasPermission(Object permission) {
        if (this.state != BundleEvent.INSTALLED) {
            return true;
        }
        throw new IllegalStateException("Bundle " + toString()
                + "has been unregistered.");
    }

    @Override
    public synchronized void start() throws BundleException {
        this.persistently = true;
        updateMetadata();
        if (this.currentStartlevel <= Framework.startlevel) {
            startBundle();
        }
    }

    public synchronized void startBundle() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot start uninstalled bundle "
                    + toString());
        } else if (this.state != BundleEvent.RESOLVED) {
            if (this.state == BundleEvent.STARTED) {
                resolveBundle(true);
            }
            this.state = BundleEvent.UPDATED;
            try {

                this.context.isValid = true;
                // if (!(this.classloader.activatorClassName == null ||
                // StringUtils
                // .isBlank(this.classloader.activatorClassName))) {
                // Class<?> loadClass = this.classloader
                // .loadClass(this.classloader.activatorClassName);
                // if (loadClass == null) {
                // throw new ClassNotFoundException(
                // this.classloader.activatorClassName);
                // }
                // this.classloader.activator = (BundleActivator) loadClass
                // .newInstance();
                // this.classloader.activator.start(this.context);
                //
                // }
                this.state = BundleEvent.RESOLVED;
                Framework.notifyBundleListeners(BundleEvent.STARTED, this);
                if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                    log.info("Framework: Bundle " + toString() + " started.");
                }
            } catch (Throwable th) {

                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                String str = "Error starting bundle " + toString();

                BundleException bundleException = new BundleException(str, th);
            }
        }
    }

    @Override
    public synchronized void stop() throws BundleException {
        this.persistently = false;
        updateMetadata();
        stopBundle();
    }

    public synchronized void stopBundle() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot stop uninstalled bundle "
                    + toString());
        } else if (this.state == BundleEvent.RESOLVED) {
            this.state = BundleEvent.UNINSTALLED;
            try {
                // if (this.classloader.activator != null) {
                // this.classloader.activator.stop(this.context);
                // }
                if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                    log.info("Framework: Bundle " + toString() + " stopped.");
                }
                // this.classloader.activator = null;
                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                Framework.notifyBundleListeners(BundleEvent.STOPPED, this);
                this.context.isValid = false;
            } catch (Throwable th) {
                // this.classloader.activator = null;
                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                Framework.notifyBundleListeners(BundleEvent.STOPPED, this);
                this.context.isValid = false;
            }
        }
    }

    @Override
    public synchronized void uninstall() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Bundle " + toString() + " is already uninstalled.");
        }
        if (this.state == BundleEvent.RESOLVED) {
            try {
                stopBundle();
            } catch (Throwable th) {
                Framework.notifyFrameworkListeners(BundleEvent.STARTED, this, th);
            }
        }
        this.state = BundleEvent.INSTALLED;
        new File(this.bundleDir, "meta").delete();
        if (this.classloader.originalExporter != null) {
            this.classloader.originalExporter.cleanup(true);
            this.classloader.originalExporter = null;
        }
        this.classloader.cleanup(true);
        this.classloader = null;
        Framework.bundles.remove(this);
        Framework.notifyBundleListeners(BundleEvent.UNINSTALLED, this);
        this.context.isValid = false;
        this.context.bundle = null;


    }

    @Override
    public synchronized void update() throws BundleException {
        String locationUpdate = this.headers.get(Constants.BUNDLE_UPDATELOCATION);
        try {

            if (locationUpdate == null) {
                locationUpdate = this.location;
            }
            update(new URL(locationUpdate).openConnection().getInputStream());
        } catch (Throwable e) {
            throw new BundleException("Could not update " + toString()
                    + " from " + locationUpdate, e);
        }
    }

    @Override
    public synchronized void update(InputStream inputStream)
            throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot update uninstalled bundle "
                    + toString());
        }
        try {
            this.archive
                    .newRevision(this.location, this.bundleDir, inputStream);
        } catch (Throwable e) {
            throw new BundleException("Could not update bundle " + toString(),
                    e);
        }
    }

    @Override
    public synchronized void update(File bundleFile) throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot update uninstalled bundle "
                    + toString());
        }
        try {
            this.archive.newRevision(this.location, this.bundleDir, bundleFile);
        } catch (Throwable e) {
            throw new BundleException("Could not update bundle " + toString(),
                    e);
        }
    }

    public synchronized void refresh() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException(
                    "Cannot refresh uninstalled bundle " + toString());
        }
        Object obj;
        if (this.state == BundleEvent.RESOLVED) {
            stopBundle();
            obj = 1;
        } else {
            obj = null;
        }
        try {
            this.archive = new BundleArchive(this.location, this.bundleDir);
            BundleClassLoader bundleClassLoader = new BundleClassLoader(this);
            String[] strArr = this.classloader.exports;
            if (strArr.length > 0) {
                int i = 0;
                Object obj2 = null;
                while (i < strArr.length) {
                    Object obj3;
                    Package packageR = Framework.exportedPackages
                            .get(new Package(strArr[i], null, false));
                    if (packageR.importingBundles == null
                            || packageR.classloader != this.classloader) {
                        obj3 = obj2;
                    } else {
                        packageR.removalPending = true;
                        obj3 = 1;
                    }
                    i++;
                    obj2 = obj3;
                }
                if (obj2 != null) {
                    if (this.classloader.originalExporter != null) {
                        bundleClassLoader.originalExporter = this.classloader.originalExporter;
                    } else {
                        bundleClassLoader.originalExporter = this.classloader;
                    }
                }
            }
            this.classloader.cleanup(true);
            this.classloader = bundleClassLoader;
            if (this.classloader.resolveBundle(false, null)) {
                this.state = BundleEvent.STOPPED;
            } else {
                this.state = BundleEvent.STARTED;
            }
            Framework.notifyBundleListeners(BundleEvent.UPDATED, this);
            if (obj != null) {
                startBundle();
            }
        } catch (BundleException e) {
            throw e;
        } catch (Throwable e2) {
            throw new BundleException("Could not refresh bundle " + toString(),
                    e2);
        }
    }

    public synchronized void optDexFile() {
        getArchive().optDexFile();
    }

    public synchronized void purge() throws BundleException {
        try {
            getArchive().purge();
        } catch (Throwable e) {
            throw new BundleException("Could not purge bundle " + toString(), e);
        }
    }

    void updateMetadata() {
        File file = new File(this.bundleDir, "meta");
        DataOutputStream dataOutputStream = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            dataOutputStream = new DataOutputStream(fileOutputStream);
            dataOutputStream.writeUTF(this.location);
            dataOutputStream.writeInt(this.currentStartlevel);
            dataOutputStream.writeBoolean(this.persistently);
            dataOutputStream.flush();
            fileOutputStream.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public String toString() {
        return this.location;
    }


}
