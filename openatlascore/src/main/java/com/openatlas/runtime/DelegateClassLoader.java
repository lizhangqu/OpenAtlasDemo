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
package com.openatlas.runtime;

import com.openatlas.framework.Framework;

import org.osgi.framework.Bundle;

import java.util.List;

public class DelegateClassLoader extends ClassLoader {
    public DelegateClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return super.loadClass(className);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        ClassLoadFromBundle.checkInstallBundleIfNeed(className);
        Class<?> loadFromInstalledBundles = ClassLoadFromBundle.loadFromInstalledBundles(className);
        if (loadFromInstalledBundles != null) {
            return loadFromInstalledBundles;
        }
        throw new ClassNotFoundException("Can't find class " + className + printExceptionInfo() + " " + ClassLoadFromBundle.getClassNotFoundReason(className));
    }


    private String printExceptionInfo() {
        StringBuilder stringBuilder = new StringBuilder("installed bundles: ");
        List<Bundle> bundles = Framework.getBundles();
        if (!(bundles == null || bundles.isEmpty())) {
            for (Bundle bundle : Framework.getBundles()) {

                stringBuilder.append(bundle.getLocation());

                stringBuilder.append(":");
            }
        }
        return stringBuilder.toString();
    }
}
