package com.lizhangqu.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.zafu.corepage.base.BaseFragment;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-20
 * Time: 13:09
 */
public class TestFragment1 extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_test1,container,false);
        return v;
    }
}
