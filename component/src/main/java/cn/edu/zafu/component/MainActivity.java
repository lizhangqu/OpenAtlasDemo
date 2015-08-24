package cn.edu.zafu.component;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("TAG", "===Plugin MainActivity is===" + this);


        findViewById(R.id.start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComponentService.class);
                startService(intent);

            }
        });

        findViewById(R.id.stop_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComponentService.class);
                stopService(intent);

            }
        });

        final ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("TAG", "onServiceDisconnected()");
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("TAG", "onServiceConnected()");
                ComponentService.BinderImpl binder = (ComponentService.BinderImpl) service;
                ComponentService.BinderImpl bindService = binder.getService();
                bindService.testMethod();

            }
        };
        findViewById(R.id.bind_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ComponentService.class);
                Log.i("TAG", "bindService()");
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.unbind_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(conn);
            }
        });
        findViewById(R.id.broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("cn.edu.zafu.component");
                sendBroadcast(intent);

            }
        });
        final ComponentBroadcastDynamic componentBroadcastDynamic = new ComponentBroadcastDynamic();
        findViewById(R.id.register_broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("cn.edu.zafu.component.ComponentBroadcastDynamic");
                registerReceiver(componentBroadcastDynamic, intentFilter);
                Log.e("TAG","ComponentBroadcastDynamic registerReceiver");

            }
        });
        findViewById(R.id.send_broadcast_dynamic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction("cn.edu.zafu.component.ComponentBroadcastDynamic");
                sendBroadcast(intent);

            }
        });
        findViewById(R.id.unregister_broadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "ComponentBroadcastDynamic unregisterReceiver");
                try{
                    unregisterReceiver(componentBroadcastDynamic);
                }catch (Exception e){
                    Log.e("TAG", "ComponentBroadcastDynamic has already unregister");
                }


            }
        });

        findViewById(R.id.contentprovider_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authorities = "cn.edu.zafu.component.ComponentContentProvider";
                Uri CONTENT_URI = Uri.parse("content://" + authorities + "/test");
                ContentValues values = new ContentValues();
                values.put("title", "title11111111");
                Uri uri =MainActivity.this.getContentResolver().insert(CONTENT_URI, values);

            }
        });

        findViewById(R.id.contentprovider_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authorities = "cn.edu.zafu.component.ComponentContentProvider";
                Uri CONTENT_URI = Uri.parse("content://" + authorities + "/test");
                int result =MainActivity.this.getContentResolver().delete(CONTENT_URI, "title=?", new String[]{"title11111111"});
                Log.e("TAG","====="+result);
            }
        });


        findViewById(R.id.contentprovider_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authorities = "cn.edu.zafu.component.ComponentContentProvider";
                Uri CONTENT_URI = Uri.parse("content://" + authorities + "/test");

                ContentValues values = new ContentValues();
                values.put("title", "title11111111");
                int result =MainActivity.this.getContentResolver().update(CONTENT_URI, values, "id=?", new String[]{"1"});
                Log.e("TAG", "=====" + result);
            }
        });

        findViewById(R.id.contentprovider_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("TAG","no impl");
            }
        });
    }


}
