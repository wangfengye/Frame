package com.example.simple;

import android.content.Intent;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testArrayMap();
    }

    /**
     *  测试Intent传值问题
     *  超1M报错-->android.os.TransactionTooLargeException: data parcel size 1048956 bytes
     *  接近1M 时,有几率闪退(PS:非官方文档,网络资料,说测试509*1024大小时偶现闪退,推测是Intent本身内存占用部分,导致整体大小超1M)
     *  解决方案:进程内:EventBus,RxBus,LiveDataBus;进程间:aidl,Messenger,广播,共享外存,FileProvider等.
     *  探源
     *      Intent传递数据本质是使用Bundle传值
     *      Bundle 在跨进程时使用Binder跨进程通信
     *      通过Parcelable机制(共享内存)实现高效跨进程通信.(相比Serializable,频繁IO,效率更高)
     *      Bundle将Parcel存储的 Binder缓冲区,大小为1M,缓冲区是进程内共享的,所以即时数据<1M,也有可能出现异常.
     *      Bundle使用ArrayMap保存数据
     */
    private void testIntentTransforData(){
        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
        Bundle bundle = new Bundle();
        // 构造 1M数据 char 两个字节
        char[] tmp = new char[509 * 1024];

        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = (char) ('a' + (int) (Math.random() * 50));
        }

        bundle.putString("main", String.copyValueOf(tmp));
        intent.putExtras(bundle);
        startActivity(intent);
        // android.os.TransactionTooLargeException: data parcel size 1048956 bytes  --->Intent传递过大数据
    }

    /**
     *  ArrayMap对比HashMap (ArraySet也是对HashSet做了类似的优化)
     *  static Object[] mBaseCache;(长度为4的数组对象) static Object[] mTwiceBaseCache;(长度为8的数组对象) 缓存存储数据的数组对象,默认两种各缓存10个的数组对象,
     *
     *  int[] mHash 存储key 的 hashcode
     *  Object[] mArray,  [key0,value0,key1,value1...]方式存储数据
     *  hashcode 冲突处理,mHashes可能存储相同hash值;查找时,二分查找到第一个符合hash,对比key,不同,向后在查找hash
     *
     *  并发隐患: 主要由缓存机制引发(9.0已修复)
     *  mBaseCache使用链式缓存,mBaseCache保存新的缓存(newCache)的引用,newCache[0]持有上一个缓存的引用.
     *  并发时,A线程触发freeArrays,将mArray[0]指向上一个缓存, 同时B线程触发put,修改了mArray[0]的值, 之后在读取缓存时, 将 mBaseCache[0] 强转报错,
     *  本质,及并发导致缓存链的断裂,(脏缓存问题)
     *  9.0+ 代码在freeArrays,前,执行 `mArray=null`,斩断引用,解决了,回收过程中对即将缓存的array修改的问题.
     */
    private void testArrayMap(){
        ArrayMap<String,Integer> map = new ArrayMap<>();
        map.put("Aa",1);
        map.put("BB",2);
        Log.i(TAG, "testArrayMap: Aa"+map.get("Aa"));
        Log.i(TAG, "testArrayMap: BB"+map.get("BB"));
        map.remove("Aa");
        map.remove("BB");
        ArrayMap<String,Integer> map1 = new ArrayMap<>(4);
    }

    /**
     * 键值仅为int, 减少拆箱操作, 分两个数组存储key,value ,减少HashCode的存储
     * 延时回收.value指向一个默认Object, 减少数组,拷贝
     */
    private void testSparseArray(){

        SparseArray<String> map = new SparseArray<>();

    }
}
