package com.maple.jsonframework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.parser.JSONLexer;
import com.maple.jsonframework.json.FastJson;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // jsonTest();
                //test();
               // fastJsonTest();

            }
        });
    }
    // 测试object转换,通过重写setter方式实现多态,但若要根据字段判断类型,需判断字段在之前解析.
    private void test(){
        String ja = "{\"code\":1,\"data\":\"日志\"}";
        Cai c1= JSON.parseObject(ja,Cai.class);
        String ja2 = "{\"code\":1,\"data\":{\"a\":\"对象\"}}";
        //在键值设为object时, 数据会被解析会对应类型, '{}' 结构会被解析为JsonObject
        Cai c2= JSON.parseObject(ja2,Cai.class);
    }
    public void jsonTest() {

       News news = demoData();
        Log.i(TAG, "元数据: " + news.toString());
        String json =FastJson.toJson(news);
        Log.i(TAG, "Object->Json: " + json);
        News after =  FastJson.parseObject(json,News.class);
        Log.i(TAG, "Json->Object: " + after.toString());
    }
    private void fastJsonTest(){
        News news = demoData();
        Log.i(TAG, "元数据: " + news.toString());
       String json = JSON.toJSONString(news);
        Log.i(TAG, "Object->Json: " + json);
        News after =  JSON.parseObject(json,News.class);
        Log.i(TAG, "Json->Object: " + after.toString());
        Log.i(TAG, "Json->Object: " + after.getArrays().toString());
    }

    /**
     * 测试fastJsonBug.(Android版,无问题)
     * 漏洞详情
     * 漏洞的关键点在com.alibaba.fastjson.parser.JSONLexerBase#scanString中，当传入json字符串时，
     * fastjson会按位获取json字符串，当识别到字符串为\x为开头时，会默认获取后两位字符，并将后两位字
     * 符与\x拼接将其变成完整的十六进制字符来处理：
     * 而当json字符串是以\x结尾时，由于fastjson并未对其进行校验，将导致其继续尝试获取后两位的字符。
     * 也就是说会直接获取到\u001A也就是EOF,当fastjson再次向后进行解析时，会不断重复获取EOF，并将其写
     * 到内存中，直到触发oom错误
     * 即当你向jsoon中置入一个是以\x结尾的String("***\x"),导致死循环,不断增加内存,导致内存溢出
     */
    private void testBug259(){
        String testData = "{\"a\":\"\\x\"}";
        Object o =JSON.parse(testData);
        Log.i(TAG, "onClick: "+o.toString());
    }
    private News demoData(){
        News news = new News();
        news.setId(1);
        news.setTitle("大事件");
        List<News.Reader> readers = new ArrayList<>();
        News.Reader a = new News.Reader();
        a.setName("暴雪");
        News.Reader b = new News.Reader();
        b.setName("巫妖王");
        News.Reader c = new News.Reader();
        readers.add(a);
        readers.add(b);
        readers.add(c);
        news.setReaders(readers);
        HashMap<String, String> map = new HashMap<>();
        map.put("张杰", "谁主春秋");
        news.setMap(map);
        ArrayList<Integer> ints = new ArrayList<>();
        ints.add(1);
        ints.add(2);
        news.setInts(ints);
        ArrayList<ArrayList<Integer>> data = new ArrayList<>();
        ArrayList<Integer> array1 = new ArrayList<>();
        array1.add(22);
        array1.add(23);
        data.add(array1);
        ArrayList<Integer> array2 = new ArrayList<>();
        array2.add(222);
        array2.add(223);
        data.add(array2);
        news.setArrays(data);
        return news;
    }
}
