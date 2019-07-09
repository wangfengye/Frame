package com.maple.livedatabus.livedata;


import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author maple on 2019/7/8 17:01.
 * @version v1.0
 * @see 1040441325@qq.com
 * LiveData 原理Demo
 */
public class LiveData<T> {
    private static final String TAG = "com.maple.livedata";
    //int 组件地址
    private HashMap<Integer, Observer<T>> map = new HashMap<>();
    private HashMap<Integer, List<T>> mPendingData = new HashMap<>();

    public void observe(Activity activity, Observer<T> observer) {
        android.app.FragmentManager manager = activity.getFragmentManager();
        HolderFragment current = (HolderFragment) manager.findFragmentByTag(TAG);
        if (current == null) {
            current = new HolderFragment();
            current.setLifecycleListener(mLifecycleListener);
            manager.beginTransaction().add(current, TAG).commitAllowingStateLoss();
        }
        map.put(activity.hashCode(), observer);

    }

    Handler mh = new Handler(Looper.getMainLooper());

    public void postValue(final T value) {
        ArrayList<Integer> removeKeys = new ArrayList<>();
        for (Map.Entry<Integer, Observer<T>> entry : map.entrySet()) {
            final Observer<T> ob = entry.getValue();
            Integer key = entry.getKey();
            if (ob.getState() == Observer.STATE_ACTIVE) {
                synchronized (this) {
                    mh.post(new Runnable() {
                        @Override
                        public void run() {
                            ob.onChanged(value);
                        }
                    });
                }
            } else if (ob.getState() == Observer.STATE_ONPAUSE) {
                if (mPendingData.get(key) == null) mPendingData.put(key, new ArrayList<T>());
                mPendingData.get(key).add(value);
            } else {
                removeKeys.add(entry.getKey());
            }
        }
        Log.i(TAG, "setValue: "+removeKeys.size());
    }

    public void setValue(T value) {
        ArrayList<Integer> removeKeys = new ArrayList<>();
        for (Map.Entry<Integer, Observer<T>> entry : map.entrySet()) {
            Observer<T> ob = entry.getValue();
            Integer key = entry.getKey();
            if (ob.getState() == Observer.STATE_ACTIVE) {
                ob.onChanged(value);
            } else if (ob.getState() == Observer.STATE_ONPAUSE) {
                if (mPendingData.get(key) == null) mPendingData.put(key, new ArrayList<T>());
                mPendingData.get(key).add(value);
            } else {
                removeKeys.add(entry.getKey());
            }
        }
        Log.i(TAG, "setValue: "+removeKeys.size());
    }

    private LifecycleListener mLifecycleListener = new LifecycleListener() {
        @Override
        public void onCreate(int code) {
            map.get(code).setState(Observer.STATE_INIT);
        }

        @Override
        public void onStart(int code) {
            map.get(code).setState(Observer.STATE_ACTIVE);
            if (mPendingData.get(code) == null || mPendingData.get(code).size() == 0) return;
            for (T t : mPendingData.get(code)) {
                map.get(code).onChanged(t);
            }
        }

        @Override
        public void onPause(int code) {
            map.get(code).setState(Observer.STATE_ONPAUSE);
        }

        @Override
        public void onDetach(int code) {
            map.remove(code);
            if (mPendingData != null) mPendingData.clear();
        }
    };
}
