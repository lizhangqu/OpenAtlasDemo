package org.osgi.service.packageadmin;

import org.osgi.framework.Bundle;

public interface PackageAdmin {
    /**
     * Gets the exported package for the specified package name.
     * <p>
     * <p>
     * If there are multiple exported packages with specified name, the exported
     * package with the highest version will be returned.
     *
     * @param name The name of the exported package to be returned.
     * @return The exported package, or <code>null</code> if no exported
     * package with the specified name exists.
     */
    ExportedPackage getExportedPackage(String name);

    ExportedPackage[] getExportedPackages(Bundle bundle);


    /**
     * Forces the update (replacement) or removal of packages exported by the
     * specified bundles.
     * <p>
     * <p>
     * If no bundles are specified, this method will update or remove any
     * packages exported by any bundles that were previously updated or
     * uninstalled since the last call to this method. The technique by which
     * this is accomplished may vary among different Framework implementations.
     * One permissible implementation is to stop and restart the Framework.
     * <p>
     * <p>
     * This method returns to the caller immediately and then performs the
     * following steps on a separate thread:
     * <p>
     * <ol>
     * <li>Compute a graph of bundles starting with the specified bundles. If no
     * bundles are specified, compute a graph of bundles starting with bundle
     * updated or uninstalled since the last call to this method. Add to the
     * graph any bundle that is wired to a package that is currently exported by
     * a bundle in the graph. The graph is fully constructed when there is no
     * bundle outside the graph that is wired to a bundle in the graph. The
     * graph may contain <code>UNINSTALLED</code> bundles that are currently
     * still exporting packages.
     * <p>
     * <li>Each bundle in the graph that is in the <code>ACTIVE</code> state
     * will be stopped as described in the <code>Bundle.stop</code> method.
     * <p>
     * <li>Each bundle in the graph that is in the <code>RESOLVED</code> state
     * is unresolved and thus moved to the <code>INSTALLED</code> state. The
     * effect of this step is that bundles in the graph are no longer
     * <code>RESOLVED</code>.
     * <p>
     * <li>Each bundle in the graph that is in the <code>UNINSTALLED</code>
     * state is removed from the graph and is now completely removed from the
     * Framework.
     * <p>
     * <li>Each bundle in the graph that was in the <code>ACTIVE</code> state
     * prior to Step 2 is started as described in the <code>Bundle.start</code>
     * method, causing all bundles required for the restart to be resolved. It
     * is possible that, as a result of the previous steps, packages that were
     * previously exported no longer are. Therefore, some bundles may be
     * unresolvable until another bundle offering a compatible package for
     * export has been installed in the Framework.
     * <li>A framework event of type
     * <code>FrameworkEvent.PACKAGES_REFRESHED</code> is fired.
     * </ol>
     * <p>
     * <p>
     * For any exceptions that are thrown during any of these steps, a
     * <code>FrameworkEvent</code> of type <code>ERROR</code> is fired
     * containing the exception. The source bundle for these events should be
     * the specific bundle to which the exception is related. If no specific
     * bundle can be associated with the exception then the System Bundle must
     * be used as the source bundle for the event.
     *
     * @param bundles The bundles whose exported packages are to be updated or
     *                removed, or <code>null</code> for all bundles updated or
     *                uninstalled since the last call to this method.
     * @throws SecurityException        If the caller does not have
     *                                  <code>AdminPermission[System Bundle,RESOLVE]</code> and the Java
     *                                  runtime environment supports permissions.
     * @throws IllegalArgumentException If the specified <code>Bundle</code>s
     *                                  were not created by the same framework instance that registered
     *                                  this <code>PackageAdmin</code> service.
     */
    void refreshPackages(Bundle[] bundles);
}
