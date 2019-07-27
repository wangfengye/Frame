package com.maple.livedatabus.livedata;

/**
 * @author maple on 2019/7/8 17:04.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class Observer<T> {
    //组件状态
    static final int STATE_INIT = 1;
    static final int STATE_ACTIVE = 2;
    static final int STATE_ONPAUSE = 3;
    private int mState;

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

    public void onChanged(T t) {

    }
}
