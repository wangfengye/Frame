package com.maple.aop.aspectJ;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.util.Log;
import android.view.View;

import com.maple.aop.MainActivity;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author maple on 2019/7/2 17:34.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Aspect
public class LoginAop {
    public static final String TAG = "LoginCheck";

    @Pointcut("execution(@com.maple.aop.aspectJ.LoginCheck * *..*.*(..))")
    public void checkMethod() {
    }

    @Pointcut("execution(@com.maple.aop.aspectJ.LogTime * *..*.*(..))")
    public void logTime() {
    }

    @Around("checkMethod()")
    public void onLoginCheck(ProceedingJoinPoint point) throws Throwable {
        Log.i(TAG, "onLoginCheck: ");
        if (MainActivity.login) {
            point.proceed();
        } else {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            LoginCheck anno = method.getAnnotation(LoginCheck.class);
            Class clazz = anno.value();
            Object target = point.getTarget();
            if (target instanceof Activity) {
                ((Activity) target).startActivity(new Intent((Context) target, clazz));
            }

        }
    }

    @Around("logTime()")
    public void onLogTime(ProceedingJoinPoint point) throws Throwable {
        String clazz = point.getTarget().getClass().getSimpleName();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        long startTime = System.currentTimeMillis();
        point.proceed();
        Log.i(TAG, clazz + "." + methodName + " 执行耗时: " + (System.currentTimeMillis() - startTime));
    }
}
