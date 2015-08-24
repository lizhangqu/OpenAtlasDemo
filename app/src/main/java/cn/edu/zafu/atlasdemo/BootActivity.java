package cn.edu.zafu.atlasdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.openatlas.boot.PlatformConfigure;

import zafu.edu.cn.atlasdemo.R;

public class BootActivity extends Activity {
    private BundlesInstallBroadcastReceiver atlasBroadCast;
    private class BundlesInstallBroadcastReceiver extends BroadcastReceiver {

        private BundlesInstallBroadcastReceiver() {
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                startActivity(new Intent(BootActivity.this,MainActivity.class));
                BootActivity.this.finish();
                Log.e("TAG","BundlesInstallBroadcastReceiver,the bundle has install");
            } catch (Exception e) {
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        if ("false".equals(System.getProperty("BUNDLES_INSTALLED", "false"))) {
            this.atlasBroadCast = new BundlesInstallBroadcastReceiver();
            registerReceiver(this.atlasBroadCast, new IntentFilter(PlatformConfigure.ACTION_BROADCAST_BUNDLES_INSTALLED));
        }else{
            startActivity(new Intent(BootActivity.this,MainActivity.class));
            BootActivity.this.finish();
        }



    }

}
