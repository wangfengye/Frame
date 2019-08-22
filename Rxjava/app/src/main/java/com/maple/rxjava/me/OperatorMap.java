package com.maple.rxjava.me;

/**
 * Created by maple on 2019/8/22 16:29
 */
public class OperatorMap<T, R> implements Operator<R, T> {
    Func1<? super T, ? extends R> transform;

    public OperatorMap(Func1<? super T, ? extends R> transform) {
        this.transform = transform;
    }

    @Override
    public Subscribe<? super T> call(Subscribe<? super R> subscrible) {
        return null;
    }

    private class MapSubscribe<T, R> extends Subscribe<T> {
        private Subscribe<? super R> actual;
        private Func1<? super T, ? extends R> transform;

        public MapSubscribe(Subscribe<R> actual, Func1<? super T, ? extends R> transform) {
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
