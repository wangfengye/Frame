package com.maple.ioc.utils;

import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author maple on 2019/7/5 16:46.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class ListenerHandler implements InvocationHandler {
    private Object target;
    private HashMap<String, Method> mMap;

    public ListenerHandler(Object target) {
        this.target = target;
        mMap = new HashMap<>();
    }

    public void addMethod(String key, Method method) {
        mMap.put(key, method);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object res = null;
        if (target == null) return null;
        String methodName = method.getName();
        res=mMap.get(methodName).invoke(target, args);
        return res;
    }
}
