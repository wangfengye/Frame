package com.maple.hotfix;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.maple.hotfix.test.FixDemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showClassLoader();
    }

    private void showClassLoader() {//展示classLoader
        ClassLoader loader = MainActivity.this.getClassLoader();
        while (loader != null) {
            Log.i(TAG, "showClassLoader: " + loader.toString());
            loader = loader.getParent();
        }
    }

    private void hotFix() {
        // 外存复制到内存,模拟网络下载
        File sourece = new File(Environment.getExternalStorageDirectory(), "fixed.dex");
        //getDir(odex,Content.MODE_PRIVITE) data.user/0/包名/app_odex/
        File target = new File(getDir("odex",
                Context.MODE_PRIVATE).getAbsoluteFile() + "/fixed.dex");
        if (target.exists()) {
            target.delete();
        }
        if (!sourece.exists()) {
            Toast.makeText(this, "无修复包", Toast.LENGTH_SHORT).show();
        }
        copyFile(sourece, target);
        Toast.makeText(this,"加载修复dex成功",Toast.LENGTH_SHORT).show();
    }

    /**
     * 文件复制
     *
     * @param source 源文件
     * @param target 目标文件
     */
    private void copyFile(File source, File target) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void hotfix(View view) {
        hotFix();
    }

    public void showVersion(View view) {
       new FixDemo().d(this);
    }
    int i=0;
    public void showVersionFinal(View view) {
        i++;
        FixDemo.DATA="static="+i;
        Toast.makeText(this, FixDemo.DESC+FixDemo.getDesc(), Toast.LENGTH_SHORT).show();
    }
}
