package com.maple.douyu.push;

/**
 * Created by maple on 2019/10/25 14:35
 */
public abstract class Pusher {
    public abstract void startPush();

    public abstract void stopPush();

    public abstract void release();
}
