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

/**
 * Defines standard names for the OSGi environment system properties, service
 * properties, and Manifest header attribute keys.
 * <p>
 * <p>
 * The values associated with these keys are of type
 * <code>String</code>, unless otherwise indicated.
 *
 * @version $Revision: 6552 $
 * @since 1.1
 */

public interface Constants {
    /**
     * Location identifier of the OSGi <i>system bundle </i>, which is defined
     * to be &quot;System Bundle&quot;.
     */
    String SYSTEM_BUNDLE_LOCATION = "System Bundle";

    /**
     * Alias for the symbolic name of the OSGi <i>system bundle </i>. It is
     * defined to be &quot;system.bundle&quot;.
     *
     * @since 1.3
     */
    String SYSTEM_BUNDLE_SYMBOLICNAME = "system.bundle";

    /**
     * Manifest header identifying the bundle's category.
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_CATEGORY = "Bundle-Category";

    /**
     * Manifest header identifying a list of directories and embedded JAR files,
     * which are bundle resources used to extend the bundle's classpath.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_CLASSPATH = "Bundle-ClassPath";

    /**
     * Manifest header identifying the bundle's copyright information.
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_COPYRIGHT = "Bundle-Copyright";

    /**
     * Manifest header containing a brief description of the bundle's
     * functionality.
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_DESCRIPTION = "Bundle-Description";

    /**
     * Manifest header identifying the bundle's name.
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_NAME = "Bundle-Name";

    /**
     * Manifest header identifying a number of hardware environments and the
     * native language code libraries that the bundle is carrying for each of
     * these environments.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_NATIVECODE = "Bundle-NativeCode";

    /**
     * Manifest header identifying the packages that the bundle offers to the
     * Framework for export.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String EXPORT_PACKAGE = "Export-Package";

    /**
     * Manifest header identifying the fully qualified class names of the
     * services that the bundle may register (used for informational purposes
     * only).
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @deprecated As of 1.2.
     */
    @Deprecated
    String EXPORT_SERVICE = "Export-Service";

    /**
     * Manifest header identifying the packages on which the bundle depends.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String IMPORT_PACKAGE = "Import-Package";

    /**
     * Manifest header identifying the packages that the bundle may dynamically
     * import during execution.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @since 1.2
     */
    String DYNAMICIMPORT_PACKAGE = "DynamicImport-Package";

    /**
     * Manifest header identifying the fully qualified class names of the
     * services that the bundle requires (used for informational purposes only).
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @deprecated As of 1.2.
     */
    @Deprecated
    String IMPORT_SERVICE = "Import-Service";

    /**
     * Manifest header identifying the bundle's vendor.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_VENDOR = "Bundle-Vendor";

    /**
     * Manifest header identifying the bundle's version.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_VERSION = "Bundle-Version";

    /**
     * Manifest header identifying the bundle's documentation URL, from which
     * further information about the bundle may be obtained.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_DOCURL = "Bundle-DocURL";

    /**
     * Manifest header identifying the contact address where problems with the
     * bundle may be reported; for example, an email address.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_CONTACTADDRESS = "Bundle-ContactAddress";

    /**
     * Manifest header attribute identifying the bundle's activator class.
     * <p>
     * <p>
     * If present, this header specifies the name of the bundle resource class
     * that implements the <code>BundleActivator</code> interface and whose
     * <code>start</code> and <code>stop</code> methods are called by the
     * Framework when the bundle is started and stopped, respectively.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_ACTIVATOR = "Bundle-Activator";

    /**
     * Manifest header identifying the location from which a new bundle version
     * is obtained during a bundle update operation.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     */
    String BUNDLE_UPDATELOCATION = "Bundle-UpdateLocation";

    /**
     * Manifest header attribute identifying the version of a package specified
     * in the Export-Package or Import-Package manifest header.
     *
     * @deprecated As of 1.3. This has been replaced by
     * {@link #VERSION_ATTRIBUTE}.
     */
    @Deprecated
    String PACKAGE_SPECIFICATION_VERSION = "specification-version";

    /**
     * Manifest header attribute identifying the processor required to run
     * native bundle code specified in the Bundle-NativeCode manifest header).
     * <p>
     * <p>
     * The attribute value is encoded in the Bundle-NativeCode manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-NativeCode: http.so ; processor=x86 ...
     * </pre>
     *
     * @see #BUNDLE_NATIVECODE
     */
    String BUNDLE_NATIVECODE_PROCESSOR = "processor";

    /**
     * Manifest header attribute identifying the operating system required to
     * run native bundle code specified in the Bundle-NativeCode manifest
     * header).
     * <p>
     * The attribute value is encoded in the Bundle-NativeCode manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-NativeCode: http.so ; osname=Linux ...
     * </pre>
     *
     * @see #BUNDLE_NATIVECODE
     */
    String BUNDLE_NATIVECODE_OSNAME = "osname";

