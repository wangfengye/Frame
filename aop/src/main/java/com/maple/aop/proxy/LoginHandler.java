package com.maple.aop.proxy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.maple.aop.MainActivity;
import com.maple.aop.UnauthActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author maple on 2019/7/2 16:49.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class LoginHandler implements InvocationHandler {
    private Object target;

    public LoginHandler(Object o) {
        this.target = o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class[] params = null;
        if (args != null && args.length > 0) {
            params = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                params[i] = args[i].getClass();
            }
        }

        Method method1 = target.getClass().getDeclaredMethod(method.getName(), params);//被代理类中的方法
        JumpTarget anno = method1.getAnnotation(JumpTarget.class);
        Class targetActivity = UnauthActivity.class;
        if (anno != null) targetActivity = anno.value();
        if (MainActivity.login) {
            method.invoke(target, args);
        } else {
            if (target instanceof Activity) {
                ((Activity) target).startActivity(new Intent((Context) target, targetActivity));
            }
        }
        return null;
    }
}
