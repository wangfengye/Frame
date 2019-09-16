package com.example.simple;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by maple on 2019/9/16 14:33
 */
public class HandlerTest {
    private static final String TAG = "HandlerTest";

    @SuppressWarnings("all")
    /**
     *  Message.Obtain 复用测试.
     *  Message回收机制,是认为,handlerMessage处理完成后,就调用recycle方法回收.
     *  所以 msg这个对象,不应该将引用传递出去,也不能在其中开子线程处理msg,因为,handlerMessage执行完就会回收msg,msg被清空
     *  Mssage 提供了 copyFrom(msg),提供一个不会被自动回收的msg.
     */
    public static void test() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "thread : " + msg.what);
                    }
                }).start();
                Log.i(TAG, "main: " + msg.what);
            }
        };
        handler.sendMessageDelayed(Message.obtain(handler, 1), 1000);
    }
}
