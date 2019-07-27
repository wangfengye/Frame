package com.maple.ioc.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple on 2019/7/5 16:34.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {
    // 方法名
    String listenerSetter();
    //监听的对象,
    Class<?> listenType();
    //3.真正被调用的方法
    String callBackListener();
}
