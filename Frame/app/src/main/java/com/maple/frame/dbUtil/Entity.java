package com.maple.frame.dbUtil;

import android.animation.ValueAnimator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple on 2019/6/14 16:45.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    String value();
}
