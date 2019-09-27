package com.maple.ndk;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perm, 200);
            }
        }
        final String in = Environment.getExternalStorageDirectory().getAbsolutePath() + "/b.png";
        final String tmp[]=new String[1];
          tmp[0] ="/storage/emulated/0/a-tmp/";

        final String out = Environment.getExternalStorageDirectory().getAbsolutePath() + "/a-out.png";
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               tmp[0] = FileUtil.diff(in, 10);
                Log.i("TAG", "diff finished" );
            }
        });
        findViewById(R.id.tv_combine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "onClick: " + tmp[0] + "---" + out);
                FileUtil.combine(tmp[0], out);

            }
        });
    }

}
