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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;

/**
 * An installed bundle in the Framework.
 * <p>
 * <p>
 * A <code>Bundle</code> object is the access point to define the lifecycle of
 * an installed bundle. Each bundle installed in the OSGi environment must have
 * an associated <code>Bundle</code> object.
 * <p>
 * <p>
 * A bundle must have a unique identity, a <code>long</code>, chosen by the
 * Framework. This identity must not change during the lifecycle of a bundle,
 * even when the bundle is updated. Uninstalling and then reinstalling the
 * bundle must create a new unique identity.
 * <p>
 * <p>
 * A bundle can be in one of six states:
 * <ul>
 * <li>{@link #UNINSTALLED}
 * <li>{@link #INSTALLED}
 * <li>{@link #RESOLVED}
 * <li>{@link #STARTING}
 * <li>{@link #STOPPING}
 * <li>{@link #ACTIVE}
 * </ul>
 * <p>
 * Values assigned to these states have no specified ordering; they represent
 * bit values that may be ORed together to determine if a bundle is in one of
 * the valid states.
 * <p>
 * <p>
 * A bundle should only execute code when its state is one of
 * <code>STARTING</code>,<code>ACTIVE</code>, or <code>STOPPING</code>.
 * An <code>UNINSTALLED</code> bundle can not be set to another state; it is a
 * zombie and can only be reached because references are kept somewhere.
 * <p>
 * <p>
 * The Framework is the only entity that is allowed to create
 * <code>Bundle</code> objects, and these objects are only valid within the
 * Framework that created them.
 *
 * @version $Revision: 6906 $
 */
public interface Bundle {
    /**
     * The bundle is uninstalled and may not be used.
     * <p>
     * <p>
     * The <code>UNINSTALLED</code> state is only visible after a bundle is
     * uninstalled; the bundle is in an unusable state but references to the
     * <code>Bundle</code> object may still be available and used for
     * introspection.
     * <p>
     * The value of <code>UNINSTALLED</code> is 0x00000001.
     */
    int UNINSTALLED = 0x00000001;

    /**
     * The bundle is installed but not yet resolved.
     * <p>
     * <p>
     * A bundle is in the <code>INSTALLED</code> state when it has been
     * installed in the Framework but is not or cannot be resolved.
     * <p>
     * This state is visible if the bundle's code dependencies are not resolved.
     * The Framework may attempt to resolve an <code>INSTALLED</code> bundle's
     * code dependencies and move the bundle to the <code>RESOLVED</code>
     * state.
     * <p>
     * The value of <code>INSTALLED</code> is 0x00000002.
     */
    int INSTALLED = 0x00000002;

    /**
     * The bundle is resolved and is able to be started.
     * <p>
     * <p>
     * A bundle is in the <code>RESOLVED</code> state when the Framework has
     * successfully resolved the bundle's code dependencies. These dependencies
     * include:
     * <ul>
     * <li>The bundle's class path from its {@link Constants#BUNDLE_CLASSPATH}
     * Manifest header.
     * <li>The bundle's package dependencies from its
     * {@link Constants#EXPORT_PACKAGE} and {@link Constants#IMPORT_PACKAGE}
     * Manifest headers.
     * <li>The bundle's required bundle dependencies from its
     * {@link Constants#REQUIRE_BUNDLE} Manifest header.
     * <li>A fragment bundle's host dependency from its
     * {@link Constants#FRAGMENT_HOST} Manifest header.
     * </ul>
     * <p>
     * Note that the bundle is not active yet. A bundle must be put in the
     * <code>RESOLVED</code> state before it can be started. The Framework may
     * attempt to resolve a bundle at any time.
     * <p>
     * The value of <code>RESOLVED</code> is 0x00000004.
     */
    int RESOLVED = 0x00000004;

    /**
     * The bundle is in the process of starting.
     * <p>
     * <p>
     * A bundle is in the <code>STARTING</code> state when its
     * {@link #start() start} method is active. A bundle must be in this
     * state when the bundle's {@link BundleActivator#start} is called. If the
     * <code>BundleActivator.start</code> method completes without exception,
     * then the bundle has successfully started and must move to the
     * <code>ACTIVE</code> state.
     * <p>
     * If the bundle has a
     * {@link Constants#ACTIVATION_LAZY lazy activation policy}, then the
     * bundle may remain in this state for some time until the activation is
     * triggered.
     * <p>
     * The value of <code>STARTING</code> is 0x00000008.
     */
    int STARTING = 0x00000008;

