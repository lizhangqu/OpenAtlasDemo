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
package com.openatlas.boot;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/****
 * Provider需要做一些处理，因为ContentProvider在Application onCreate之前初始化，so，做一个桥<br>
 * 告诉系统这个ContentProvider初始化完毕，都可以用了，实际上还没完成，只是一个空实现，当需要的类能加载的时候对正常的类进行实例化
 * @author BunnyBlue
 *
 * *****/
public class ProviderProxy extends ContentProvider {
    ContentProvider mContentProvider;
    String mTargetProvider;

    /**
     * @param mTargetProvider 真正的ContentProvider类名
     */
    public ProviderProxy(String mTargetProvider) {
        this.mTargetProvider = mTargetProvider;
    }

    /*****验证ContentProvider实现类是否加载*****/
    protected ContentProvider getContentProvider() {
        if (this.mContentProvider != null) {
            return this.mContentProvider;
        }
        try {
            Class<?> loadClass = Globals.getClassLoader().loadClass(mTargetProvider);
            if (loadClass != null) {
                Constructor<?> constructor = loadClass.getConstructor();
                constructor.setAccessible(true);
                this.mContentProvider = (ContentProvider) constructor.newInstance();
                Field declaredField = ContentProvider.class.getDeclaredField("mContext");
                declaredField.setAccessible(true);
                declaredField.set(this.mContentProvider, getContext());
                declaredField = ContentProvider.class.getDeclaredField("mReadPermission");
                declaredField.setAccessible(true);
                declaredField.set(this.mContentProvider, getReadPermission());
                declaredField = ContentProvider.class.getDeclaredField("mWritePermission");
                declaredField.setAccessible(true);
                declaredField.set(this.mContentProvider, getWritePermission());
                declaredField = ContentProvider.class.getDeclaredField("mPathPermissions");
                declaredField.setAccessible(true);
                declaredField.set(this.mContentProvider, getPathPermissions());
                this.mContentProvider.onCreate();
                return this.mContentProvider;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        ContentProvider mContentProvider = getContentProvider();
        if (mContentProvider != null) {
            return mContentProvider.query(uri, projection, selection, selectionArgs, sortOrder);
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        ContentProvider mContentProvider = getContentProvider();
        if (mContentProvider != null) {
            return mContentProvider.getType(uri);
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        ContentProvider mContentProvider = getContentProvider();
        if (mContentProvider != null) {
            return mContentProvider.insert(uri, contentValues);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        ContentProvider mContentProvider = getContentProvider();
        if (mContentProvider != null) {
            return mContentProvider.delete(uri, selection, selectionArgs);
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        ContentProvider mContentProvider = getContentProvider();
        if (mContentProvider != null) {
            return mContentProvider.update(uri, values, selection, selectionArgs);
        }
        return 0;
    }
}