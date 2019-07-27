package com.maple.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.maple.eventbus.util.EventBus;
import com.maple.eventbus.util.Subscribe;
import com.maple.eventbus.util.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.get().register(this);
    }

    @Subscribe
    public void observer(EventBean bean) {
        Log.i("Main", Thread.currentThread().getName() + "\n" + bean.toString());
        Toast.makeText(this, Thread.currentThread().getName() + "\n" + bean.toString(), Toast.LENGTH_SHORT).show();
    }

    public void send(View view) {
        EventBus.get().post(new EventBean("main", "gogogo"));
    }

    public void goSec(View view) {
        startActivity(new Intent(this, SecActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get().unRegister(this);
    }
}
