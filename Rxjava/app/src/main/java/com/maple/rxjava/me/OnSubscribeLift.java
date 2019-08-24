package com.maple.rxjava.me;

/**
 * Created by maple on 2019/8/22 16:21
 */
public class OnSubscribeLift<T, R> implements OnSubscribe<R> {
    private OnSubscribe<T> parent;
    private Operator<? extends R, ? super T> operator;

     OnSubscribeLift(OnSubscribe<T> onSubscribe,  Operator<? extends R, ? super T>  trOperatorMap) {
        parent= onSubscribe;
        operator= trOperatorMap;
    }


    @Override
    public void call(Subscribe<? super R> subscribe) {
        parent.call(operator.call(subscribe));
    }
}
