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

import com.openatlas.framework.Framework.ServiceListenerEntry;
import com.openatlas.log.Logger;
import com.openatlas.log.LoggerFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.SynchronousBundleListener;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;

public class BundleContextImpl implements BundleContext {
    static final Logger log;
    BundleImpl bundle;
    boolean isValid;

    public BundleContextImpl() {
        this.isValid = true;
    }

    static {
        log = LoggerFactory.getInstance("BundleContextImpl");
    }

    private void checkValid() {
        if (!this.isValid) {
            throw new IllegalStateException("BundleContext of bundle "
                    + this.bundle
                    + " used after bundle has been stopped or uninstalled.");
        }
    }

    @Override
    public void addBundleListener(BundleListener bundleListener) {
        checkValid();
        List<BundleListener> list = bundleListener instanceof SynchronousBundleListener ? Framework.syncBundleListeners
                : Framework.bundleListeners;
        if (this.bundle.registeredBundleListeners == null) {
            this.bundle.registeredBundleListeners = new ArrayList<BundleListener>();
        }
        if (!this.bundle.registeredBundleListeners.contains(bundleListener)) {
            list.add(bundleListener);
            this.bundle.registeredBundleListeners.add(bundleListener);
        }
    }

    @Override
    public void addFrameworkListener(FrameworkListener frameworkListener) {
        checkValid();
        if (this.bundle.registeredFrameworkListeners == null) {
            this.bundle.registeredFrameworkListeners = new ArrayList<FrameworkListener>();
        }
        if (!this.bundle.registeredFrameworkListeners
                .contains(frameworkListener)) {
            Framework.frameworkListeners.add(frameworkListener);
            this.bundle.registeredFrameworkListeners.add(frameworkListener);
        }
    }

    @Override
    public void addServiceListener(ServiceListener serviceListener, String filter)
            throws InvalidSyntaxException {
        checkValid();
        ServiceListenerEntry serviceListenerEntry = new ServiceListenerEntry(
                serviceListener, filter);
        if (this.bundle.registeredServiceListeners == null) {
            this.bundle.registeredServiceListeners = new ArrayList<ServiceListener>();
        }
        if (isServiceListenerRegistered(serviceListener)) {
            Framework.serviceListeners.remove(serviceListenerEntry);
        } else {
            this.bundle.registeredServiceListeners.add(serviceListener);
        }
        Framework.serviceListeners.add(serviceListenerEntry);
    }

