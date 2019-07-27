package com.maple.hotfix;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * @author maple on 2019/7/1 15:45.
 * @version v1.0
 * @see 1040441325@qq.com
 * <p>
 * 修复核心
 * dex修复(java代码修复)
 * todo: so修复
 */
public class FixDexUtils {
    private static HashSet<File> loadedDexes = new HashSet<>();

    static {
        loadedDexes.clear();
    }

    public static void loadFixedDex(Context context) {
        if (context == null) return;
        // 获取修复dex.
        File fileDir = context.getDir("odex", Context.MODE_PRIVATE);
        File[] files = fileDir.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".dex") && !f.getName().equals("classes.dex")) {
                loadedDexes.add(f);
            }
        }
        createDexClassLoader(context, fileDir);
        Log.i("HOT_FIX", "hotfix_success");
    }

    /**
     * 创建类加载器
     *
     * @param context
     * @param fileDir
     */
    private static void createDexClassLoader(Context context, File fileDir) {
        String optimizedDirectory = fileDir.getAbsolutePath() + "/opt_dex";//临时解压目录
        File fileOpt = new File(optimizedDirectory);
        if (!fileOpt.exists()) fileOpt.mkdirs();
        DexClassLoader classLoader;

        for (File f : loadedDexes) {
            // String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent
            classLoader = new DexClassLoader(f.getAbsolutePath(), optimizedDirectory, null, context.getClassLoader());
            //fix
            try {
                hotFix(classLoader, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void hotFix(DexClassLoader classLoader, Context context) throws Exception {
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        Object dexElements = getDexElements(classLoader);
        Object sysDexElements = getDexElements(pathClassLoader);
        Object dexElementsAfter = combineArray(dexElements, sysDexElements);//合并数组
        setDexElementsByClassLoader(pathClassLoader,dexElementsAfter);
    }

    /**
     * 数组合并
     * @param a a
     * @param b b
     * @return combine
     */
    private static Object combineArray(Object a, Object b) {
        Object c = null;
        if (a instanceof Object[] && b instanceof Object[]) {
            Class<?> clazz = ((Object[]) b).getClass().getComponentType();
            c = Array.newInstance(clazz,((Object[]) a).length+((Object[]) b).length);
            System.arraycopy(a, 0, c, 0, ((Object[]) a).length);
            System.arraycopy(b, 0, c, ((Object[]) a).length, ((Object[]) b).length);
        }
        return c;
    }

    /**
     * 获取从系统遍历出的dexElements;
     * BaseDexClassLoader  --> DexPathList --> dexElements
     *
     * @param classLoader BaseDexClassLoader
     * @return dexElements
     */
    private static Object getDexElements(BaseDexClassLoader classLoader) {
        Object dexElements = null;
        try {
            Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathList = pathListField.get(classLoader);
            Class<?> classPathList = pathList.getClass();
            Field fieldDexElements = classPathList.getDeclaredField("dexElements");
            fieldDexElements.setAccessible(true);
            dexElements = fieldDexElements.get(pathList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dexElements;

    }


    private static void setDexElementsByClassLoader(ClassLoader loader, Object value) {
        try {
            Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathList = pathListField.get(loader);
            Class<?> classPathList = pathList.getClass();
            Field fieldDexElements = classPathList.getDeclaredField("dexElements");
            fieldDexElements.setAccessible(true);
            fieldDexElements.set(pathList, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
