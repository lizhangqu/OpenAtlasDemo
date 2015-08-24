/*
 * Copyright (c) OSGi Alliance (2000, 2009). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.framework;

import java.util.Dictionary;

/**
 * A reference to a service.
 * <p>
 * <p>
 * The Framework returns <code>ServiceReference</code> objects from the
 * <code>BundleContext.getServiceReference</code> and
 * <code>BundleContext.getServiceReferences</code> methods.
 * <p>
 * A <code>ServiceReference</code> object may be shared between bundles and
 * can be used to examine the properties of the service and to get the service
 * object.
 * <p>
 * Every service registered in the Framework has a unique
 * <code>ServiceRegistration</code> object and may have multiple, distinct
 * <code>ServiceReference</code> objects referring to it.
 * <code>ServiceReference</code> objects associated with a
 * <code>ServiceRegistration</code> object have the same <code>hashCode</code>
 * and are considered equal (more specifically, their <code>equals()</code>
 * method will return <code>true</code> when compared).
 * <p>
 * If the same service object is registered multiple times,
 * <code>ServiceReference</code> objects associated with different
 * <code>ServiceRegistration</code> objects are not equal.
 *
 * @version $Revision: 6374 $
 * @see BundleContext#getServiceReference
 * @see BundleContext#getServiceReferences
 * @see BundleContext#getService
 */

public interface ServiceReference<T> {
    /**
     * Returns the property value to which the specified property key is mapped
     * in the properties <code>Dictionary</code> object of the service
     * referenced by this <code>ServiceReference</code> object.
     * <p>
     * <p>
     * Property keys are case-insensitive.
     * <p>
     * <p>
     * This method must continue to return property values after the service has
     * been unregistered. This is so references to unregistered services (for
     * example, <code>ServiceReference</code> objects stored in the log) can
     * still be interrogated.
     *
     * @param key The property key.
     * @return The property value to which the key is mapped; <code>null</code>
     * if there is no property named after the key.
     */
    Object getProperty(String key);

    /**
     * Returns an array of the keys in the properties <code>Dictionary</code>
     * object of the service referenced by this <code>ServiceReference</code>
     * object.
     * <p>
     * <p>
     * This method will continue to return the keys after the service has been
     * unregistered. This is so references to unregistered services (for
     * example, <code>ServiceReference</code> objects stored in the log) can
     * still be interrogated.
     * <p>
     * <p>
     * This method is <i>case-preserving </i>; this means that every key in the
     * returned array must have the same case as the corresponding key in the
     * properties <code>Dictionary</code> that was passed to the
     * {@link BundleContext#registerService(String[], Object, Dictionary)} or
     * {@link ServiceRegistration#setProperties} methods.
     *
     * @return An array of property keys.
     */
    String[] getPropertyKeys();

    /**
     * Returns the bundle that registered the service referenced by this
     * <code>ServiceReference</code> object.
     * <p>
     * <p>
     * This method must return <code>null</code> when the service has been
     * unregistered. This can be used to determine if the service has been
     * unregistered.
     *
     * @return The bundle that registered the service referenced by this
     * <code>ServiceReference</code> object; <code>null</code> if that
     * service has already been unregistered.
     * @see BundleContext#registerService(String[], Object, Dictionary)
     */
    Bundle getBundle();

    /**
     * Returns the bundles that are using the service referenced by this
     * <code>ServiceReference</code> object. Specifically, this method returns
     * the bundles whose usage count for that service is greater than zero.
     *
     * @return An array of bundles whose usage count for the service referenced
     * by this <code>ServiceReference</code> object is greater than
     * zero; <code>null</code> if no bundles are currently using that
     * service.
     * @since 1.1
     */
    Bundle[] getUsingBundles();
}
