package com.maple.arouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import dalvik.system.DexFile;

/**
 * @author maple on 2019/6/29 14:10.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class  ARouter {
    private static ARouter instance;


    public static ARouter getInstance() {
        if (instance == null) throw new RuntimeException("you need init first");
        return instance;
    }

    public synchronized static void init(Context context) {
        instance = new ARouter();
        //todo:初始化,加入入口类
        List<String> clazzes = getClassName("com.maple.arouter.apt",context);
        for (String clazz : clazzes)
            try {
                Class<?> aClass = Class.forName(clazz);
                if (!IRouter.class.isAssignableFrom(aClass)) continue;
                IRouter o = (IRouter) aClass.newInstance();
                o.putActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private static List<String> getClassName(String packageName,Context context) {
        List<String> classList = new ArrayList<>();
        String path =null;
        try {
            path =context.getPackageManager().getApplicationInfo(context.getPackageName(),0).sourceDir;
            DexFile dexFile = new DexFile(path);
            Enumeration entries = dexFile.entries();
            while (entries.hasMoreElements()){
                String name = (String) entries.nextElement();
                if (name.contains(packageName)){
                    classList.add(name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

    HashMap<String, Class<? extends Activity>> mMap = new HashMap<>();

    public void jump(Activity activity, String key, Bundle bundle) {
        Class targetClass = mMap.get(key);
        Intent intent = new Intent(activity, targetClass);
        if (bundle != null) intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public void jump(Activity activity, String key) {
        jump(activity, key, null);
    }

    public void addRoute(String key, Class<? extends Activity> clazz) {
        mMap.put(key, clazz);
    }
}
