package cn.edu.zafu.component;

import android.app.Application;
import android.util.Log;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-24
 * Time: 09:52
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("TAG", "Plugin named Component has init!");
        Log.e("TAG", "===Plugin Application is===" + this);
    }


}
