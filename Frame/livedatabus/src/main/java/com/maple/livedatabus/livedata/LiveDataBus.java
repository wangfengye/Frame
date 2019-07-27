package com.maple.livedatabus.livedata;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maple on 2019/7/8 18:02.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class LiveDataBus {
    public static final String TAG = "LiveDataBus";
    private final Map<String, LiveData<Object>> bus;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    private static class Holder {
        private static final LiveDataBus INSTANCE = new LiveDataBus();
    }

    public static LiveDataBus get() {
        return LiveDataBus.Holder.INSTANCE;
    }

    public <T> LiveData<T> with(String target, Class<T> type) {
        if (!bus.containsKey(target)) {
            bus.put(target, new LiveData<>());
        }
        return (LiveData<T>) bus.get(target);
    }

    public LiveData<Object> with(String target) {
        return with(target, Object.class);
    }
}
