package com.maple.plugincore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;

/**
 * @author maple on 2019/6/20 16:34.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class BaseActivity extends Activity implements IPlugin {
    protected Activity that;

    @Override
    public void attach(Activity proxyActivity) {
        that = proxyActivity;
    }

    @Override
    public void setContentView(View view) {
        that.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        that.setContentView(layoutResID);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onStart() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onResume() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onPause() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onStop() {

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroy() {

    }

    @Override
    public Resources getResources() {
        return that.getResources();
    }

    @Override
    public Window getWindow() {
        return that.getWindow();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return that.findViewById(id);
    }
}
