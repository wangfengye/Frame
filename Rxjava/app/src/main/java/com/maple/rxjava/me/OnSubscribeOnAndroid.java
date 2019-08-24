package com.maple.rxjava.me;


import android.os.Handler;
import android.os.Looper;

/**
 * Created by maple on 2019/8/23 15:58
 */
public class OnSubscribeOnAndroid <T> implements OnSubscribe<T> {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private Observable<T> source;

    public OnSubscribeOnAndroid(Observable<T> source) {
        this.source = source;
    }

    @Override
    public void call(final Subscribe<? super T> subscribe) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                source.subscribe(subscribe);
            }
        });
    }
}
