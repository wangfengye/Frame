package com.maple.aop.aspectJ;

import com.maple.aop.UnauthActivity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author maple on 2019/7/2 17:40.
 * @version v1.0
 * @see 1040441325@qq.com
 * 添加该注解的方法执行前会检测登录状态,
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginCheck {
    Class value()default UnauthActivity.class;
}
