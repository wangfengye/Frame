package com.maple.livedatabus.guangfang;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author maple on 2019/6/26 16:42.
 * @version v1.0
 * @see 1040441325@qq.com
 */

public class LiveDataBus {
    public static final String TAG = "LiveDataBus";
    private final Map<String, MyMutableLiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class Holder {
        private static final LiveDataBus INSTANCE = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return Holder.INSTANCE;
    }

    public <T> MutableLiveData<T> with(String target, Class<T> type) {
        if (!bus.containsKey(target)) {
            bus.put(target, new MyMutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(target);
    }

    public MutableLiveData<Object> with(String target) {
        return with(target, Object.class);
    }

    private static class BusObserver<T> implements Observer<T> {
        private Observer<T> observer;

        public BusObserver(Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (observer != null) {
                if (!isCallOnObserve()) observer.onChanged(t);
            }
        }

        private boolean isCallOnObserve() {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                for (StackTraceElement s : stackTrace) {
                    if ("android.arch.lifecycle.LiveData".equals(s.getClassName())
                            && "observeForever".equals(s.getMethodName())) {//若是observeFor调用,则发送的是最近发送的一条消息,无需发送
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static class MyMutableLiveData<T> extends MutableLiveData<T> {
        private Map<Observer, Observer> observerMap = new HashMap<>();

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            super.observe(owner, observer);
            hook(observer);//关闭该hook,即为粘性通道(绑定后立即发送最近一条推送)
        }

        @Override
        public void observeForever(@NonNull Observer<T> observer) {
            super.observeForever(new BusObserver<T>(observer));
        }

        @Override
        public void removeObserver(@NonNull Observer<T> observer) {
            Observer realObserver = null;
            if (observerMap.containsKey(observer)) {
                realObserver = observerMap.remove(observer);
            } else {
                realObserver = observer;
            }
            super.removeObserver(realObserver);
        }

        private void hook(Observer<T> observer) {
            try {//hook mLastVersion
                Field mVersionField = LiveData.class.getDeclaredField("mVersion");
                mVersionField.setAccessible(true);

                Field mObserversField = LiveData.class.getDeclaredField("mObservers");
                mObserversField.setAccessible(true);
                Object mObservers = mObserversField.get(this);
                Class<?> classObservers = mObservers.getClass();
                Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
                methodGet.setAccessible(true);
                Object objectWrapperEntry = methodGet.invoke(mObservers, observer);
                Object objectWrapper = null;
                if (objectWrapperEntry instanceof Map.Entry) {
                    objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
                }
                if (objectWrapper == null) throw new NoSuchFieldException("objectWrapper no found");
                Class<?> classObjectWrapper = objectWrapper.getClass().getSuperclass();
                Field mLastVersionField = classObjectWrapper.getDeclaredField("mLastVersion");
                mLastVersionField.setAccessible(true);
                mLastVersionField.set(objectWrapper, mVersionField.get(this));

                Log.i(TAG, "observe: " + mLastVersionField.get(objectWrapper) + "---" + mVersionField.get(this));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

/** 获取 ObserverWrapper
 Class innerClazz[] = LiveData.class.getDeclaredClasses();
 Class observerClass= null;
 for (Class clazz : innerClazz) {
 if (clazz.getSimpleName().equals("ObserverWrapper")){
 observerClass = clazz;break;
 }
 }
 if (observerClass==null){
 Log.e(TAG, "反射获取ObserverWrapper失败" );return;
 }
 *
 *
 *
 */
}