    /**
     * The bundle is in the process of stopping.
     * <p>
     * <p>
     * A bundle is in the <code>STOPPING</code> state when its
     * {@link #stop() stop} method is active. A bundle must be in this state
     * when the bundle's {@link BundleActivator#stop} method is called. When the
     * <code>BundleActivator.stop</code> method completes the bundle is
     * stopped and must move to the <code>RESOLVED</code> state.
     * <p>
     * The value of <code>STOPPING</code> is 0x00000010.
     */
    int STOPPING = 0x00000010;

    /**
     * The bundle is now running.
     * <p>
     * <p>
     * A bundle is in the <code>ACTIVE</code> state when it has been
     * successfully started and activated.
     * <p>
     * The value of <code>ACTIVE</code> is 0x00000020.
     */
    int ACTIVE = 0x00000020;

    /**
     * The bundle start operation is transient and the persistent autostart
     * setting of the bundle is not modified.
     * <p>
     * <p>
     * This bit may be set when calling {@link #start()} to notify the
     * framework that the autostart setting of the bundle must not be modified.
     * If this bit is not set, then the autostart setting of the bundle is
     * modified.
     *
     * @see #start()
     * @since 1.4
     */
    int START_TRANSIENT = 0x00000001;

    /**
     * The bundle start operation must activate the bundle according to the
     * bundle's declared
     * {@link Constants#BUNDLE_ACTIVATIONPOLICY activation policy}.
     * <p>
     * <p>
     * This bit may be set when calling {@link #start()} to notify the
     * framework that the bundle must be activated using the bundle's declared
     * activation policy.
     *
     * @see Constants#BUNDLE_ACTIVATIONPOLICY
     * @see #start()
     * @since 1.4
     */
    int START_ACTIVATION_POLICY = 0x00000002;

    /**
     * The bundle stop is transient and the persistent autostart setting of the
     * bundle is not modified.
     * <p>
     * <p>
     * This bit may be set when calling {@link #stop()} to notify the
     * framework that the autostart setting of the bundle must not be modified.
     * If this bit is not set, then the autostart setting of the bundle is
     * modified.
     *
     * @see #stop()
     * @since 1.4
     */
    int STOP_TRANSIENT = 0x00000001;

    /**
     * Request that all certificates used to sign the bundle be returned.
     *
     * @since 1.5
     */
    int SIGNERS_ALL = 1;

    /**
     * Request that only certificates used to sign the bundle that are trusted
     * by the framework be returned.
     *
     * @since 1.5
     */
    int SIGNERS_TRUSTED = 2;

    /**
     * Returns this bundle's current state.
     * <p>
     * <p>
     * A bundle can be in only one state at any time.
     *
     * @return An element of <code>UNINSTALLED</code>,<code>INSTALLED</code>,
     * <code>RESOLVED</code>,<code>STARTING</code>,
     * <code>STOPPING</code>,<code>ACTIVE</code>.
     */
    int getState();

    /**
     * Starts this bundle with no options.
     * <p>
     * <p>
     * This method performs the same function as calling <code>start(0)</code>.
     *
     * @throws BundleException       If this bundle could not be started. This could
     *                               be because a code dependency could not be resolved or the
     *                               specified <code>BundleActivator</code> could not be loaded or
     *                               threw an exception or this bundle is a fragment.
     * @throws IllegalStateException If this bundle has been uninstalled or this
     *                               bundle tries to change its own state.
     * @throws SecurityException     If the caller does not have the appropriate
     *                               <code>AdminPermission[this,EXECUTE]</code>, and the Java Runtime
     *                               Environment supports permissions.
     * @see #start()
     */
    void start() throws BundleException;


    /**
     * Stops this bundle with no options.
     * <p>
     * <p>
     * This method performs the same function as calling <code>stop(0)</code>.
     *
     * @throws BundleException       If this bundle's <code>BundleActivator</code>
     *                               threw an exception or this bundle is a fragment.
     * @throws IllegalStateException If this bundle has been uninstalled or this
     *                               bundle tries to change its own state.
     * @throws SecurityException     If the caller does not have the appropriate
     *                               <code>AdminPermission[this,EXECUTE]</code>, and the Java Runtime
     *                               Environment supports permissions.
     * @see #start()
     */
    void stop() throws BundleException;

