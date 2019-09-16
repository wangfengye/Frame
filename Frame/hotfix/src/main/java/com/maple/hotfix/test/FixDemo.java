package com.maple.hotfix.test;

import android.content.Context;
import android.widget.Toast;

/**
 * @author maple on 2019/7/1 13:31.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class FixDemo {
    //其他类中调用的静态常量为未修复的,修复的类调用的为修复后的
    public static  final  String DESC ="Version 2";
    //静态变量其他类中可修改.
    public static  String DATA="";
    public  void d(Context context) {
        Toast.makeText(context, "已修复"+DESC+getDesc()+DATA, Toast.LENGTH_SHORT).show();
    }
    // 静态函数,修复后,其他类调用也是修复后的.
    public static String getDesc(){
        return "getDesc2";
    }
}
