package com.maple.sophix.util;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * @author maple on 2019/7/8 11:17.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class SopHix {
    private static final String TAG = "SopHix";

    static {
        System.loadLibrary("native-lib");

    }
    // 拷贝修复包
    // 加载dex

    public static boolean load(String path, Context context) {
        try {
            File opt = context.getDir("opt", Context.MODE_PRIVATE);
            File opt2 = new File(opt, "a");
            if (!opt2.exists()) opt2.createNewFile();

            DexFile file = DexFile.loadDex(path, opt2.getAbsolutePath(), Context.MODE_PRIVATE);
            Enumeration<String> entries = file.entries();
            while (entries.hasMoreElements()) {
                String key = entries.nextElement();
                Log.i(TAG, "load: " + key);
                Class clazz = file.loadClass(key, getClassLoader(file));
                if (clazz != null) {
                    dueReplaceMethod(clazz);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //遍历替换的方法修改方法表
    private static void dueReplaceMethod(Class clazz) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {

            String[] values = getAnnosSystem(method, clazz);
            if (values == null) continue;
            Log.i(TAG, "find fix method: " + method.getName());
            Class<?> wrongClazz = Class.forName(values[0]);
            Method wrongMethod = wrongClazz.getMethod(values[1], method.getParameterTypes());
            replace(wrongMethod, method);
        }
    }

    private static String[] getAnnos(Method method, Class clazz) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class repalceC = Class.forName("com.maple.sophix.util.Replace", true, clazz.getClassLoader());
        Object anno = method.getAnnotation(repalceC);
        if (anno == null) return null;
        String[] res = new String[2];
        Method getClazz = repalceC.getDeclaredMethod("clazz");
        res[0] = (String) getClazz.invoke(anno);
        Method getMethod = repalceC.getDeclaredMethod("method");
        res[1] = (String) getMethod.invoke(anno);
        return res;
    }

    private static String[] getAnnosSystem(Method method, Class clazz)  {
        Replace anno = method.getAnnotation(Replace.class);
        if (anno == null) return null;
        String[] res = new String[2];
        res[0] = anno.clazz();
        res[1] = anno.method();
        return res;
    }

    private static native void replace(Method wrongMethod, Method method);


    private static ClassLoader getClassLoader(final DexFile dexFile) {
        return new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                Class clazz = dexFile.loadClass(name, this);
                if (clazz == null) {
                    clazz = Class.forName(name);
                }
                return clazz;
            }
        };

    }
}
