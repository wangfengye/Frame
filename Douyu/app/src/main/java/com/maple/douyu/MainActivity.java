package com.maple.douyu;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.maple.douyu.push.LivePusher;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] permissions=new String[]{Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions,1);
        }
        // Example of a call to a native method
        SurfaceView view= findViewById(R.id.surface);
        final LivePusher pusher=new LivePusher(view.getHolder());
        findViewById(R.id.btn_camera_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pusher.switchCamera();
            }
        });
        findViewById(R.id.btn_push).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pusher.startPush();
            }
        });
    }

}
