package com.maple.rxjava.me;

/**
 * Created by maple on 2019/8/22 14:55
 * 角色 女
 * T 代表具备的能力
 */
public abstract class Subscribe<T> {
   public abstract void onNext(T t);
}
