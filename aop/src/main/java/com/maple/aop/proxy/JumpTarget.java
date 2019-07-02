package com.maple.aop.proxy;

import com.maple.aop.UnauthActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @author maple on 2019/7/2 17:06.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JumpTarget {
    Class value() default UnauthActivity.class;
}
