package com.maple.ioc;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by maple on 2019/7/30 10:46
 */
public class Ocr {
    private static final String LANGUAGE = "eng";

    static {
        System.loadLibrary("card-ocr");
    }

    /**
     * @param bitmap 原图
     * @param config ARGB8888格式
     * @param i      执行到第几步
     * @return 输出截取的身份证
     */
    public static native Bitmap getCardIdBitmap(Bitmap bitmap, Bitmap.Config config, int i);

    /**
     * 文字识别(耗时操作)
     *
     * @param bitmap 截取的身份证号
     * @return 身份证号
     */
    public static String ocr(Bitmap bitmap, Context context) {
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(context.getCacheDir()+"/ocr", LANGUAGE);
        tessBaseAPI.setImage(bitmap);
        return tessBaseAPI.getUTF8Text();

    }

    public static boolean saveBitmap(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), "ocr.jpg");
        try {
            if (!file.exists()) file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