    /**
     * Manifest header attribute identifying the operating system version
     * required to run native bundle code specified in the Bundle-NativeCode
     * manifest header).
     * <p>
     * The attribute value is encoded in the Bundle-NativeCode manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-NativeCode: http.so ; osversion=&quot;2.34&quot; ...
     * </pre>
     *
     * @see #BUNDLE_NATIVECODE
     */
    String BUNDLE_NATIVECODE_OSVERSION = "osversion";

    /**
     * Manifest header attribute identifying the language in which the native
     * bundle code is written specified in the Bundle-NativeCode manifest
     * header. See ISO 639 for possible values.
     * <p>
     * The attribute value is encoded in the Bundle-NativeCode manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-NativeCode: http.so ; language=nl_be ...
     * </pre>
     *
     * @see #BUNDLE_NATIVECODE
     */
    String BUNDLE_NATIVECODE_LANGUAGE = "language";

    /**
     * Manifest header identifying the required execution environment for the
     * bundle. The service platform may run this bundle if any of the execution
     * environments named in this header matches one of the execution
     * environments it implements.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @since 1.2
     */
    String BUNDLE_REQUIREDEXECUTIONENVIRONMENT = "Bundle-RequiredExecutionEnvironment";

    /**
     * Manifest header identifying the bundle's symbolic name.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @since 1.3
     */
    String BUNDLE_SYMBOLICNAME = "Bundle-SymbolicName";

    /**
     * Manifest header directive identifying whether a bundle is a singleton.
     * The default value is <code>false</code>.
     * <p>
     * <p>
     * The directive value is encoded in the Bundle-SymbolicName manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-SymbolicName: com.acme.module.test; singleton:=true
     * </pre>
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @see #BUNDLE_SYMBOLICNAME
     * @since 1.3
     */
    String SINGLETON_DIRECTIVE = "singleton";

    /**
     * Manifest header directive identifying if and when a fragment may attach
     * to a host bundle. The default value is
     * {@link #FRAGMENT_ATTACHMENT_ALWAYS always}.
     * <p>
     * <p>
     * The directive value is encoded in the Bundle-SymbolicName manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-SymbolicName: com.acme.module.test; fragment-attachment:=&quot;never&quot;
     * </pre>
     *
     * @see #BUNDLE_SYMBOLICNAME
     * @see #FRAGMENT_ATTACHMENT_ALWAYS
     * @see #FRAGMENT_ATTACHMENT_RESOLVETIME
     * @see #FRAGMENT_ATTACHMENT_NEVER
     * @since 1.3
     */
    String FRAGMENT_ATTACHMENT_DIRECTIVE = "fragment-attachment";

    /**
     * Manifest header directive value identifying a fragment attachment type of
     * always. A fragment attachment type of always indicates that fragments are
     * allowed to attach to the host bundle at any time (while the host is
     * resolved or during the process of resolving the host bundle).
     * <p>
     * <p>
     * The directive value is encoded in the Bundle-SymbolicName manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-SymbolicName: com.acme.module.test; fragment-attachment:=&quot;always&quot;
     * </pre>
     *
     * @see #FRAGMENT_ATTACHMENT_DIRECTIVE
     * @since 1.3
     */
    String FRAGMENT_ATTACHMENT_ALWAYS = "always";

    /**
     * Manifest header directive value identifying a fragment attachment type of
     * resolve-time. A fragment attachment type of resolve-time indicates that
     * fragments are allowed to attach to the host bundle only during the
     * process of resolving the host bundle.
     * <p>
     * <p>
     * The directive value is encoded in the Bundle-SymbolicName manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-SymbolicName: com.acme.module.test; fragment-attachment:=&quot;resolve-time&quot;
     * </pre>
     *
     * @see #FRAGMENT_ATTACHMENT_DIRECTIVE
     * @since 1.3
     */
    String FRAGMENT_ATTACHMENT_RESOLVETIME = "resolve-time";

    /**
     * Manifest header directive value identifying a fragment attachment type of
     * never. A fragment attachment type of never indicates that no fragments
     * are allowed to attach to the host bundle at any time.
     * <p>
     * <p>
     * The directive value is encoded in the Bundle-SymbolicName manifest header
     * like:
     * <p>
     * <pre>
     *     Bundle-SymbolicName: com.acme.module.test; fragment-attachment:=&quot;never&quot;
     * </pre>
     *
     * @see #FRAGMENT_ATTACHMENT_DIRECTIVE
     * @since 1.3
     */
    String FRAGMENT_ATTACHMENT_NEVER = "never";

