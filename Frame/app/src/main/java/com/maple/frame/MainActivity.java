package com.maple.frame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.maple.frame.dbUtil.BaseDao;
import com.maple.frame.dbUtil.DaoFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: " + MainActivity.class.getClassLoader());
        final List<String> data = Collections.synchronizedList(new ArrayList<String>());
        data.add("a");
        new Thread(new Runnable() {
            @Override
            public void run() {
                data.add("b");
            }
        }).start();

    }

    public void add(View v) {
        long a =DaoFactory.get().getDao(BaseDao.class, User.class).insert(new User("map"));
        Log.i(TAG, "add: "+a);
    }

    public void findOne(View v) {
        User o = new User();
        o.setId(1L);
        User user = (User) DaoFactory.get().getDao(BaseDao.class, User.class).find(o);
        Log.i(TAG, "find: " + (user==null?"NULL":user.toString()));
    }

    public void find(View v) {
        List<User> data = DaoFactory.get().getDao(BaseDao.class, User.class).findAll();
        for (User u : data) {
            Log.i(TAG, "find: " + u.toString());
        }
    }

    public void update(View v) {
        User o = new User();
        o.setId(2L);
        o.setName("after update");
        long a = DaoFactory.get().getDao(BaseDao.class, User.class).update(o);
        Log.i(TAG, "update: "+a);
        BaseDao<User> d = DaoFactory.get().getDao(BaseDao.class, User.class);
        d.update(o);
    }
    public void delete(View v){
        User o = new User();
        o.setName("map");
        long a = DaoFactory.get().getDao(BaseDao.class, User.class).delete(o);
        Log.i(TAG, "delete: "+a);
    }
}
