package com.maple.rxjava;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by maple on 2019/8/22 15:50
 * rxjava使用测试
 */
public class TestRxjava implements ITest {
    private static final String TAG = "TestRxjava";

    public void testDemo() {
        rx.Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                log("发送 原始数据");
                subscriber.onNext("原始数据");
            }
        }).subscribeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        try {//模拟执行耗时
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        log("处理中");
                        return s + "-->处理完成";
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        log("complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        log(e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        log("onNext" + s);
                        this.onCompleted();
                    }
                });
    }

    public void log(String s) {
        Log.i(TAG, Thread.currentThread().getName() + ": " + s);
    }
}
