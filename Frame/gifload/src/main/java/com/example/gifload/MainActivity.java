package com.example.gifload;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String path;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perm, 200);
            }
        }
        path = Environment.getExternalStorageDirectory() + "/gifLad.gif";

    }

    public void glideLoad(View view) {
        GifLoader.with(path,img).destroy();
        Glide.with(this).asGif().load(new File(path)).into(img);
    }

    public void nativeLoad(View view) {
        GifLoader.with(path, img).canLoop().speed(800).start();
    }


}
