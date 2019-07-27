package com.maple.eventbus.util;

import java.lang.reflect.Method;

/**
 * @author maple on 2019/7/9 13:55.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class SubscribeMethod {
    private Method method;
    private ThreadMode mode;
    private Class<?> type;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getMode() {
        return mode;
    }

    public void setMode(ThreadMode mode) {
        this.mode = mode;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
