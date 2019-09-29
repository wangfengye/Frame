package com.maple.bsdiff;

/**
 * Created by maple on 2019/9/29 10:14
 */
public class BsdiffUtil {
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * 合并差分包
     *
     * @param oldApkPath 旧包路径
     * @param newApkPath 新包生成路径
     * @param patch      差分包路径
     */
    public static native void patch(String oldApkPath, String newApkPath,String patch);

    /**
     * 生成差分包
     * @param oldApkPath 旧包路径
     * @param patch      新包路径
     * @param newApkPath 差分包生成路径
     */
    public static native void diff(String oldApkPath, String newApkPath, String patch);
}