    /**
     * Manifest header identifying the base name of the bundle's localization
     * entries.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @see #BUNDLE_LOCALIZATION_DEFAULT_BASENAME
     * @since 1.3
     */
    String BUNDLE_LOCALIZATION = "Bundle-Localization";

    /**
     * Default value for the <code>Bundle-Localization</code> manifest header.
     *
     * @see #BUNDLE_LOCALIZATION
     * @since 1.3
     */
    String BUNDLE_LOCALIZATION_DEFAULT_BASENAME = "OSGI-INF/l10n/bundle";

    /**
     * Manifest header identifying the symbolic names of other bundles required
     * by the bundle.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @since 1.3
     */
    String REQUIRE_BUNDLE = "Require-Bundle";

    /**
     * Manifest header attribute identifying a range of versions for a bundle
     * specified in the <code>Require-Bundle</code> or
     * <code>Fragment-Host</code> manifest headers. The default value is
     * <code>0.0.0</code>.
     * <p>
     * <p>
     * The attribute value is encoded in the Require-Bundle manifest header
     * like:
     * <p>
     * <pre>
     *     Require-Bundle: com.acme.module.test; bundle-version=&quot;1.1&quot;
     *     Require-Bundle: com.acme.module.test; bundle-version=&quot;[1.0,2.0)&quot;
     * </pre>
     * <p>
     * <p>
     * The bundle-version attribute value uses a mathematical interval notation
     * to specify a range of bundle versions. A bundle-version attribute value
     * specified as a single version means a version range that includes any
     * bundle version greater than or equal to the specified version.
     *
     * @see #REQUIRE_BUNDLE
     * @since 1.3
     */
    String BUNDLE_VERSION_ATTRIBUTE = "bundle-version";

    /**
     * Manifest header identifying the symbolic name of another bundle for which
     * that the bundle is a fragment.
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @since 1.3
     */
    String FRAGMENT_HOST = "Fragment-Host";

    /**
     * Manifest header attribute is used for selection by filtering based upon
     * system properties.
     * <p>
     * <p>
     * The attribute value is encoded in manifest headers like:
     * <p>
     * <pre>
     *     Bundle-NativeCode: libgtk.so; selection-filter=&quot;(ws=gtk)&quot;; ...
     * </pre>
     *
     * @see #BUNDLE_NATIVECODE
     * @since 1.3
     */
    String SELECTION_FILTER_ATTRIBUTE = "selection-filter";

    /**
     * Manifest header identifying the bundle manifest version. A bundle
     * manifest may express the version of the syntax in which it is written by
     * specifying a bundle manifest version. Bundles exploiting OSGi Release 4,
     * or later, syntax must specify a bundle manifest version.
     * <p>
     * The bundle manifest version defined by OSGi Release 4 or, more
     * specifically, by version 1.3 of the OSGi Core Specification is "2".
     * <p>
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @since 1.3
     */
    String BUNDLE_MANIFESTVERSION = "Bundle-ManifestVersion";

    /**
     * Manifest header attribute identifying the version of a package specified
     * in the Export-Package or Import-Package manifest header.
     * <p>
     * <p>
     * The attribute value is encoded in the Export-Package or Import-Package
     * manifest header like:
     * <p>
     * <pre>
     *     Import-Package: org.osgi.framework; version=&quot;1.1&quot;
     * </pre>
     *
     * @see #EXPORT_PACKAGE
     * @see #IMPORT_PACKAGE
     * @since 1.3
     */
    String VERSION_ATTRIBUTE = "version";

    /**
     * Manifest header attribute identifying the symbolic name of a bundle that
     * exports a package specified in the Import-Package manifest header.
     * <p>
     * <p>
     * The attribute value is encoded in the Import-Package manifest header
     * like:
     * <p>
     * <pre>
     *     Import-Package: org.osgi.framework; bundle-symbolic-name=&quot;com.acme.module.test&quot;
     * </pre>
     *
     * @see #IMPORT_PACKAGE
     * @since 1.3
     */
    String BUNDLE_SYMBOLICNAME_ATTRIBUTE = "bundle-symbolic-name";

    /**
     * Manifest header directive identifying the resolution type in the
     * Import-Package or Require-Bundle manifest header. The default value is
     * {@link #RESOLUTION_MANDATORY mandatory}.
     * <p>
     * <p>
     * The directive value is encoded in the Import-Package or Require-Bundle
     * manifest header like:
     * <p>
     * <pre>
     *     Import-Package: org.osgi.framework; resolution:=&quot;optional&quot;
     *     Require-Bundle: com.acme.module.test; resolution:=&quot;optional&quot;
     * </pre>
     *
     * @see #IMPORT_PACKAGE
     * @see #REQUIRE_BUNDLE
     * @see #RESOLUTION_MANDATORY
     * @see #RESOLUTION_OPTIONAL
     * @since 1.3
     */
    String RESOLUTION_DIRECTIVE = "resolution";

