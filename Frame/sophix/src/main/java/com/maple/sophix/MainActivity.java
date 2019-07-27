package com.maple.sophix;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.maple.sophix.util.SopHix;

public class MainActivity extends AppCompatActivity {


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
    }

    public void test(View view) {
        Toast.makeText(this, new Test().mirror(), Toast.LENGTH_SHORT).show();
    }

    public void hotFix(View view) {
        if (SopHix.load(Environment.getExternalStorageDirectory() + "/sophix.dex", this)) {
            Toast.makeText(this, "修复结束", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "修复出现异常", Toast.LENGTH_SHORT).show();
        }
        ;

    }
}
