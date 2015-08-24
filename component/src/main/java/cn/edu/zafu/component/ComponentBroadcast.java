package cn.edu.zafu.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-24
 * Time: 12:20
 */
public class ComponentBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "===Plugin BroadcastReceiver onReceive===");
    }
}
