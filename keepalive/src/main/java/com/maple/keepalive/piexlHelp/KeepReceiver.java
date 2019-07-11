package com.maple.keepalive.piexlHelp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author maple on 2019/7/11 11:36.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class KeepReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            KeepActivity.startKeep(context);
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            KeepActivity.stopKeep();
        }
    }
}