    /**
     * Updates this bundle from an <code>InputStream</code>.
     * <p>
     * <p>
     * If the specified <code>InputStream</code> is <code>null</code>, the
     * Framework must create the <code>InputStream</code> from which to read the
     * updated bundle by interpreting, in an implementation dependent manner,
     * this bundle's {@link Constants#BUNDLE_UPDATELOCATION
     * Bundle-UpdateLocation} Manifest header, if present, or this bundle's
     * original location.
     * <p>
     * <p>
     * If this bundle's state is <code>ACTIVE</code>, it must be stopped before
     * the update and started after the update successfully completes.
     * <p>
     * <p>
     * If this bundle has exported any packages that are imported by another
     * bundle, these packages must not be updated. Instead, the previous package
     * version must remain exported until the
     * <code>PackageAdmin.refreshPackages</code> method has been has been called
     * or the Framework is relaunched.
     * <p>
     * <p>
     * The following steps are required to update a bundle:
     * <ol>
     * <li>If this bundle's state is <code>UNINSTALLED</code> then an
     * <code>IllegalStateException</code> is thrown.
     * <p>
     * <li>If this bundle's state is <code>ACTIVE</code>, <code>STARTING</code>
     * or <code>STOPPING</code>, this bundle is stopped as described in the
     * <code>Bundle.stop</code> method. If <code>Bundle.stop</code> throws an
     * exception, the exception is rethrown terminating the update.
     * <p>
     * <li>The updated version of this bundle is read from the input stream and
     * installed. If the Framework is unable to install the updated version of
     * this bundle, the original version of this bundle must be restored and a
     * <code>BundleException</code> must be thrown after completion of the
     * remaining steps.
     * <p>
     * <li>This bundle's state is set to <code>INSTALLED</code>.
     * <p>
     * <li>If the updated version of this bundle was successfully installed, a
     * bundle event of type {@link BundleEvent#UPDATED} is fired.
     * <p>
     * <li>If this bundle's state was originally <code>ACTIVE</code>, the
     * updated bundle is started as described in the <code>Bundle.start</code>
     * method. If <code>Bundle.start</code> throws an exception, a Framework
     * event of type {@link FrameworkEvent#ERROR} is fired containing the
     * exception.
     * </ol>
     * <p>
     * <b>Preconditions </b>
     * <ul>
     * <li><code>getState()</code> not in &#x007B; <code>UNINSTALLED</code>
     * &#x007D;.
     * </ul>
     * <b>Postconditions, no exceptions thrown </b>
     * <ul>
     * <li><code>getState()</code> in &#x007B; <code>INSTALLED</code>,
     * <code>RESOLVED</code>, <code>ACTIVE</code> &#x007D;.
     * <li>This bundle has been updated.
     * </ul>
     * <b>Postconditions, when an exception is thrown </b>
     * <ul>
     * <li><code>getState()</code> in &#x007B; <code>INSTALLED</code>,
     * <code>RESOLVED</code>, <code>ACTIVE</code> &#x007D;.
     * <li>Original bundle is still used; no update occurred.
     * </ul>
     *
     * @param input The <code>InputStream</code> from which to read the new
     *              bundle or <code>null</code> to indicate the Framework must create
     *              the input stream from this bundle's
     *              {@link Constants#BUNDLE_UPDATELOCATION Bundle-UpdateLocation}
     *              Manifest header, if present, or this bundle's original location.
     *              The input stream must always be closed when this method completes,
     *              even if an exception is thrown.
     * @throws BundleException       If the input stream cannot be read or the update
     *                               fails.
     * @throws IllegalStateException If this bundle has been uninstalled or this
     *                               bundle tries to change its own state.
     * @throws SecurityException     If the caller does not have the appropriate
     *                               <code>AdminPermission[this,LIFECYCLE]</code> for both the current
     *                               bundle and the updated bundle, and the Java Runtime Environment
     *                               supports permissions.
     * @see #stop()
     * @see #start()
     */
    void update(InputStream input) throws BundleException;

    void update(File bundleFile) throws BundleException;

    /**
     * Updates this bundle.
     * <p>
     * <p>
     * This method performs the same function as calling
     * {@link #update(InputStream)} with a <code>null</code> InputStream.
     *
     * @throws BundleException       If the update fails.
     * @throws IllegalStateException If this bundle has been uninstalled or this
     *                               bundle tries to change its own state.
     * @throws SecurityException     If the caller does not have the appropriate
     *                               <code>AdminPermission[this,LIFECYCLE]</code> for both the current
     *                               bundle and the updated bundle, and the Java Runtime Environment
     *                               supports permissions.
     * @see #update(InputStream)
     */
    void update() throws BundleException;

