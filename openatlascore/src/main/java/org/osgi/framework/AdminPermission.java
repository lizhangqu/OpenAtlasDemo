package org.osgi.framework;

import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A bundle's authority to perform specific privileged administrative operations
 * on or to get sensitive information about a bundle. The actions for this
 * permission are:
 * <p>
 * <pre>
 *  Action               Methods
 *  class                Bundle.loadClass
 *  execute              Bundle.start
 *                       Bundle.stop
 *                       StartLevel.setBundleStartLevel
 *  extensionLifecycle   BundleContext.installBundle for extension bundles
 *                       Bundle.update for extension bundles
 *                       Bundle.uninstall for extension bundles
 *  lifecycle            BundleContext.installBundle
 *                       Bundle.update
 *                       Bundle.uninstall
 *  listener             BundleContext.addBundleListener for SynchronousBundleListener
 *                       BundleContext.removeBundleListener for SynchronousBundleListener
 *  metadata             Bundle.getHeaders
 *                       Bundle.getLocation
 *  resolve              PackageAdmin.refreshPackages
 *                       PackageAdmin.resolveBundles
 *  resource             Bundle.getResource
 *                       Bundle.getResources
 *                       Bundle.getEntry
 *                       Bundle.getEntryPaths
 *                       Bundle.findEntries
 *                       Bundle resource/entry URL creation
 *  startlevel           StartLevel.setStartLevel
 *                       StartLevel.setInitialBundleStartLevel
 *  context              Bundle.getBundleContext
 * </pre>
 * <p>
 * <p>
 * The special action &quot;*&quot; will represent all actions. The
 * <code>resolve</code> action is implied by the <code>class</code>,
 * <code>execute</code> and <code>resource</code> actions.
 * <p>
 * The name of this permission is a filter expression. The filter gives access
 * to the following attributes:
 * <ul>
 * <li>signer - A Distinguished Name chain used to sign a bundle. Wildcards in a
 * DN are not matched according to the filter string rules, but according to the
 * rules defined for a DN chain.</li>
 * <li>location - The location of a bundle.</li>
 * <li>id - The bundle ID of the designated bundle.</li>
 * <li>name - The symbolic name of a bundle.</li>
 * </ul>
 * Filter attribute names are processed in a case sensitive manner.
 *
 * @version $Revision: 7743 $
 */
public final class AdminPermission extends BasicPermission {
    private static final long serialVersionUID = 7348630669480294335L;

    public AdminPermission() {
        super("AdminPermission");
    }

    /**
     * Create a new AdminPermission.
     * <p>
     * This constructor must only be used to create a permission that is going
     * to be checked.
     * <p>
     * Examples:
     * <p>
     * <pre>
     * (signer=\*,o=ACME,c=US)
     * (&amp;(signer=\*,o=ACME,c=US)(name=com.acme.*)(location=http://www.acme.com/bundles/*))
     * (id&gt;=1)
     * </pre>
     * <p>
     * <p>
     * When a signer key is used within the filter expression the signer value
     * must escape the special filter chars ('*', '(', ')').
     * <p>
     * Null arguments are equivalent to "*".
     *
     * @param filter  A filter expression that can use signer, location, id, and
     *                name keys. A value of &quot;*&quot; or <code>null</code> matches
     *                all bundle. Filter attribute names are processed in a case
     *                sensitive manner.
     * @param actions <code>class</code>, <code>execute</code>,
     *                <code>extensionLifecycle</code>, <code>lifecycle</code>,
     *                <code>listener</code>, <code>metadata</code>, <code>resolve</code>
     *                , <code>resource</code>, <code>startlevel</code> or
     *                <code>context</code>. A value of "*" or <code>null</code>
     *                indicates all actions.
     * @throws IllegalArgumentException If the filter has an invalid syntax.
     */
    public AdminPermission(String filter, String actions) {
        // arguments will be null if called from a PermissionInfo defined with
        // no args
        this();
    }


    /**
     * Determines if the specified permission is implied by this object. This
     * method throws an exception if the specified permission was not
     * constructed with a bundle.
     * <p>
     * <p>
     * This method returns <code>true</code> if the specified permission is an
     * AdminPermission AND
     * <ul>
     * <li>this object's filter matches the specified permission's bundle ID,
     * bundle symbolic name, bundle location and bundle signer distinguished
     * name chain OR</li>
     * <li>this object's filter is "*"</li>
     * </ul>
     * AND this object's actions include all of the specified permission's
     * actions.
     * <p>
     * Special case: if the specified permission was constructed with "*"
     * filter, then this method returns <code>true</code> if this object's
     * filter is "*" and this object's actions include all of the specified
     * permission's actions
     *
     * @param p The requested permission.
     * @return <code>true</code> if the specified permission is implied by this
     * object; <code>false</code> otherwise.
     */
    @Override
    public boolean implies(Permission p) {
        return p instanceof AdminPermission;
    }

    /**
     * Determines the equality of two <code>AdminPermission</code> objects.
     *
     * @param obj The object being compared for equality with this object.
     * @return <code>true</code> if <code>obj</code> is equivalent to this
     * <code>AdminPermission</code>; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AdminPermission;
    }

    /**
     * Returns a new <code>PermissionCollection</code> object suitable for
     * storing <code>AdminPermission</code>s.
     *
     * @return A new <code>PermissionCollection</code> object.
     */
    @Override
    public PermissionCollection newPermissionCollection() {
        return new AdminPermissionCollection();
    }

}

final class AdminPermissionCollection extends PermissionCollection {
    private static final long serialVersionUID = -7328900470853808407L;
    private boolean hasElement;

    public AdminPermissionCollection() {
        this.hasElement = false;
    }

    @Override
    public void add(Permission permission) {
        if (!(permission instanceof AdminPermission)) {
            throw new IllegalArgumentException("invalid permission: "
                    + permission);
        } else if (isReadOnly()) {
            throw new SecurityException(
                    "attempt to add Component Permission to Component readonly PermissionCollection");
        } else {
            this.hasElement = true;
        }
    }

    @Override
    public boolean implies(Permission permission) {
        return this.hasElement && (permission instanceof AdminPermission);
    }

    /**
     * Returns an enumeration of all <code>AdminPermission</code> objects in the
     * container.
     *
     * @return Enumeration of all <code>AdminPermission</code> objects.
     */

    @Override
    public Enumeration elements() {
        return new Enumeration() {
            private boolean more;

            {
                this.more = AdminPermissionCollection.this.hasElement;
            }

            @Override
            public boolean hasMoreElements() {
                return this.more;
            }

            @Override
            public Object nextElement() {
                if (this.more) {
                    this.more = false;
                    return new AdminPermission();
                }
                throw new NoSuchElementException();
            }
        };
    }
}