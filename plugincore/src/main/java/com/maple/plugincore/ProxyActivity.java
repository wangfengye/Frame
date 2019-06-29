package com.maple.plugincore;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import dalvik.system.DexClassLoader;

public class ProxyActivity extends Activity {//动态加载三方activity;
    public static final String CLASS_NAME = "className";
    IPlugin mIPlugin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过第三方插件apkde类加载器,获取出入的类名,然后去实例化类,实例化后调用第三方插件的activity的方法
        String name = getIntent().getStringExtra(CLASS_NAME);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mIPlugin.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIPlugin.onResume();
    }

    @Override
    protected void onPause() {
        mIPlugin.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mIPlugin.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mIPlugin.onDestroy();
        super.onDestroy();
    }
}