    /**
     * Uninstalls this bundle.
     * <p>
     * <p>
     * This method causes the Framework to notify other bundles that this bundle
     * is being uninstalled, and then puts this bundle into the
     * <code>UNINSTALLED</code> state. The Framework must remove any resources
     * related to this bundle that it is able to remove.
     * <p>
     * <p>
     * If this bundle has exported any packages, the Framework must continue to
     * make these packages available to their importing bundles until the
     * <code>PackageAdmin.refreshPackages</code> method has been called or the
     * Framework is relaunched.
     * <p>
     * <p>
     * The following steps are required to uninstall a bundle:
     * <ol>
     * <li>If this bundle's state is <code>UNINSTALLED</code> then an
     * <code>IllegalStateException</code> is thrown.
     * <p>
     * <li>If this bundle's state is <code>ACTIVE</code>, <code>STARTING</code>
     * or <code>STOPPING</code>, this bundle is stopped as described in the
     * <code>Bundle.stop</code> method. If <code>Bundle.stop</code> throws an
     * exception, a Framework event of type {@link FrameworkEvent#ERROR} is
     * fired containing the exception.
     * <p>
     * <li>This bundle's state is set to <code>UNINSTALLED</code>.
     * <p>
     * <li>A bundle event of type {@link BundleEvent#UNINSTALLED} is fired.
     * <p>
     * <li>This bundle and any persistent storage area provided for this bundle
     * by the Framework are removed.
     * </ol>
     * <p>
     * <b>Preconditions </b>
     * <ul>
     * <li><code>getState()</code> not in &#x007B; <code>UNINSTALLED</code>
     * &#x007D;.
     * </ul>
     * <b>Postconditions, no exceptions thrown </b>
     * <ul>
     * <li><code>getState()</code> in &#x007B; <code>UNINSTALLED</code>
     * &#x007D;.
     * <li>This bundle has been uninstalled.
     * </ul>
     * <b>Postconditions, when an exception is thrown </b>
     * <ul>
     * <li><code>getState()</code> not in &#x007B; <code>UNINSTALLED</code>
     * &#x007D;.
     * <li>This Bundle has not been uninstalled.
     * </ul>
     *
     * @throws BundleException       If the uninstall failed. This can occur if
     *                               another thread is attempting to change this bundle's state and
     *                               does not complete in a timely manner.
     * @throws IllegalStateException If this bundle has been uninstalled or this
     *                               bundle tries to change its own state.
     * @throws SecurityException     If the caller does not have the appropriate
     *                               <code>AdminPermission[this,LIFECYCLE]</code>, and the Java
     *                               Runtime Environment supports permissions.
     * @see #stop()
     */
    void uninstall() throws BundleException;

    /**
     * Returns this bundle's Manifest headers and values. This method returns
     * all the Manifest headers and values from the main section of this
     * bundle's Manifest file; that is, all lines prior to the first blank line.
     * <p>
     * <p>
     * Manifest header names are case-insensitive. The methods of the returned
     * <code>Dictionary</code> object must operate on header names in a
     * case-insensitive manner.
     * <p>
     * If a Manifest header value starts with &quot;%&quot;, it must be
     * localized according to the default locale. If no localization is found
     * for a header value, the header value without the leading &quot;%&quot; is
     * returned.
     * <p>
     * <p>
     * For example, the following Manifest headers and values are included if
     * they are present in the Manifest file:
     * <p>
     * <pre>
     *     Bundle-Name
     *     Bundle-Vendor
     *     Bundle-Version
     *     Bundle-Description
     *     Bundle-DocURL
     *     Bundle-ContactAddress
     * </pre>
     * <p>
     * <p>
     * This method must continue to return Manifest header information while
     * this bundle is in the <code>UNINSTALLED</code> state.
     *
     * @return A <code>Dictionary</code> object containing this bundle's
     * Manifest headers and values.
     * @throws SecurityException If the caller does not have the
     *                           appropriate <code>AdminPermission[this,METADATA]</code>, and
     *                           the Java Runtime Environment supports permissions.
     * @see Constants#BUNDLE_LOCALIZATION
     */
    Dictionary<String, String> getHeaders();

    /**
     * Returns this bundle's unique identifier. This bundle is assigned a unique
     * identifier by the Framework when it was installed in the OSGi
     * environment.
     * <p>
     * <p>
     * A bundle's unique identifier has the following attributes:
     * <ul>
     * <li>Is unique and persistent.
     * <li>Is a <code>long</code>.
     * <li>Its value is not reused for another bundle, even after a bundle is
     * uninstalled.
     * <li>Does not change while a bundle remains installed.
     * <li>Does not change when a bundle is updated.
     * </ul>
     * <p>
     * <p>
     * This method must continue to return this bundle's unique identifier while
     * this bundle is in the <code>UNINSTALLED</code> state.
     *
     * @return The unique identifier of this bundle.
     */
    long getBundleId();

