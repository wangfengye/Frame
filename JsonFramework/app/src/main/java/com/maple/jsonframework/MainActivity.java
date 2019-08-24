package com.maple.jsonframework;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jsonTest();
    }
    public void jsonTest(){
        News news = new News();
        news.setId(1);
        news.setTitle("大事件");
        List<News.Reader> readers =new ArrayList<>();
        News.Reader a= new News.Reader();a.setName("暴雪");
        News.Reader b= new News.Reader();b.setName("巫妖王");
        readers.add(a);readers.add(b);
        news.setReaders(readers);
        Log.i(TAG, "object->string "+FastJson.toJson(news));
    }
}
