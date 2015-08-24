package com.lizhangqu.fragment;

import android.app.Application;
import android.util.Log;

import cn.edu.zafu.corepage.core.CoreConfig;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-20
 * Time: 13:26
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "==========App=============");
        CoreConfig.init(this);
    }
}
