package com.maple.livedatabus.livedata;

/**
 * @author maple on 2019/7/8 17:10.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public interface LifecycleListener {
    void onCreate(int code);
    void onStart(int code);
    void onPause(int code);
    void onDetach(int code);
}