    /**
     * Manifest header directive value identifying a mandatory resolution type.
     * A mandatory resolution type indicates that the import package or require
     * bundle must be resolved when the bundle is resolved. If such an import or
     * require bundle cannot be resolved, the module fails to resolve.
     * <p>
     * <p>
     * The directive value is encoded in the Import-Package or Require-Bundle
     * manifest header like:
     * <p>
     * <pre>
     *     Import-Package: org.osgi.framework; resolution:=&quot;manditory&quot;
     *     Require-Bundle: com.acme.module.test; resolution:=&quot;manditory&quot;
     * </pre>
     *
     * @see #RESOLUTION_DIRECTIVE
     * @since 1.3
     */
    String RESOLUTION_MANDATORY = "mandatory";

    /**
     * Manifest header directive value identifying an optional resolution type.
     * An optional resolution type indicates that the import or require bundle
     * is optional and the bundle may be resolved without the import or require
     * bundle being resolved. If the import or require bundle is not resolved
     * when the bundle is resolved, the import or require bundle may not be
     * resolved before the bundle is refreshed.
     * <p>
     * <p>
     * The directive value is encoded in the Import-Package or Require-Bundle
     * manifest header like:
     * <p>
     * <pre>
     *     Import-Package: org.osgi.framework; resolution:=&quot;optional&quot;
     *     Require-Bundle: com.acme.module.test; resolution:=&quot;optional&quot;
     * </pre>
     *
     * @see #RESOLUTION_DIRECTIVE
     * @since 1.3
     */
    String RESOLUTION_OPTIONAL = "optional";

    /**
     * Manifest header directive identifying a list of packages that an exported
     * package uses.
     * <p>
     * <p>
     * The directive value is encoded in the Export-Package manifest header
     * like:
     * <p>
     * <pre>
     *     Export-Package: org.osgi.util.tracker; uses:=&quot;org.osgi.framework&quot;
     * </pre>
     *
     * @see #EXPORT_PACKAGE
     * @since 1.3
     */
    String USES_DIRECTIVE = "uses";

    /**
     * Manifest header directive identifying a list of classes to include in the
     * exported package.
     * <p>
     * <p>
     * This directive is used by the Export-Package manifest header to identify
     * a list of classes of the specified package which must be allowed to be
     * exported. The directive value is encoded in the Export-Package manifest
     * header like:
     * <p>
     * <pre>
     *     Export-Package: org.osgi.framework; include:=&quot;MyClass*&quot;
     * </pre>
     * <p>
     * <p>
     * This directive is also used by the Bundle-ActivationPolicy manifest
     * header to identify the packages from which class loads will trigger lazy
     * activation. The directive value is encoded in the Bundle-ActivationPolicy
     * manifest header like:
     * <p>
     * <pre>
     *     Bundle-ActivationPolicy: lazy; include:=&quot;org.osgi.framework&quot;
     * </pre>
     *
     * @see #EXPORT_PACKAGE
     * @see #BUNDLE_ACTIVATIONPOLICY
     * @since 1.3
     */
    String INCLUDE_DIRECTIVE = "include";

    /**
     * Manifest header directive identifying a list of classes to exclude in the
     * exported package..
     * <p>
     * This directive is used by the Export-Package manifest header to identify
     * a list of classes of the specified package which must not be allowed to
     * be exported. The directive value is encoded in the Export-Package
     * manifest header like:
     * <p>
     * <pre>
     *     Export-Package: org.osgi.framework; exclude:=&quot;*Impl&quot;
     * </pre>
     * <p>
     * <p>
     * This directive is also used by the Bundle-ActivationPolicy manifest
     * header to identify the packages from which class loads will not trigger
     * lazy activation. The directive value is encoded in the
     * Bundle-ActivationPolicy manifest header like:
     * <p>
     * <pre>
     *     Bundle-ActivationPolicy: lazy; exclude:=&quot;org.osgi.framework&quot;
     * </pre>
     *
     * @see #EXPORT_PACKAGE
     * @see #BUNDLE_ACTIVATIONPOLICY
     * @since 1.3
     */
    String EXCLUDE_DIRECTIVE = "exclude";

