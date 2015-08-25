package cn.edu.zafu.atlasdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.openatlas.framework.Atlas;
import com.openatlas.framework.BundleImpl;

import org.osgi.framework.BundleException;

import java.io.File;

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
        //Log.e("TAG",""+bundleClassLoader);
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
        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File("/sdcard/test-openatlas-debug.apk");
                if (!file.exists()){
                    Toast.makeText(MainActivity.this, "Test Update pkg not exist", Toast.LENGTH_LONG).show();
                }
                try {
                    Atlas.getInstance().updateBundle("com.lizhangqu.test",file);
                } catch (BundleException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Atlas.getInstance().restoreBundle(new String[]{"com.lizhangqu.test"});
            }
        });
        findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file=new File("/sdcard/test-openatlas-debug.apk");
                    if (!file.exists()){
                        Toast.makeText(MainActivity.this, "Test Update pkg not exist", Toast.LENGTH_LONG).show();
                    }
                    Atlas.getInstance().installBundle("com.lizhangqu.test1",file);
                } catch (BundleException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.uninstall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Atlas.getInstance().uninstallBundle("com.lizhangqu.test1");
                } catch (BundleException e) {
                    e.printStackTrace();
                }
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
