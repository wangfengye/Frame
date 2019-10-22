package com.maple.sophix.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author maple on 2019/7/8 11:20.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Replace {
    String clazz();//className

    String method();//methodName
}
