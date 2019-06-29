package com.maple.plugincore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;

import dalvik.system.DexClassLoader;

/**
 * @author maple on 2019/6/20 15:51.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class PluginManager {
    private Context mContext;//上下文

    public void loadPath(String path) {
        try {

            AssetManager assetManager = AssetManager.class.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Activity activity, String className) {
        Intent intent = new Intent(activity, ProxyActivity.class);
        intent.putExtra(ProxyActivity.CLASS_NAME, className);
        activity.startActivity(intent);
    }
}