    /**
     * Returns this bundle's location identifier.
     * <p>
     * <p>
     * The location identifier is the location passed to
     * <code>BundleContext.installBundle</code> when a bundle is installed.
     * The location identifier does not change while this bundle remains
     * installed, even if this bundle is updated.
     * <p>
     * <p>
     * This method must continue to return this bundle's location identifier
     * while this bundle is in the <code>UNINSTALLED</code> state.
     *
     * @return The string representation of this bundle's location identifier.
     * @throws SecurityException If the caller does not have the
     *                           appropriate <code>AdminPermission[this,METADATA]</code>, and
     *                           the Java Runtime Environment supports permissions.
     */
    String getLocation();

    /**
     * Returns this bundle's <code>ServiceReference</code> list for all
     * services it has registered or <code>null</code> if this bundle has no
     * registered services.
     * <p>
     * <p>
     * If the Java runtime supports permissions, a <code>ServiceReference</code>
     * object to a service is included in the returned list only if the caller
     * has the <code>ServicePermission</code> to get the service using at
     * least one of the named classes the service was registered under.
     * <p>
     * <p>
     * The list is valid at the time of the call to this method, however, as the
     * Framework is a very dynamic environment, services can be modified or
     * unregistered at anytime.
     *
     * @return An array of <code>ServiceReference</code> objects or
     * <code>null</code>.
     * @throws IllegalStateException If this bundle has been
     *                               uninstalled.
     * @see ServiceRegistration
     * @see ServiceReference
     * @see ServicePermission
     */
    ServiceReference[] getRegisteredServices();

    /**
     * Returns this bundle's <code>ServiceReference</code> list for all
     * services it is using or returns <code>null</code> if this bundle is not
     * using any services. A bundle is considered to be using a service if its
     * use count for that service is greater than zero.
     * <p>
     * <p>
     * If the Java Runtime Environment supports permissions, a
     * <code>ServiceReference</code> object to a service is included in the
     * returned list only if the caller has the <code>ServicePermission</code>
     * to get the service using at least one of the named classes the service
     * was registered under.
     * <p>
     * The list is valid at the time of the call to this method, however, as the
     * Framework is a very dynamic environment, services can be modified or
     * unregistered at anytime.
     *
     * @return An array of <code>ServiceReference</code> objects or
     * <code>null</code>.
     * @throws IllegalStateException If this bundle has been
     *                               uninstalled.
     * @see ServiceReference
     * @see ServicePermission
     */
    ServiceReference[] getServicesInUse();

    /**
     * Determines if this bundle has the specified permissions.
     * <p>
     * <p>
     * If the Java Runtime Environment does not support permissions, this method
     * always returns <code>true</code>.
     * <p>
     * <code>permission</code> is of type <code>Object</code> to avoid
     * referencing the <code>java.security.Permission</code> class directly.
     * This is to allow the Framework to be implemented in Java environments
     * which do not support permissions.
     * <p>
     * <p>
     * If the Java Runtime Environment does support permissions, this bundle and
     * all its resources including embedded JAR files, belong to the same
     * <code>java.security.ProtectionDomain</code>; that is, they must share
     * the same set of permissions.
     *
     * @param permission The permission to verify.
     * @return <code>true</code> if this bundle has the specified permission
     * or the permissions possessed by this bundle imply the specified
     * permission; <code>false</code> if this bundle does not have the
     * specified permission or <code>permission</code> is not an
     * <code>instanceof</code> <code>java.security.Permission</code>.
     * @throws IllegalStateException If this bundle has been
     *                               uninstalled.
     */
    boolean hasPermission(Object permission);

    /**
     * Find the specified resource from this bundle's class loader.
     * <p>
     * This bundle's class loader is called to search for the specified
     * resource. If this bundle's state is <code>INSTALLED</code>, this method
     * must attempt to resolve this bundle before attempting to get the
     * specified resource. If this bundle cannot be resolved, then only this
     * bundle must be searched for the specified resource. Imported packages
     * cannot be searched when this bundle has not been resolved. If this bundle
     * is a fragment bundle then <code>null</code> is returned.
     * <p>
     * Note: Jar and zip files are not required to include directory entries.
     * URLs to directory entries will not be returned if the bundle contents do
     * not contain directory entries.
     *
     * @param name The name of the resource. See
     *             <code>ClassLoader.getResource</code> for a description of the
     *             format of a resource name.
     * @return A URL to the named resource, or <code>null</code> if the resource
     * could not be found or if this bundle is a fragment bundle or if
     * the caller does not have the appropriate
     * <code>AdminPermission[this,RESOURCE]</code>, and the Java Runtime
     * Environment supports permissions.
     * @throws IllegalStateException If this bundle has been uninstalled.
     * @since 1.1
     */
    URL getResource(String name);
}
