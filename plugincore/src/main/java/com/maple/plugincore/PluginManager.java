package com.maple.plugincore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * @author maple on 2019/6/20 15:51.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class PluginManager {
    public static final String TAG = "PluginManager";
    private Context mContext;//上下文
    private DexClassLoader mClassLoader;//类加载器
    private Resources mResources;//资源加载器
    private PackageInfo mPackageInfo;//包信息.

    private PluginManager() {
    }

    public AssetManager getAssets(String path) {
        AssetManager assets = null;
        try {
            assets = AssetManager.class.newInstance();
            Method addAssetPathMethod = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assets, path);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return assets;
    }

    public DexClassLoader getClassLoader() {
        return mClassLoader;
    }

    public Resources getResources() {
        return mResources;
    }

    private static class Holder {
        private static final PluginManager INSTANCE = new PluginManager();
    }

    public static PluginManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * @param path 插件地址
     * @return 加载成功与否
     */
    public boolean loadPlugin(String path) {
        // 获取插件包信息类
        mPackageInfo = mContext.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (mPackageInfo == null) {
            return false;
        }
        // 初始化类加载器
        String optimizedDirectory = mContext.getDir("opt_dex", Context.MODE_PRIVATE).getAbsolutePath();
        mClassLoader = new DexClassLoader(path, optimizedDirectory, null, mContext.getClassLoader());
        //初始化资源
        AssetManager assets = getAssets(path);
        if (assets == null) return false;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        Configuration config = mContext.getResources().getConfiguration();
        mResources = new Resources(assets, metrics, config);
        return true;
    }

    public void showPackageInfo() {
        ActivityInfo[] activities = mPackageInfo.activities;
        StringBuilder builder = new StringBuilder();
        for (ActivityInfo info : activities) {
            builder.append(info.toString()).append("\n");
        }
        Log.i(TAG, "----showPackageInfo: ----\n" + builder.toString());
    }

    public static void startActivity(Activity activity, String className) {
        Intent intent = new Intent(activity, ProxyActivity.class);
        intent.putExtra(ProxyActivity.CLASS_NAME, className);
        activity.startActivity(intent);
    }
}
