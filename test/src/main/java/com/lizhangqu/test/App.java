package com.lizhangqu.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-24
 * Time: 16:25
 */
public class App extends Application{
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        Log.e("TAG","Application onCreate");
    }

    public static Context getContext(){
        return mContext;
    }
}
