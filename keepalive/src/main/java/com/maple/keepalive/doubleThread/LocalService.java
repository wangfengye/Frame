package com.maple.keepalive.doubleThread;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.maple.keepalive.ForegroundService;

/**
 * @author maple on 2019/7/11 11:10.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class LocalService extends ForegroundService {
    public static final String TAG = "LocalService";
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "拉活: ");
            bindService(new Intent(LocalService.this, RemoteService.class), conn, Service.BIND_IMPORTANT);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "I become: ");
        bindService(new Intent(LocalService.this, RemoteService.class), conn, Service.BIND_IMPORTANT);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }
    private class  LocalBinder extends Binder{

    }
}
