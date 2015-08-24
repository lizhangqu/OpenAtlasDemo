/**
 * OpenAtlasForAndroid Project
 * <p/>
 * The MIT License (MIT)
 * Copyright (c) 2015 Bunny Blue
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author BunnyBlue
 * *
 */
package com.openAtlas.bundleInfo.maker;

import java.io.File;
import java.util.Collection;

/**
 * @author BunnyBlue
 *
 */
public class ApkPreProcess {
    public static void preProcess(String mDir) {
        Collection<File> apkFiles = org.apache.commons.io.FileUtils.listFiles(new File(mDir), new String[]{"apk"}, true);

        for (File file : apkFiles) {
            String pkgName = PackageLite.parse(file.getAbsolutePath()).packageName;

            pkgName = "lib" + pkgName.replaceAll("\\.", "_") + ".so";
            File targetFile = new File(mDir + File.separator + pkgName);
            if (targetFile.exists())
                targetFile.delete();

            System.out.println("rename: " + file.getName() + " -> " + pkgName);
            while(!file.renameTo(targetFile)) {
                System.gc();
                Thread.yield();
            }
            System.out.println("ApkPreProcess.preProcess() processed " + pkgName);
        }
    }
}
