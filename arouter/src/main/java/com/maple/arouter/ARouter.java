package com.maple.arouter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;

/**
 * @author maple on 2019/6/29 14:10.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class ARouter {
    private static ARouter instance;

    public static ARouter getInstance() {
        if (instance == null) throw new RuntimeException("you need init first");
        return instance;
    }

    public synchronized static void init() {
        instance = new ARouter();
        //todo:初始化,加入入口类
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
