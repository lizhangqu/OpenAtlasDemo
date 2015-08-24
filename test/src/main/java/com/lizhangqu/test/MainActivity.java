package com.lizhangqu.test;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction=supportFragmentManager.beginTransaction();
        transaction.add(R.id.container,new TestFragment(),"testFragment");
        transaction.commit();

    }


}
