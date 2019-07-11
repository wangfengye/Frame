package com.maple.keepalive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maple.keepalive.doubleThread.LocalService;
import com.maple.keepalive.doubleThread.RemoteService;
import com.maple.keepalive.piexlHelp.KeepActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 双进程保活
        startService(new Intent(this,LocalService.class));
        startService(new Intent(this,RemoteService.class));
        // 一像素
        KeepActivity.registerKeep(this);
    }
}
