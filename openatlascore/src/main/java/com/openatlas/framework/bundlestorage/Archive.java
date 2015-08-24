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
package com.openatlas.framework.bundlestorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.jar.Manifest;

public interface Archive {
    void close();

    Class<?> findClass(String clazz, ClassLoader classLoader)
            throws ClassNotFoundException;

    File findLibrary(String name);

    File getArchiveFile();

    BundleArchiveRevision getCurrentRevision();

    Manifest getManifest() throws IOException;

    List<URL> getResources(String location) throws IOException;

    boolean isDexOpted();

    BundleArchiveRevision newRevision(String packageName, File bundleDir, File bundleFile)
            throws IOException;

    BundleArchiveRevision newRevision(String packageName, File bundleDir,
                                      InputStream bundleInputStream) throws IOException;

    InputStream openAssetInputStream(String assetName) throws IOException;

    InputStream openNonAssetInputStream(String assetName) throws IOException;

    void optDexFile();

    void purge() throws Exception;
}
