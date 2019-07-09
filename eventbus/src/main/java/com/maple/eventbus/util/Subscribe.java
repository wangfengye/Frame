package com.maple.eventbus.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple on 2019/7/9 13:41.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    ThreadMode value()default ThreadMode.MAIN;
    boolean sticky() default false;
}
