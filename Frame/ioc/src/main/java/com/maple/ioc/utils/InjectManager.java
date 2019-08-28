package com.maple.ioc.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author maple on 2019/7/5 15:43.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class InjectManager {
    public static void inject(Activity activity) {
        // 布局注入
        injectLayout(activity);
        //控件注入
        injectViews(activity);
        //事件注入
        injectEvents(activity);
        try {
            injectEvents2(activity);//第二种事件注入方式
        } catch (Exception e) {
            Log.i("TAG", "inject: "+e.getMessage());
            e.printStackTrace();
        }
    }

    private static void injectEvents2(Activity activity) throws Exception {
        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annos = method.getAnnotations();
            for (Annotation an : annos) {
                Class<? extends Annotation> annotationType = an.annotationType();
                if (annotationType == null) continue;
                EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                if (eventBase == null) continue;
                // 获取元注解内容
                String listenerSetter = eventBase.listenerSetter();
                Class<?> listenerType = eventBase.listenType();
                String callBack = eventBase.callBackListener();
                // 获取注解内容
                Method valueMethod = annotationType.getDeclaredMethod("value");
                int[] viewIds = (int[]) valueMethod.invoke(an);
                //构造callBack代理进行执行
                ListenerHandler invo = new ListenerHandler(activity);
                method.setAccessible(true);
                invo.addMethod(callBack, method);//存储真正执行的方法

                Object o = Proxy.newProxyInstance(activity.getClassLoader(), new Class[]{listenerType}, invo);

                for (int viewId : viewIds) {
                    View view = activity.findViewById(viewId);
                    Method setter = view.getClass().getMethod(listenerSetter, listenerType);//setOnClickListener
                    setter.invoke(view, o);//执行set函数
                }
            }
        }
    }

    private static void injectEvents(final Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            OnClick anno = method.getAnnotation(OnClick.class);
            if (anno == null) continue;
            int[] ids = anno.value();
            method.setAccessible(true);
            for (int id : ids) {
                activity.findViewById(id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            method.invoke(activity, v);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private static void injectViews(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            InjectView anno = f.getAnnotation(InjectView.class);
            if (anno == null) continue;
            f.setAccessible(true);
            try {
                f.set(activity, activity.findViewById(anno.value()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectLayout(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        ContentView anno = clazz.getAnnotation(ContentView.class);
        if (anno == null) return;
        int layoutId = anno.value();
        activity.setContentView(layoutId);
    }
}
