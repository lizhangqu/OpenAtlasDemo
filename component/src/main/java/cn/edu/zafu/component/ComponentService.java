package cn.edu.zafu.component;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-24
 * Time: 11:52
 */
public class ComponentService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "===Plugin Service onCreate===");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG", "===Plugin Service onBind===");
        return new BinderImpl();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("TAG", "===Plugin Service onUnbind===");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TAG", "===Plugin Service onStartCommand===");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "===Plugin Service onDestroy===");
    }
    class BinderImpl extends Binder{
        BinderImpl getService(){
            return BinderImpl.this;
        }
        void testMethod(){
            Log.e("TAG","BinderImpl testMethod");
        }
    }

}
