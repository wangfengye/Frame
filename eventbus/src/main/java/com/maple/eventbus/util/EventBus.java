package com.maple.eventbus.util;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maple on 2019/7/9 13:42.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class EventBus {
    private HashMap<Object, List<SubscribeMethod>> cacheMap = new HashMap<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private HashMap<Class<?>, Object> stickyCache = new HashMap<>();

    public static EventBus get() {
        return Holder.INSTANCE;
    }

    public void register(Object o) {
        if (cacheMap.containsKey(o)) return;
        ArrayList<SubscribeMethod> tmp = new ArrayList<>();
        Class<?> t = o.getClass();
        while (t != null) {//获取父类遍历方法.
            Method[] methods = t.getDeclaredMethods();
            for (Method method : methods) {
                Subscribe anno = method.getAnnotation(Subscribe.class);
                if (anno == null) continue;
                SubscribeMethod sm = new SubscribeMethod();
                sm.setMethod(method);
                sm.setMode(anno.value());
                if (method.getParameterTypes().length != 1) {
                    throw new RuntimeException("参数必须有且只有一个");
                }
                sm.setType(method.getParameterTypes()[0]);
                tmp.add(sm);
                if (anno.sticky()&& stickyCache.get(sm.getType())!=null){//推送粘性任务
                    dispatch(sm,o, stickyCache.get(sm.getType()));
                }
            }
            t = t.getSuperclass();
        }
        cacheMap.put(o, tmp);
    }

    public void unRegister(Object o) {
        cacheMap.remove(o);
    }

    public void post(Object o) {
        Class c = o.getClass();
        stickyCache.put(c, o);
        for (final Map.Entry<Object, List<SubscribeMethod>> entry : cacheMap.entrySet()) {
            List<SubscribeMethod> tmp = entry.getValue();
            if (tmp == null || tmp.size() <= 0) continue;
            for (final SubscribeMethod method : tmp) {
                if (method.getType().isAssignableFrom(c)) {
                    dispatch(method,entry.getKey(),o);
                }
            }
        }
    }
    //推送数据
    private void dispatch(final SubscribeMethod method, final Object key, final Object o) {
        switch (method.getMode()) {
            case MAIN:
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    invoke(method, key, o);
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            invoke(method, key, o);
                        }
                    });
                }
                break;
            case IO:
                //new thread 模拟向IO线程池提交任务
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        invoke(method, key, o);
                    }
                }).start();
                break;
        }
    }

    private void invoke(SubscribeMethod method, Object key, Object o) {
        try {
            method.getMethod().invoke(key, o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static class Holder {
        private static final EventBus INSTANCE = new EventBus();
    }

}
