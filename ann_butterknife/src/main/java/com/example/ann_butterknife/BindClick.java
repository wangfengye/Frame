package com.example.ann_butterknife;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by maple on 2019/7/24 15:08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface BindClick {
    int value();
}
