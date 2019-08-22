package com.maple.rxjava.me;

/**
 * Created by maple on 2019/8/22 16:23
 */
public interface Operator<T, R> extends Func1<Subscribe<? super T>, Subscribe<? super R>> {

}
