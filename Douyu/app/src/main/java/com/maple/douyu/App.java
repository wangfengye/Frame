package com.maple.douyu;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;

/**
 * Created by maple on 2019/11/8 15:10
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DoraemonKit.install(this);
    }
}
