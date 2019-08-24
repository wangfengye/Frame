package com.maple.rxjava.me;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by maple on 2019/8/23 14:27
 * 线程切换中间类.
 */
public class OnSubscribeOnIo<T> implements OnSubscribe<T> {
    private static ThreadFactory threadFactory = new ThreadFactory() {

        private final ThreadGroup group=Thread.currentThread().getThreadGroup();
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix ="IO-";

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    };
    private static ExecutorService mPool = Executors.newCachedThreadPool(threadFactory);
    private Observable<T> source;

    public OnSubscribeOnIo(Observable<T> source) {
        this.source = source;
    }

    @Override
    public void call(final Subscribe<? super T> subscribe) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                source.subscribe(subscribe);
            }
        };
        mPool.execute(runnable);
    }
}
