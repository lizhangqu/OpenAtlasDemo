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
package com.openatlas.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.Signature;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


public class PackageValidate {


    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;


    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;

    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;

    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;


    private static final boolean DEBUG_JAR = false;


    /**
     * File name in an APK for the Android manifest.
     */
    private static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";


    private String mArchiveSourcePath;


    private int mParseError = 0;

    private static final Object mSync = new Object();
    private static WeakReference<byte[]> mReadBuffer;


    private static final String TAG = "PackageValidate";

    public PackageValidate(String archiveSourcePath) {
        mArchiveSourcePath = archiveSourcePath;
    }


    public Package parsePackage() {
        Package pkg = new Package("");
        pkg.mPath = mArchiveSourcePath;
        return pkg;

    }

    public static Certificate[] loadCertificates(JarFile jarFile, JarEntry je,
                                                 byte[] readBuffer) {
        InputStream is = null;
        try {
            // We must read the stream for the JarEntry to retrieve
            // its certificates.
            is = new BufferedInputStream(jarFile.getInputStream(je));
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
                // not using
            }

            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
            Log.w(TAG, "Exception reading " + je.getName() + " in "
                    + jarFile.getName(), e);
        } catch (RuntimeException e) {
            Log.w(TAG, "Exception reading " + je.getName() + " in "
                    + jarFile.getName(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }


    public boolean isSignaturesSame(Signature[] s1, Signature[] s2) {
        if (s1 == null) {
            return false;
        }
        if (s2 == null) {
            return false;
        }
        HashSet<Signature> set1 = new HashSet<Signature>();
        for (Signature sig : s1) {
            set1.add(sig);
        }
        HashSet<Signature> set2 = new HashSet<Signature>();
        for (Signature sig : s2) {
            set2.add(sig);
        }
        return set1.equals(set2);
    }

    public boolean collectCertificates() {
        Package pkg = parsePackage();
        pkg.mSignatures = null;

        WeakReference<byte[]> readBufferRef;
        byte[] readBuffer = null;
        synchronized (mSync) {
            readBufferRef = mReadBuffer;
            if (readBufferRef != null) {
                mReadBuffer = null;
                readBuffer = readBufferRef.get();
            }
            if (readBuffer == null) {
                readBuffer = new byte[8192];
                readBufferRef = new WeakReference<byte[]>(readBuffer);
            }
        }

        try {
            JarFile jarFile = new JarFile(mArchiveSourcePath);

            Certificate[] certs = null;

            {
                Enumeration<JarEntry> entries = jarFile.entries();
                final Manifest manifest = jarFile.getManifest();
                while (entries.hasMoreElements()) {
                    final JarEntry je = entries.nextElement();
                    if (je.isDirectory()) continue;

                    final String name = je.getName();

                    if (name.startsWith("META-INF/"))
                        continue;

                    if (ANDROID_MANIFEST_FILENAME.equals(name)) {
                        final Attributes attributes = manifest.getAttributes(name);
                        pkg.manifestDigest = ManifestDigest.fromAttributes(attributes);
                    }

                    final Certificate[] localCerts = loadCertificates(jarFile, je, readBuffer);
                    if (DEBUG_JAR) {
                        Log.i(TAG, "File " + mArchiveSourcePath + " entry " + je.getName()
                                + ": certs=" + certs + " ("
                                + (certs != null ? certs.length : 0) + ")");
                    }

                    if (localCerts == null) {
                        Log.e(TAG, "Package " + pkg.packageName
                                + " has no certificates at entry "
                                + je.getName() + "; ignoring!");
                        jarFile.close();
                        mParseError = INSTALL_PARSE_FAILED_NO_CERTIFICATES;
                        return false;
                    } else if (certs == null) {
                        certs = localCerts;
                    } else {
                        // Ensure all certificates match.
                        for (int i = 0; i < certs.length; i++) {
                            boolean found = false;
                            for (int j = 0; j < localCerts.length; j++) {
                                if (certs[i] != null &&
                                        certs[i].equals(localCerts[j])) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found || certs.length != localCerts.length) {
                                Log.e(TAG, "Package " + pkg.packageName
                                        + " has mismatched certificates at entry "
                                        + je.getName() + "; ignoring!");
                                jarFile.close();
                                mParseError = INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
                                return false;
                            }
                        }
                    }
                }
            }
            jarFile.close();

            synchronized (mSync) {
                mReadBuffer = readBufferRef;
            }

            if (certs != null && certs.length > 0) {
                final int N = certs.length;
                pkg.mSignatures = new Signature[certs.length];
                for (int i = 0; i < N; i++) {
                    pkg.mSignatures[i] = new Signature(
                            certs[i].getEncoded());
                }
            } else {
                Log.e(TAG, "Package " + pkg.packageName
                        + " has no certificates; ignoring!");
                mParseError = INSTALL_PARSE_FAILED_NO_CERTIFICATES;
                return false;
            }
        } catch (CertificateEncodingException e) {
            Log.w(TAG, "Exception reading " + mArchiveSourcePath, e);
            mParseError = INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
            return false;
        } catch (IOException e) {
            Log.w(TAG, "Exception reading " + mArchiveSourcePath, e);
            mParseError = INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
            return false;
        } catch (RuntimeException e) {
            Log.w(TAG, "Exception reading " + mArchiveSourcePath, e);
            mParseError = INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
            return false;
        }
        return true;
    }


    public final static class Package {
        public String packageName;
        //For now we only support one application per package.
        public final ApplicationInfo applicationInfo = new ApplicationInfo();
        // If this s a 3rd party app, this is the path of the zip file.
        public String mPath;
        // Signatures that were read from the package.
        public Signature mSignatures[];

        /**
         * Digest suitable for comparing whether this package's manifest is the
         * same as another.
         */
        public ManifestDigest manifestDigest;

        public Package(String _name) {
            packageName = _name;
            applicationInfo.packageName = _name;
            applicationInfo.uid = -1;
        }


    }

}
