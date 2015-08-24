package com.lizhangqu.fragment;

import android.os.Bundle;

import cn.edu.zafu.corepage.base.BaseActivity;
import cn.edu.zafu.corepage.core.CoreAnim;
import cn.edu.zafu.corepage.core.CoreConfig;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        openPage("test", null, CoreAnim.none);
    }
}
