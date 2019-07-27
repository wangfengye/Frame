package com.maple.plugincore;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dalvik.system.DexClassLoader;

public class ProxyActivity extends Activity {//动态加载三方activity;
    public static final String CLASS_NAME = "className";
    IPlugin mIPlugin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过第三方插件apkde类加载器,获取出入的类名,然后去实例化类,实例化后调用第三方插件的activity的方法
        String name = getIntent().getStringExtra(CLASS_NAME);
        try {
            //新建插件activity实例
            Class<?> targetActivityCalzz = PluginManager.getInstance().getClassLoader().loadClass(name);
            Object targetActivityInstance = targetActivityCalzz.newInstance();
            if (targetActivityInstance instanceof IPlugin) {//适配生命周期
                mIPlugin = (IPlugin) targetActivityInstance;
                mIPlugin.attach(ProxyActivity.this);
                mIPlugin.onCreate(new Bundle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mIPlugin.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIPlugin.onResume();
    }

    @Override
    public void onPause() {
        mIPlugin.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mIPlugin.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mIPlugin.onDestroy();
        super.onDestroy();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }
}
