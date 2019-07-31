package com.maple.ioc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";
    private ImageView img;
    private ImageView src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        src = findViewById(R.id.res);
        String[] perm = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(perm[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perm, 200);
            }
        }
        copyData();
    }
    private int i=1;
    public  void onClick(View v){
        Bitmap bitmap = getBitmap(img);
        final Bitmap dst = Ocr.getCardIdBitmap(bitmap,bitmap.getConfig(),i++);
        src.setImageBitmap(dst);
        if (i>=5){
          //  Ocr.saveBitmap(dst);//保存训练样本
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String res =Ocr.ocr(dst,MainActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, res+"", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }
    public Bitmap getBitmap(ImageView img) {
        img.setDrawingCacheEnabled(true);
        Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        img.setDrawingCacheEnabled(false);
        return bitmap;
    }
    public void copyData(){
        File dir = new File(getCacheDir().getAbsolutePath(),"/ocr/tessdata");
        if (!dir.exists())
            dir.mkdirs();
        try {
            String[] list = getAssets().list("tess");
            for(String path: list){
                Log.i(TAG, "copyData: "+path);
                File file = new File(dir,path);
                InputStream in = getAssets().open("tess/"+path);
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] bytes =new byte[in.available()];//直接读整体,优化建议,定长byte循环读
                in.read(bytes);
                outputStream.write(bytes);
                outputStream.flush();
                in.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
