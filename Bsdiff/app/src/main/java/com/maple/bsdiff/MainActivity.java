package com.maple.bsdiff;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private ExecutorService mPool;

    // Used to load the 'native-lib' library on application startup.


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(perm, 200);
        }
        mPool = Executors.newFixedThreadPool(1);
    }


    public void clickDiff(View view) {

        mPool.execute(new Runnable() {
            @Override
            public void run() {
                BsdiffUtil.diff(Environment.getExternalStorageDirectory() + "/a.png"
                        , Environment.getExternalStorageDirectory() + "/c.png"
                        , Environment.getExternalStorageDirectory() + "/diff-c.tmp");
                Log.i(TAG, "clickDiff: finished");
            }
        });


    }

    public void clickPatch(View view) {

        mPool.execute(new Runnable() {
            @Override
            public void run() {
                BsdiffUtil.patch(Environment.getExternalStorageDirectory() + "/a.png"
                        , Environment.getExternalStorageDirectory() + "/c-patch.png"
                        , Environment.getExternalStorageDirectory() + "/diff-c.tmp");
                Log.i(TAG, "clickPatch: finished");
            }
        });
    }

    public void update(View view) {
        mPool.execute(new Runnable() {
            @Override
            public void run() {
                BsdiffUtil.patch(getApplicationInfo().sourceDir
                        , Environment.getExternalStorageDirectory() + "/next.png"
                        , Environment.getExternalStorageDirectory() + "/patch.new");
                Log.i(TAG, "update: finished");
            }
        });
    }

    public static void install(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(FileProvider.getUriForFile(context, "aaa.fileprovider", new File(Environment.getExternalStorageDirectory(), "next.apk")),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


}
