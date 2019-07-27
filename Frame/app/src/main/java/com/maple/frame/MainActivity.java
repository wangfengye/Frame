package com.maple.frame;

import android.database.MatrixCursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.maple.frame.dbUtil.DaoFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: "+ MainActivity.class.getClassLoader());

    }
    public void add(View v){
       if (DaoFactory.getDao(User.class).insert(new User("map"))) Log.i(TAG, "add: true");
       else Log.i(TAG, "add: error");
    }
    public void find(View v){
        DaoFactory.getDao(User.class).findAll();
    }
}
