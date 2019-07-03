package com.maple.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.maple.plugincore.BaseActivity;
import com.maple.plugincore.IPlugin;
import com.maple.plugincore.PluginManager;
import com.maple.plugincore.ProxyActivity;

public class MainActivity extends BaseActivity{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.a).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(that,"我是插件",Toast.LENGTH_SHORT).show();

                // java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String android.content.Context.getPackageName()' on a null object reference
               // startActivity(new Intent(MainActivity.this,SecActivity.class));

               // java.lang.NullPointerException: Attempt to invoke virtual method 'android.app.ActivityThread$ApplicationThread android.app.ActivityThread.getApplicationThread()' on a null object reference
                // startActivity(new Intent(that,SecActivity.class));
                PluginManager.startActivity(that,SecActivity.class.getName());
            }
        });
    }
}
