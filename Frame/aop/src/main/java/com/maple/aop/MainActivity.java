package com.maple.aop;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.maple.aop.aspectJ.LogTime;
import com.maple.aop.aspectJ.LoginCheck;
import com.maple.aop.proxy.IJump;
import com.maple.aop.proxy.LoginHandler;
import com.maple.aop.proxy.JumpTarget;

import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity implements IJump {
    public static boolean login;
    private IJump proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proxy = (IJump) Proxy.newProxyInstance(getClassLoader(), new Class[]{IJump.class},
                new LoginHandler(this));
        new AlertDialog.Builder(this).create().show();
    }

    public void gotoA(View view) {
        proxy.jump();
    }

    @Override
    @JumpTarget(BActivity.class)
    public void jump() {
        startActivity(new Intent(MainActivity.this, AActivity.class));
    }

    @LogTime
    @LoginCheck(BActivity.class)
    public void gotoB(View view) {
        startActivity(new Intent(MainActivity.this, AActivity.class));
    }
    public void checkbox(View view) {
        login = !login;
        if (login) ((TextView) view).setText("当前已登录,点击切换");
        if (!login) ((TextView) view).setText("当前未登录,点击切换");
    }
}
