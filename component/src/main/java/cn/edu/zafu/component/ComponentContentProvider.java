package cn.edu.zafu.component;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-24
 * Time: 12:50
 */
public class ComponentContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        Log.e("TAG","ComponentContentProvider onCreate");
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("TAG","ComponentContentProvider query:"+Arrays.asList(projection)+" "+Arrays.asList(selectionArgs)+" "+selectionArgs+" "+sortOrder);
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.e("TAG","ComponentContentProvider getType");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.e("TAG","ComponentContentProvider insert:"+values);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.e("TAG","ComponentContentProvider delete:"+selection+" "+ Arrays.asList(selectionArgs));
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.e("TAG","ComponentContentProvider update:"+values+" "+selection+" "+Arrays.asList(selectionArgs));
        return 0;
    }
}
