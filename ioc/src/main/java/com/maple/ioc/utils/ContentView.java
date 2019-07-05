package com.maple.ioc.utils;

import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple on 2019/7/5 15:45.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentView {
    @LayoutRes
    int value();
}
