package com.maple.rxjava.me;


/**
 * Created by maple on 2019/8/22 14:39
 * 被观察者,
 */
public class Observable<T> {
    private OnSubscribe<T> onSubscribe;

    private Observable(OnSubscribe<T> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public static <T> Observable<T> create(OnSubscribe<T> onSubscribe) {
        return new Observable<>(onSubscribe);
    }

    public void subscribe(Subscribe<? super T> subscribe) {
        onSubscribe.call(subscribe);
    }
    public final <R> Observable<R> map(Func1<? super T, ? extends R> func) {
        return lift(new OperatorMap<>(func));
    }

    private <R> Observable<R> lift(OperatorMap<T, R> trOperatorMap) {
        return new Observable<>(new OnSubscribeLift<>(onSubscribe,trOperatorMap));
    }
}
