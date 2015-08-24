package com.lizhangqu.test;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-24
 * Time: 16:19
 */
public class TestFragment extends Fragment {

    public TestFragment(){
        Context context=App.getContext();
        Log.e("TAG","TestFragment Context:"+context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_test,container,false);
        return view;
    }
}