    /**
     * Manifest header directive identifying names of matching attributes which
     * must be specified by matching Import-Package statements in the
     * Export-Package manifest header.
     * <p>
     * <p>
     * The directive value is encoded in the Export-Package manifest header
     * like:
     * <p>
     * <pre>
     *     Export-Package: org.osgi.framework; mandatory:=&quot;bundle-symbolic-name&quot;
     * </pre>
     *
     * @see #EXPORT_PACKAGE
     * @since 1.3
     */
    String MANDATORY_DIRECTIVE = "mandatory";

    /**
     * Manifest header directive identifying the visibility of a required bundle
     * in the Require-Bundle manifest header. The default value is
     * {@link #VISIBILITY_PRIVATE private}.
     * <p>
     * <p>
     * The directive value is encoded in the Require-Bundle manifest header
     * like:
     * <p>
     * <pre>
     *     Require-Bundle: com.acme.module.test; visibility:=&quot;reexport&quot;
     * </pre>
     *
     * @see #REQUIRE_BUNDLE
     * @see #VISIBILITY_PRIVATE
     * @see #VISIBILITY_REEXPORT
     * @since 1.3
     */
    String VISIBILITY_DIRECTIVE = "visibility";

    /**
     * Manifest header directive value identifying a private visibility type. A
     * private visibility type indicates that any packages that are exported by
     * the required bundle are not made visible on the export signature of the
     * requiring bundle.
     * <p>
     * <p>
     * The directive value is encoded in the Require-Bundle manifest header
     * like:
     * <p>
     * <pre>
     *     Require-Bundle: com.acme.module.test; visibility:=&quot;private&quot;
     * </pre>
     *
     * @see #VISIBILITY_DIRECTIVE
     * @since 1.3
     */
    String VISIBILITY_PRIVATE = "private";

    /**
     * Manifest header directive value identifying a reexport visibility type. A
     * reexport visibility type indicates any packages that are exported by the
     * required bundle are re-exported by the requiring bundle. Any arbitrary
     * arbitrary matching attributes with which they were exported by the
     * required bundle are deleted.
     * <p>
     * <p>
     * The directive value is encoded in the Require-Bundle manifest header
     * like:
     * <p>
     * <pre>
     *     Require-Bundle: com.acme.module.test; visibility:=&quot;reexport&quot;
     * </pre>
     *
     * @see #VISIBILITY_DIRECTIVE
     * @since 1.3
     */
    String VISIBILITY_REEXPORT = "reexport";

    /**
     * Manifest header directive identifying the type of the extension fragment.
     * <p>
     * <p>
     * The directive value is encoded in the Fragment-Host manifest header like:
     * <p>
     * <pre>
     *     Fragment-Host: system.bundle; extension:=&quot;framework&quot;
     * </pre>
     *
     * @see #FRAGMENT_HOST
     * @see #EXTENSION_FRAMEWORK
     * @see #EXTENSION_BOOTCLASSPATH
     * @since 1.3
     */
    String EXTENSION_DIRECTIVE = "extension";

    /**
     * Manifest header directive value identifying the type of extension
     * fragment. An extension fragment type of framework indicates that the
     * extension fragment is to be loaded by the framework's class loader.
     * <p>
     * <p>
     * The directive value is encoded in the Fragment-Host manifest header like:
     * <p>
     * <pre>
     *     Fragment-Host: system.bundle; extension:=&quot;framework&quot;
     * </pre>
     *
     * @see #EXTENSION_DIRECTIVE
     * @since 1.3
     */
    String EXTENSION_FRAMEWORK = "framework";

    /**
     * Manifest header directive value identifying the type of extension
     * fragment. An extension fragment type of bootclasspath indicates that the
     * extension fragment is to be loaded by the boot class loader.
     * <p>
     * <p>
     * The directive value is encoded in the Fragment-Host manifest header like:
     * <p>
     * <pre>
     *     Fragment-Host: system.bundle; extension:=&quot;bootclasspath&quot;
     * </pre>
     *
     * @see #EXTENSION_DIRECTIVE
     * @since 1.3
     */
    String EXTENSION_BOOTCLASSPATH = "bootclasspath";

    /**
     * Manifest header identifying the bundle's activation policy.
     * <p>
     * The attribute value may be retrieved from the <code>Dictionary</code>
     * object returned by the <code>Bundle.getHeaders</code> method.
     *
     * @see #ACTIVATION_LAZY
     * @see #INCLUDE_DIRECTIVE
     * @see #EXCLUDE_DIRECTIVE
     * @since 1.4
     */
    String BUNDLE_ACTIVATIONPOLICY = "Bundle-ActivationPolicy";