    private boolean isServiceListenerRegistered(ServiceListener serviceListener) {
        ServiceListener[] serviceListenerArr = this.bundle.registeredServiceListeners
                .toArray(new ServiceListener[this.bundle.registeredServiceListeners
                        .size()]);
        for (ServiceListener serviceListener2 : serviceListenerArr) {
            if (serviceListener2 == serviceListener) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addServiceListener(ServiceListener serviceListener) {
        checkValid();
        try {
            addServiceListener(serviceListener, null);
        } catch (InvalidSyntaxException e) {
        }
    }

    @Override
    public Filter createFilter(String filter) throws InvalidSyntaxException {
        if (filter != null) {
            return RFC1960Filter.fromString(filter);
        }
        throw new NullPointerException();
    }

    @Override
    public Bundle getBundle() {
        return this.bundle;
    }

    @Override
    public Bundle getBundle(long bundleID) {
        checkValid();
        return null;
    }

    @Override
    public Bundle[] getBundles() {
        checkValid();
        List<Bundle> bundles = Framework.getBundles();
        Bundle[] bundleArr = bundles.toArray(new Bundle[bundles
                .size()]);
        Bundle[] obj = new Bundle[(bundleArr.length + 1)];
        obj[0] = Framework.systemBundle;
        System.arraycopy(bundleArr, 0, obj, 1, bundleArr.length);
        return obj;
    }

    @Override
    public File getDataFile(String bundleName) {
        checkValid();
        try {
            File file = new File(new File(this.bundle.bundleDir, "/data/"), bundleName);
            file.getParentFile().mkdirs();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getProperty(String key) {
        return (String) Framework.properties.get(key);
    }

    @Override
    public Object getService(ServiceReference serviceReference) {
        checkValid();
        if (serviceReference != null) {
            return ((ServiceReferenceImpl) serviceReference)
                    .getService(this.bundle);
        }
        throw new NullPointerException("Null service reference.");
    }

    @Override
    public ServiceReference[] getServiceReferences(String clazz, String filter)
            throws InvalidSyntaxException {
        Collection collection = null;
        checkValid();
        Filter fromString = RFC1960Filter.fromString(filter);
        if (clazz == null) {
            collection = Framework.services;
        } else {
            List<ServiceReference> list = Framework.classes_services.get(clazz);
            if (list == null) {
                return null;
            }
        }
        List arrayList = new ArrayList();
        ServiceReferenceImpl[] serviceReferenceImplArr = (ServiceReferenceImpl[]) collection
                .toArray(new ServiceReferenceImpl[collection.size()]);
        for (int i = 0; i < serviceReferenceImplArr.length; i++) {
            if (fromString.match(serviceReferenceImplArr[i])) {
                arrayList.add(serviceReferenceImplArr[i]);
            }
        }
        if (Framework.DEBUG_SERVICES && log.isInfoEnabled()) {
            log.info("Framework: REQUESTED SERVICES " + clazz + " " + filter);
            log.info("\tRETURNED " + arrayList);
        }
        return arrayList.size() == 0 ? null : (ServiceReference[]) arrayList
                .toArray(new ServiceReference[arrayList.size()]);
    }

    @Override
    public ServiceReference getServiceReference(String clazz) {
        ServiceReference serviceReference = null;
        checkValid();
        int i = -1;
        long j = 5000;
        List list = Framework.classes_services.get(clazz);
        if (list != null) {
            ServiceReference[] serviceReferenceArr = (ServiceReference[]) list
                    .toArray(new ServiceReference[list.size()]);
            int i2 = 0;
            while (i2 < serviceReferenceArr.length) {
                int intValue;
                ServiceReference serviceReference2;
                int i3;
                Integer num = (Integer) serviceReferenceArr[i2]
                        .getProperty(Constants.SERVICE_RANKING);
                if (num != null) {
                    intValue = num.intValue();
                } else {
                    intValue = 0;
                }
                long longValue = ((Long) serviceReferenceArr[i2]
                        .getProperty(Constants.SERVICE_ID)).longValue();
                if (intValue > i || (intValue == i && longValue < j)) {
                    serviceReference2 = serviceReferenceArr[i2];
                    i3 = intValue;
                } else {
                    longValue = j;
                    i3 = i;
                    serviceReference2 = serviceReference;
                }
                i2++;
                serviceReference = serviceReference2;
                i = i3;
                j = longValue;
            }
            if (Framework.DEBUG_SERVICES && log.isInfoEnabled()) {
                log.info("Framework: REQUESTED SERVICE " + clazz);
                log.info("\tRETURNED " + serviceReference);
            }
        }
        return serviceReference;
    }

    @Override
    public Bundle installBundle(String bundleName) throws BundleException {
        if (bundleName == null) {
            throw new IllegalArgumentException("Location must not be null");
        }
        checkValid();
        return Framework.installNewBundle(bundleName);
    }

    @Override
    public Bundle installBundle(String location, InputStream inputStream)
            throws BundleException {
        if (location == null) {
            throw new IllegalArgumentException("Location must not be null");
        }
        checkValid();
        return Framework.installNewBundle(location, inputStream);
    }

    @Override
    public ServiceRegistration registerService(String[] clazzes, Object service, Dictionary<String, ?> properties) {
        checkValid();
        if (service == null) {
            throw new IllegalArgumentException("Cannot register Component null service");
        }
        ServiceReferenceImpl serviceReferenceImpl = new ServiceReferenceImpl(
                this.bundle, service, properties, clazzes);
        Framework.services.add(serviceReferenceImpl);
        if (this.bundle.registeredServices == null) {
            this.bundle.registeredServices = new ArrayList();
        }
        this.bundle.registeredServices.add(serviceReferenceImpl);
        for (Object addValue : clazzes) {
            Framework.addValue(Framework.classes_services, addValue,
                    serviceReferenceImpl);
        }
        if (Framework.DEBUG_SERVICES && log.isInfoEnabled()) {
            log.info("Framework: REGISTERED SERVICE " + clazzes[0]);
        }
        Framework.notifyServiceListeners(1, serviceReferenceImpl);
        return serviceReferenceImpl.registration;
    }

    @Override
    public ServiceRegistration registerService(String clazz, Object service, Dictionary<String, ?> properties) {
        return registerService(new String[]{clazz}, service,
                properties);
    }

    @Override
    public void removeBundleListener(BundleListener bundleListener) {
        checkValid();
        (bundleListener instanceof SynchronousBundleListener ? Framework.syncBundleListeners
                : Framework.bundleListeners).remove(bundleListener);
        this.bundle.registeredBundleListeners.remove(bundleListener);
        if (this.bundle.registeredBundleListeners.isEmpty()) {
            this.bundle.registeredBundleListeners = null;
        }
    }

    @Override
    public void removeFrameworkListener(FrameworkListener frameworkListener) {
        checkValid();
        Framework.frameworkListeners.remove(frameworkListener);
        this.bundle.registeredFrameworkListeners.remove(frameworkListener);
        if (this.bundle.registeredFrameworkListeners.isEmpty()) {
            this.bundle.registeredFrameworkListeners = null;
        }
    }

    @Override
    public void removeServiceListener(ServiceListener serviceListener) {
        checkValid();
        try {
            Framework.serviceListeners.remove(new ServiceListenerEntry(
                    serviceListener, null));
            this.bundle.registeredServiceListeners.remove(serviceListener);
            if (this.bundle.registeredServiceListeners.isEmpty()) {
                this.bundle.registeredServiceListeners = null;
            }
        } catch (InvalidSyntaxException e) {
        }
    }

    @Override
    public synchronized boolean ungetService(ServiceReference serviceReference) {
        checkValid();
        return ((ServiceReferenceImpl) serviceReference)
                .ungetService(this.bundle);
    }


}
