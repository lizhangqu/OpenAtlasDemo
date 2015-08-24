package cn.edu.zafu.atlasdemo;

import com.openatlas.android.lifecycle.AtlasApp;
import com.openatlas.framework.AtlasConfig;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-11
 * Time: 15:29
 */
public class BootApp extends AtlasApp {
    static{
        //AtlasConfig.DELAY = new String[]{"com.lizhangqu.test","com.lizhangqu.zxing"};
        AtlasConfig.AUTO = new String[]{"com.lizhangqu.test","com.lizhangqu.zxing","com.lizhangqu.fragment","com.lizhangqu.component"};
        //AtlasConfig.STORE = new String[]{"com.lizhangqu.test","com.lizhangqu.zxing"};
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // CoreConfig.init(this);
        //PlatformConfigure.BundleNotFoundActivity=BundleNotFoundActivity.class;
    }
}
