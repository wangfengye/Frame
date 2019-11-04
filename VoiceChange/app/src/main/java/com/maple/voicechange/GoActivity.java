package com.maple.voicechange;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RadioGroup;

import org.fmod.FMOD;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoActivity extends AppCompatActivity {
    private int mType=EffectUtil.TYPE_LUOLI;
    private ExecutorService mPool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go);
        FMOD.init(this);
        mPool= Executors.newFixedThreadPool(2);//第二个线程让暂停可以执行
        final String path = Environment.getExternalStorageDirectory()+"/b.mp3";
        findViewById(R.id.imageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        EffectUtil.fix(path,mType);
                    }
                });

            }
        });
        String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perm, 200);
            }
        }
        RadioGroup  group= findViewById(R.id.rg);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_a:
                        mType=EffectUtil.TYPE_NORMAL;
                        break;
                    case R.id.rb_b:
                        mType=EffectUtil.TYPE_LUOLI;
                        break;
                    case R.id.rb_c:
                        mType=EffectUtil.TYPE_UNCLE;
                        break;
                    case R.id.rb_d:
                        mType=EffectUtil.TYPE_JINGSONG;
                        break;
                    case R.id.rb_e:
                        mType=EffectUtil.TYPE_KONGLING;
                        break;
                    case R.id.rb_f:
                        mType=EffectUtil.TYPE_GAOGUAI;
                        break;
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FMOD.close();
    }
}
