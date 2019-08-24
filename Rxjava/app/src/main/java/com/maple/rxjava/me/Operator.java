package com.maple.rxjava.me;

/**
 * Created by maple on 2019/8/22 16:23
 * 中介
 * T 是转换前内容类型,R是转换后内容类型
 */
 interface Operator<T, R> extends Func1<Subscribe<? super T>, Subscribe<? super R>> {

}
