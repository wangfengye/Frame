package com.maple.rxjava.me;

/**
 * Created by maple on 2019/8/22 16:29
 */
public class OperatorMap<T, R> implements Operator<R, T> {
    //<?,?> 转换前订阅,转换后订阅
    private Func1<? super T, ? extends R> transform;

     OperatorMap(Func1<? super T, ? extends R> transform) {
        this.transform = transform;
    }
    @Override
    public Subscribe<? super T> call(Subscribe<? super R> subscribe) {
        // 返回转换后的订阅
        return new MapSubscribe<>(subscribe,transform);
    }

    private static class MapSubscribe<T, R> extends Subscribe<T> {
        private Subscribe<? super R> actual;
        private Func1<? super T, ? extends R> transform;

        private MapSubscribe(Subscribe<? super R> actual, Func1<? super T, ? extends R> transform) {
            this.actual = actual;
            this.transform = transform;
        }

        @Override
       public void onNext(T t) {
            R r = transform.call(t);
            actual.onNext(r);
        }
    }
}