    /**
     * Bundle activation policy declaring the bundle must be activated when the
     * first class load is made from the bundle.
     * <p>
     * A bundle with the lazy activation policy that is started with the
     * {@link Bundle#START_ACTIVATION_POLICY START_ACTIVATION_POLICY} option
     * will wait in the {@link Bundle#STARTING STARTING} state until the first
     * class load from the bundle occurs. The bundle will then be activated
     * before the class is returned to the requester.
     * <p>
     * The activation policy value is specified as in the
     * Bundle-ActivationPolicy manifest header like:
     * <p>
     * <pre>
     *       Bundle-ActivationPolicy: lazy
     * </pre>
     *
     * @see #BUNDLE_ACTIVATIONPOLICY
     * @see Bundle#start()
     * @see Bundle#START_ACTIVATION_POLICY
     * @since 1.4
     */
    String ACTIVATION_LAZY = "lazy";

    /**
     * Framework environment property identifying the Framework version.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     */
    String FRAMEWORK_VERSION = "org.osgi.framework.version";

    /**
     * Framework environment property identifying the Framework implementation
     * vendor.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     */
    String FRAMEWORK_VENDOR = "org.osgi.framework.vendor";

    /**
     * Framework environment property identifying the Framework implementation
     * language (see ISO 639 for possible values).
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     */
    String FRAMEWORK_LANGUAGE = "org.osgi.framework.language";

    /**
     * Framework environment property identifying the Framework host-computer's
     * operating system.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     */
    String FRAMEWORK_OS_NAME = "org.osgi.framework.os.name";

    /**
     * Framework environment property identifying the Framework host-computer's
     * operating system version number.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     */
    String FRAMEWORK_OS_VERSION = "org.osgi.framework.os.version";

    /**
     * Framework environment property identifying the Framework host-computer's
     * processor name.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     */
    String FRAMEWORK_PROCESSOR = "org.osgi.framework.processor";

    /**
     * Framework environment property identifying execution environments
     * provided by the Framework.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @since 1.2
     */
    String FRAMEWORK_EXECUTIONENVIRONMENT = "org.osgi.framework.executionenvironment";

    /**
     * Framework environment property identifying packages for which the
     * Framework must delegate class loading to the parent class loader of the
     * bundle.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @see #FRAMEWORK_BUNDLE_PARENT
     * @since 1.3
     */
    String FRAMEWORK_BOOTDELEGATION = "org.osgi.framework.bootdelegation";

    /**
     * Framework environment property identifying packages which the system
     * bundle must export.
     * <p>
     * <p>
     * If this property is not specified then the framework must calculate a
     * reasonable default value for the current execution environment.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @since 1.3
     */
    String FRAMEWORK_SYSTEMPACKAGES = "org.osgi.framework.system.packages";

    /**
     * Framework environment property identifying extra packages which the
     * system bundle must export from the current execution environment.
     * <p>
     * <p>
     * This property is useful for configuring extra system packages in addition
     * to the system packages calculated by the framework.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @see #FRAMEWORK_SYSTEMPACKAGES
     * @since 1.5
     */
    String FRAMEWORK_SYSTEMPACKAGES_EXTRA = "org.osgi.framework.system.packages.extra";

    /**
     * Framework environment property identifying whether the Framework supports
     * framework extension bundles.
     * <p>
     * <p>
     * As of version 1.4, the value of this property must be <code>true</code>.
     * The Framework must support framework extension bundles.
     * <p>
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @since 1.3
     */
    String SUPPORTS_FRAMEWORK_EXTENSION = "org.osgi.supports.framework.extension";

    /**
     * Framework environment property identifying whether the Framework supports
     * bootclasspath extension bundles.
     * <p>
     * <p>
     * If the value of this property is <code>true</code>, then the Framework
     * supports bootclasspath extension bundles. The default value is
     * <code>false</code>.
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @since 1.3
     */
    String SUPPORTS_BOOTCLASSPATH_EXTENSION = "org.osgi.supports.bootclasspath.extension";

    /**
     * Framework environment property identifying whether the Framework supports
     * fragment bundles.
     * <p>
     * <p>
     * As of version 1.4, the value of this property must be <code>true</code>.
     * The Framework must support fragment bundles.
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @since 1.3
     */
    String SUPPORTS_FRAMEWORK_FRAGMENT = "org.osgi.supports.framework.fragment";

    /**
     * Framework environment property identifying whether the Framework supports
     * the {@link #REQUIRE_BUNDLE Require-Bundle} manifest header.
     * <p>
     * <p>
     * As of version 1.4, the value of this property must be <code>true</code>.
     * The Framework must support the <code>Require-Bundle</code> manifest
     * header.
     * <p>
     * The value of this property may be retrieved by calling the
     * <code>BundleContext.getProperty</code> method.
     *
     * @since 1.3
     */
    String SUPPORTS_FRAMEWORK_REQUIREBUNDLE = "org.osgi.supports.framework.requirebundle";

