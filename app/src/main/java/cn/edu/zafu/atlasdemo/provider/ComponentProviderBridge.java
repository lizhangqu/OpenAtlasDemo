package cn.edu.zafu.atlasdemo.provider;

import com.openatlas.boot.ProviderProxy;

/**
 * User:lizhangqu(513163535@qq.com)
 * Date:2015-08-24
 * Time: 13:59
 */
public class ComponentProviderBridge extends ProviderProxy{

    public ComponentProviderBridge() {
        super("cn.edu.zafu.component.ComponentContentProvider");
    }
}
