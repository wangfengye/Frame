package com.maple.frame;

import android.database.MatrixCursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.maple.frame.dbUtil.BaseDao;
import com.maple.frame.dbUtil.DaoFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: "+ MainActivity.class.getClassLoader());

    }
    public void add(View v){
       if (DaoFactory.get().getDao(BaseDao.class,User.class).insert(new User("map"))) Log.i(TAG, "add: true");
       else Log.i(TAG, "add: error");
    }
    public void find(View v){
        List<User> data = DaoFactory.get().getDao(BaseDao.class, User.class).findAll();
        for(User u: data){
            Log.i(TAG, "find: "+u.toString());
        }
    }
}
