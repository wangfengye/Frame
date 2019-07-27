package com.maple.ioc.utils;

import android.support.annotation.IdRes;
import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple on 2019/7/5 17:08.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerSetter = "setOnClickListener",listenType = View.OnClickListener.class,callBackListener = "onClick")
public @interface EventBaseOnclick {
    int[] value();
}
