package com.maple.wifiutil.utils;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;

/**
 * @author maple on 2019/7/9 16:09.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class NetworkManager {
    public static final String TAG = "NetworkManager";
    private static volatile NetworkManager instance;

    private Application application;
    private INetwork receiver;
    private HashMap<Object, List<NetworkMethod>> mMap = new HashMap<>();
    private final NetChangeObserver observer = new NetChangeObserver() {
        @Override
        public void onConnect(NetType netType) {
            for (Map.Entry<Object, List<NetworkMethod>> entry : mMap.entrySet()) {
                List<NetworkMethod> tmp = entry.getValue();
                for (NetworkMethod nwMethod : tmp) {
                    if (nwMethod.getType() == NetType.AUTO) {
                        invoke(nwMethod.getMethod(), entry.getKey(), netType);
                    } else if (nwMethod.getType() == netType) {
                        invoke(nwMethod.getMethod(), entry.getKey(), netType);
                    }
                }
            }
        }

        @Override
        public void onDisConnect() {
            for (Map.Entry<Object, List<NetworkMethod>> entry : mMap.entrySet()) {
                List<NetworkMethod> tmp = entry.getValue();
                for (NetworkMethod nwMethod : tmp) {
                    invoke(nwMethod.getMethod(), entry.getKey(), NetType.NONE);
                }
            }
        }
    };

    private void invoke(Method method, Object key, NetType param) {
        try {
            method.invoke(key, param);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void init(Application application) {
        if (application == null) return;
        instance = new NetworkManager(application);
    }

    private NetworkManager(Application application) {
        this.application = application;
        // 广播
       // this.receiver = new NetStateReceiver();
        // NetworkCallback
        this.receiver=new NetWorkCallbackImpl();
        receiver.register(observer, application);


    }



    public static NetworkManager get() {
        if (instance == null) {
            throw new RuntimeException("need init before");
        }
        return instance;
    }

    //不支持类继承注解
    public void register(Object o) {
        if (mMap.containsKey(o)) {
            Log.e(TAG, "register: 重复注册");
            return;
        }
        Class<?> clazz = o.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        ArrayList<NetworkMethod> tmp = new ArrayList<>();
        for (Method method : methods) {
            NetWork anno = method.getAnnotation(NetWork.class);
            if (anno == null) continue;
            if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].isAssignableFrom(NetType.class)) {
                throw new RuntimeException(method.getName() + "必须有且仅有参数NetType");
            }
            tmp.add(new NetworkMethod(method, method.getParameterTypes()[0], anno.value()));
        }
        mMap.put(o, tmp);
    }

    public void unRegister(Object o) {
        mMap.remove(o);
    }

    public void unRegisterAll() {
        mMap.clear();
        receiver.unRegister(application);
    }
}
