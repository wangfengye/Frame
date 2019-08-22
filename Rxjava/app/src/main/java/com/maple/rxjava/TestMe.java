package com.maple.rxjava;

import android.util.Log;

import com.maple.rxjava.me.Observable;
import com.maple.rxjava.me.OnSubscribe;
import com.maple.rxjava.me.Subscribe;

/**
 * Created by maple on 2019/8/22 17:52
 */
public class TestMe implements ITest {
    public static final String TAG = "TestMe";
    @Override
    public void testDemo() {
        Observable.create(new OnSubscribe<String>() {
            @Override
            public void call(Subscribe<? super String> subscribe) {
                log("发送:原始数据");
                subscribe.onNext("原始数据");
            }
        }).subscribe(new Subscribe<String>() {
            @Override
            public void onNext(String s) {
                log("onNext"+s);
            }
        });
    }

    @Override
    public void log(String s) {
        Log.i(TAG, Thread.currentThread().getName() + ": " + s);
    }
}
