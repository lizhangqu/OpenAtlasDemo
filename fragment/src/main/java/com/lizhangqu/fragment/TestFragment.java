package com.lizhangqu.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.zafu.corepage.base.BaseFragment;
import cn.edu.zafu.corepage.core.CoreAnim;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-20
 * Time: 13:09
 */
public class TestFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_test,container,false);
        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPage("test1",null, CoreAnim.slide);
            }
        });
        return v;
    }
}
