package cn.edu.zafu.atlasdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.openatlas.framework.Atlas;
import com.openatlas.framework.BundleImpl;

import cn.edu.zafu.corepage.core.CoreConfig;
import zafu.edu.cn.atlasdemo.R;

public class MainActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CoreConfig.setIsOpenAtlas(true);
        ClassLoader bundleClassLoader = Atlas.getInstance().getBundleClassLoader("com.lizhangqu.fragment");
        CoreConfig.setBundleClassLoader(bundleClassLoader);
       // Log.e("TAG",""+bundleClassLoader);
        //openPage("test", null, CoreAnim.none);
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(MainActivity.this, "com.lizhangqu.test.MainActivity");
                startActivity(intent);
            }
        });
        findViewById(R.id.qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(MainActivity.this, "com.lizhangqu.zxing.android.CaptureActivity");
                startActivity(intent);
            }
        });
        findViewById(R.id.fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(MainActivity.this, "com.lizhangqu.fragment.MainActivity");
                startActivity(intent);
            }
        });
        findViewById(R.id.component).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(MainActivity.this, "cn.edu.zafu.component.MainActivity");
                startActivity(intent);
            }
        });



        try {
            BundleImpl bundle = (BundleImpl)Atlas.getInstance().getBundle("com.lizhangqu.test");
            bundle.startBundle();

            ClassLoader cl = Atlas.getInstance().getBundleClassLoader("com.lizhangqu.test");
            Fragment fragment=(Fragment)cl.loadClass("com.lizhangqu.test.TestFragment").newInstance();
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction=supportFragmentManager.beginTransaction();
            transaction.add(R.id.plugin_container,fragment,"testFragment");
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
