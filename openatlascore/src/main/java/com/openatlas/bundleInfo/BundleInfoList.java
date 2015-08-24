/**
 * OpenAtlasForAndroid
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
package com.openatlas.bundleInfo;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/******Bundle 从asset的json解析插件对象列表，存放插件信息
 *
 * *********/
public class BundleInfoList {//TODO 与BundleListing合并
    private static BundleInfoList singleton;
    private final String TAG;
    private List<BundleInfo> mBundleInfoList;

    /******Bundle对象 实际上是插件包******/
    public static class BundleInfo {
        /******四大组件列表******/
        public List<String> Components;
        /******Bundle依赖******/
        public List<String> DependentBundles;
        /******Bundle名称******/
        public String bundleName;
        /******Bundle中是否包含动态库******/
        public boolean hasSO;
    }

    private BundleInfoList() {
        this.TAG = BundleInfoList.class.getSimpleName();
    }

    public static synchronized BundleInfoList getInstance() {
        BundleInfoList bundleInfoList;
        synchronized (BundleInfoList.class) {
            if (singleton == null) {
                singleton = new BundleInfoList();
            }
            bundleInfoList = singleton;
        }
        return bundleInfoList;
    }

    /***初始化Bundle列表*****/
    public synchronized boolean init(ArrayList<BundleInfo> linkedList) {
        boolean initilized;

        if (this.mBundleInfoList != null || linkedList == null) {

            Log.i(TAG, "XXXXXBundleInfoList initialization failed.");
            initilized = false;
        } else {
            this.mBundleInfoList = linkedList;
            initilized = true;
        }
        return initilized;
    }

    /*****获取当前Bundle依赖列表名称
     * @param mBundleName Bundle名称
     * @return mDependentBundleNames bundle依赖列表名称
     * ******/
    public List<String> getDependencyForBundle(String mBundleName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.size() == 0) {
            return null;
        }
        for (BundleInfo bundleInfo : this.mBundleInfoList) {
            if (bundleInfo.bundleName.equals(mBundleName)) {
                List<String> mDependentBundleNames = new ArrayList<String>();
                if (!(bundleInfo == null || bundleInfo.DependentBundles == null)) {
                    for (int i = 0; i < bundleInfo.DependentBundles.size(); i++) {
                        if (!TextUtils.isEmpty(bundleInfo.DependentBundles.get(i))) {
                            mDependentBundleNames.add(bundleInfo.DependentBundles.get(i));
                        }
                    }
                }
                return mDependentBundleNames;
            }
        }
        return null;
    }

    /****检测Bundle是否含有动态库
     * @param mBundleName Bundle名称
     * @return boolean 是否含有动态库
     * ******/
    public boolean getHasSO(String mBundleName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.isEmpty()) {
            return false;
        }
        for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
            BundleInfo bundleInfo = this.mBundleInfoList.get(index);
            if (bundleInfo.bundleName.equals(mBundleName)) {
                return bundleInfo.hasSO;
            }
        }
        return false;
    }

    /****获取当前组件的Bundle名称
     * @param mComponentName 组件名称
     *@return bundleName 当前组件对应的包名
     * *******/
    public String getBundleNameForComponet(String mComponentName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.size() == 0) {
            return null;
        }
        for (BundleInfo bundleInfo : this.mBundleInfoList) {
            for (String equals : bundleInfo.Components) {
                if (equals.equals(mComponentName)) {
                    return bundleInfo.bundleName;
                }
            }
        }
        return null;
    }

    /****获取插件列表名称 *
     * ******/
    @Deprecated
    public List<String> getAllBundleNames() {
        if (this.mBundleInfoList == null || this.mBundleInfoList.isEmpty()) {
            return null;
        }
        LinkedList<String> linkedList = new LinkedList<String>();
        for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
            BundleInfo bundleInfo = this.mBundleInfoList.get(index);
            linkedList.add(bundleInfo.bundleName);
        }
        return linkedList;
    }

    /******
     * 获取Bundle信息
     * @param mBundleName Bundle名称插件的包名
     * @author BunnyBlue
     * @return BundleInfo 插件对象
     * ******/
    public BundleInfo getBundleInfo(String mBundleName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.isEmpty()) {
            return null;
        }
        for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
            BundleInfo bundleInfo = this.mBundleInfoList.get(index);
            if (bundleInfo.bundleName.equals(mBundleName)) {
                return bundleInfo;
            }
        }
        return null;
    }

    /****dump  BundleList******/
    public void print() {
        if (this.mBundleInfoList != null && this.mBundleInfoList.isEmpty()) {
            for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
                BundleInfo bundleInfo = this.mBundleInfoList.get(index);
                Log.i(TAG, "BundleName: " + bundleInfo.bundleName);
                for (String str : bundleInfo.Components) {
                    Log.i(TAG, "****components: " + str);
                }
                for (String str2 : bundleInfo.DependentBundles) {
                    Log.i(TAG, "****dependancy: " + str2);
                }
            }
        }
    }
}
