package com.maple.keepalive.piexlHelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * @author maple on 2019/7/11 11:26.
 * @version v1.0
 * @see 1040441325@qq.com\
 * 保活一像素Activity
 */
public class KeepActivity extends Activity {
    private static KeepReceiver keepReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = 1;
        layoutParams.height = 1;
        layoutParams.x = 0;
        layoutParams.y = 0;
        window.setAttributes(layoutParams);
        activity = new WeakReference<>(this);
    }

    private static WeakReference<KeepActivity> activity;

    public static void startKeep(Context context) {
        Intent intent = new Intent(context, KeepActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void stopKeep() {
        if (null != activity) {
            activity.get().finish();
        }
    }

    public static void registerKeep(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        keepReceiver = new KeepReceiver();
        context.registerReceiver(keepReceiver, filter);
    }

    public static void unRegisterKeep(Context context) {
        context.unregisterReceiver(keepReceiver);
    }
}
