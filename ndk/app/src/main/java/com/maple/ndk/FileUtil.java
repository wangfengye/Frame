package com.maple.ndk;

/**
 * Created by maple on 2019/9/26 16:35
 */
public class FileUtil {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     *
     * @param path 文件路径
     * @param count 拆分数量
     * @return 拆分文件的目录
     */
    public static native String diff(String path,int count);

    /**
     *  合并文件
     * @param tmpFiles 拆分目录
     * @param out 输出目录
     */
    public static native void combine(String tmpFiles,String out);
}
