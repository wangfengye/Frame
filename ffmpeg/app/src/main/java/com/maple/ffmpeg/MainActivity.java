package com.maple.ffmpeg;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button tv = findViewById(R.id.sample_text);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDecode();
            }
        });
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perm, 200);
            }
        }
        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });
        findViewById(R.id.btn_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String videoPath = new File(Environment.getExternalStorageDirectory(), "natural.mp4").getAbsolutePath();
                final String out = new File(Environment.getExternalStorageDirectory(), "qwww.pcm").getAbsolutePath();
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                VideoPlayer.sound(videoPath, out);
                            }
                        }
                ).start();

            }
        });
    }

    private void testDecode() {
        String path = new File(Environment.getExternalStorageDirectory(), "f_in.mp4").getAbsolutePath();
        String out = new File(Environment.getExternalStorageDirectory(), "f_out.yuv").getAbsolutePath();
        VideoUtil.decode(path, out);
        Toast.makeText(this, "decode finish", Toast.LENGTH_SHORT).show();
    }

    private void play() {
        VideoView videoView = findViewById(R.id.vv);
        final String videoPath = new File(Environment.getExternalStorageDirectory(), "natural.mp4").getAbsolutePath();
        final Surface surface = videoView.getHolder().getSurface();
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoPlayer.render(videoPath, surface);
            }
        }).start();

    }

}
