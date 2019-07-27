package com.maple.skia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perm, 200);
            }
        }
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.img_0);
        Log.i(TAG, "byteCount: " + bmp.getAllocationByteCount());


    }

    public void nativeCompress(View view) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.img_0);
        File file = new File(Environment.getExternalStorageDirectory(), "skiaNative.jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        NativeCompress.nativeCompress(bmp, file.getAbsolutePath(),1920,1080);
        Toast.makeText(this, "native success", Toast.LENGTH_SHORT).show();
        NativeCompress.compressBysize(bmp,new File(Environment.getExternalStorageDirectory(), "skiaSize.jpg"));
        NativeCompress.compressByQual(bmp,new File(Environment.getExternalStorageDirectory(), "skiaQual.jpg"));
    }
    public void compress(View view){
        File file = new File(Environment.getExternalStorageDirectory(),"test_0.jpg" );

        File out = new File(Environment.getExternalStorageDirectory(), "skiaJava.jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            LuBan.compress(file,false,out.getAbsolutePath(),false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Java success", Toast.LENGTH_SHORT).show();
    }

}
