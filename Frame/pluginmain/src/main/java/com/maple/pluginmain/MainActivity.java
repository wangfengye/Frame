package com.maple.pluginmain;


import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.maple.plugincore.PluginManager;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void gotoPlugin(View view) {
        String className = "com.maple.myapplication.MainActivity";
        PluginManager.startActivity(MainActivity.this, className);
    }

    public void loadPlugin(View view) {
        PluginManager.getInstance().setContext(MainActivity.this);
        String pluginPath = Environment.getExternalStorageDirectory() + "/plugin.apk";
        if (PluginManager.getInstance().loadPlugin(pluginPath)) {
            Toast.makeText(this, "load success", Toast.LENGTH_SHORT).show();
            PluginManager.getInstance().showPackageInfo();
        } else {
            Toast.makeText(this, "load error", Toast.LENGTH_SHORT).show();
        }
    }
}