    /**
     * Specifies the type of security manager the framework must use. If not
     * specified then the framework will not set the VM security manager.
     *
     * @see #FRAMEWORK_SECURITY_OSGI
     * @since 1.5
     */
    String FRAMEWORK_SECURITY = "org.osgi.framework.security";

    /**
     * Specifies that a security manager that supports all security aspects of
     * the OSGi core specification including postponed conditions must be
     * installed.
     * <p>
     * <p>
     * If this value is specified and there is a security manager already
     * installed, then a <code>SecurityException</code> must be thrown when the
     * Framework is initialized.
     *
     * @see #FRAMEWORK_SECURITY
     * @since 1.5
     */
    String FRAMEWORK_SECURITY_OSGI = "org/osgi";

    /**
     * Specified the persistent storage area used by the framework. The value of
     * this property must be a valid file path in the file system to a
     * directory. If the specified directory does not exist then the framework
     * will create the directory. If the specified path exists but is not a
     * directory or if the framework fails to create the storage directory, then
     * framework initialization must fail. The framework is free to use this
     * directory as it sees fit. This area can not be shared with anything else.
     * <p>
     * If this property is not set, the framework should use a reasonable
     * platform default for the persistent storage area.
     *
     * @since 1.5
     */
    String FRAMEWORK_STORAGE = "org.osgi.framework.storage";

    /**
     * Specifies if and when the persistent storage area for the framework
     * should be cleaned. If this property is not set, then the framework
     * storage area must not be cleaned.
     *
     * @see #FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT
     * @since 1.5
     */
    String FRAMEWORK_STORAGE_CLEAN = "org.osgi.framework.storage.clean";

    /**
     * Specifies that the framework storage area must be cleaned before the
     * framework is initialized for the first time. Subsequent inits, starts or
     * updates of the framework will not result in cleaning the framework
     * storage area.
     *
     * @since 1.5
     */
    String FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT = "onFirstInit";

    /**
     * Specifies a comma separated list of additional library file extensions
     * that must be used when a bundle's class loader is searching for native
     * libraries. If this property is not set, then only the library name
     * returned by <code>System.mapLibraryName(String)</code> will be used to
     * search. This is needed for certain operating systems which allow more
     * than one extension for a library. For example, AIX allows library
     * extensions of <code>.a</code> and <code>.so</code>, but
     * <code>System.mapLibraryName(String)</code> will only return names with
     * the <code>.a</code> extension.
     *
     * @since 1.5
     */
    String FRAMEWORK_LIBRARY_EXTENSIONS = "org.osgi.framework.library.extensions";

    /**
     * Specifies an optional OS specific command to set file permissions on
     * extracted native code. On some operating systems, it is required that
     * native libraries be set to executable. This optional property allows you
     * to specify the command. For example, on a UNIX style OS, this property
     * could have the following value.
     * <p>
     * <pre>
     * chmod +rx ${abspath}
     * </pre>
     * <p>
     * The <code>${abspath}</code> is used by the framework to substitute the
     * actual absolute file path.
     *
     * @since 1.5
     */
    String FRAMEWORK_EXECPERMISSION = "org.osgi.framework.command.execpermission";

    /**
     * Specifies the trust repositories used by the framework. The value is a
     * <code>java.io.File.pathSeparator</code> separated list of valid file
     * paths to files that contain key stores of type <code>JKS</code>. The
     * framework will use the key stores as trust repositories to authenticate
     * certificates of trusted signers. The key stores are only used as
     * read-only trust repositories to access public keys. No passwords are
     * required to access the key stores' public keys.
     * <p>
     * Note that framework implementations are allowed to use other trust
     * repositories in addition to the trust repositories specified by this
     * property. How these other trust repositories are configured and populated
     * is implementation specific.
     *
     * @since 1.5
     */
    String FRAMEWORK_TRUST_REPOSITORIES = "org.osgi.framework.trust.repositories";

    /**
     * Specifies the current windowing system. The framework should provide a
     * reasonable default if this is not set.
     *
     * @since 1.5
     */
    String FRAMEWORK_WINDOWSYSTEM = "org.osgi.framework.windowsystem";

    /**
     * Specifies the beginning start level of the framework.
     *
     * @see "Core Specification, section 8.2.3."
     * @since 1.5
     */
    String FRAMEWORK_BEGINNING_STARTLEVEL = "org.osgi.framework.startlevel.beginning";

