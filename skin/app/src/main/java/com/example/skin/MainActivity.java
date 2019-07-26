package com.example.skin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perm, 200);
            }
        }
    }

    public void click(View view) {// 使用需在sdCard 根目录下放入皮肤包(真实项目建议直接放到项目私有目录下.防止被用户误删)
        if (SkinManager.getInstance().loadSkin(getApplicationContext(),
                new File(Environment.getExternalStorageDirectory(), "skin1.apk").getAbsolutePath())) {
            updateSkin();
        } else {
            Toast.makeText(this, "换肤失败", Toast.LENGTH_SHORT).show();
        }

    }
}
