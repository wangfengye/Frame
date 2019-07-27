package com.maple.keepalive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

/**
 * @author maple on 2019/7/11 10:57.
 * @version v1.0
 * @see 1040441325@qq.com
 * 前台服务保活.
 */
public class ForegroundService extends Service {
    private static final int SERVICE_ID = 0x1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 26) {
            startForeground(SERVICE_ID, new Notification());
            startService(new Intent(this, InnerService.class));//构造同id的Notication的服务,关闭一个以取消notification
        } else {
            //最不起眼的notification
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("channel", "xxx", NotificationManager.IMPORTANCE_MIN);
            if (null != manager) {
                manager.createNotificationChannel(channel);
                Notification noti = new NotificationCompat.Builder(this, "channel").build();
                startForeground(SERVICE_ID, noti);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static class InnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
    }
}