    /**
     * Specifies the parent class loader type for all bundle class loaders.
     * Default value is {@link #FRAMEWORK_BUNDLE_PARENT_BOOT boot}.
     *
     * @see #FRAMEWORK_BUNDLE_PARENT_BOOT
     * @see #FRAMEWORK_BUNDLE_PARENT_EXT
     * @see #FRAMEWORK_BUNDLE_PARENT_APP
     * @see #FRAMEWORK_BUNDLE_PARENT_FRAMEWORK
     * @since 1.5
     */
    String FRAMEWORK_BUNDLE_PARENT = "org.osgi.framework.bundle.parent";

    /**
     * Specifies to use of the boot class loader as the parent class loader for
     * all bundle class loaders.
     *
     * @see #FRAMEWORK_BUNDLE_PARENT
     * @since 1.5
     */
    String FRAMEWORK_BUNDLE_PARENT_BOOT = "boot";

    /**
     * Specifies to use the extension class loader as the parent class loader
     * for all bundle class loaders.
     *
     * @see #FRAMEWORK_BUNDLE_PARENT
     * @since 1.5
     */
    String FRAMEWORK_BUNDLE_PARENT_EXT = "ext";

    /**
     * Specifies to use the application class loader as the parent class loader
     * for all bundle class loaders.  Depending on how the framework is
     * launched, this may refer to the same class loader as
     * {@link #FRAMEWORK_BUNDLE_PARENT_FRAMEWORK}.
     *
     * @see #FRAMEWORK_BUNDLE_PARENT
     * @since 1.5
     */
    String FRAMEWORK_BUNDLE_PARENT_APP = "app";

    /**
     * Specifies to use the framework class loader as the parent class loader
     * for all bundle class loaders. The framework class loader is the class
     * loader used to load the framework implementation.  Depending on how the
     * framework is launched, this may refer to the same class loader as
     * {@link #FRAMEWORK_BUNDLE_PARENT_APP}.
     *
     * @see #FRAMEWORK_BUNDLE_PARENT
     * @since 1.5
     */
    String FRAMEWORK_BUNDLE_PARENT_FRAMEWORK = "framework";

	/*
     * Service properties.
	 */

    /**
     * Service property identifying all of the class names under which a service
     * was registered in the Framework. The value of this property must be of
     * type <code>String[]</code>.
     * <p>
     * <p>
     * This property is set by the Framework when a service is registered.
     */
    String OBJECTCLASS = "objectClass";

    /**
     * Service property identifying a service's registration number. The value
     * of this property must be of type <code>Long</code>.
     * <p>
     * <p>
     * The value of this property is assigned by the Framework when a service is
     * registered. The Framework assigns a unique value that is larger than all
     * previously assigned values since the Framework was started. These values
     * are NOT persistent across restarts of the Framework.
     */
    String SERVICE_ID = "service.id";

    /**
     * Service property identifying a service's persistent identifier.
     * <p>
     * <p>
     * This property may be supplied in the <code>properties</code>
     * <code>Dictionary</code> object passed to the
     * <code>BundleContext.registerService</code> method. The value of this
     * property must be of type <code>String</code>, <code>String[]</code>, or
     * <code>Collection</code> of <code>String</code>.
     * <p>
     * <p>
     * A service's persistent identifier uniquely identifies the service and
     * persists across multiple Framework invocations.
     * <p>
     * <p>
     * By convention, every bundle has its own unique namespace, starting with
     * the bundle's identifier (see {@link Bundle#getBundleId}) and followed by
     * a dot (.). A bundle may use this as the prefix of the persistent
     * identifiers for the services it registers.
     */
    String SERVICE_PID = "service.pid";

    /**
     * Service property identifying a service's ranking number.
     * <p>
     * <p>
     * This property may be supplied in the <code>properties
     * Dictionary</code> object passed to the
     * <code>BundleContext.registerService</code> method. The value of this
     * property must be of type <code>Integer</code>.
     * <p>
     * <p>
     * <p>
     * The default ranking is zero (0). A service with a ranking of
     * <code>Integer.MAX_VALUE</code> is very likely to be returned as the
     * default service, whereas a service with a ranking of
     * <code>Integer.MIN_VALUE</code> is very unlikely to be returned.
     * <p>
     * <p>
     * If the supplied property value is not of type <code>Integer</code>, it is
     * deemed to have a ranking value of zero.
     */
    String SERVICE_RANKING = "service.ranking";

    /**
     * Service property identifying a service's vendor.
     * <p>
     * <p>
     * This property may be supplied in the properties <code>Dictionary</code>
     * object passed to the <code>BundleContext.registerService</code> method.
     */
    String SERVICE_VENDOR = "service.vendor";

    /**
     * Service property identifying a service's description.
     * <p>
     * <p>
     * This property may be supplied in the properties <code>Dictionary</code>
     * object passed to the <code>BundleContext.registerService</code> method.
     */
    String SERVICE_DESCRIPTION = "service.description";
}
