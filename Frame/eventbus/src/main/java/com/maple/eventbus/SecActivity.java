package com.maple.eventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.maple.eventbus.util.EventBus;
import com.maple.eventbus.util.Subscribe;
import com.maple.eventbus.util.ThreadMode;

public class SecActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        EventBus.get().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get().unRegister(this);
    }

    @Subscribe(value = ThreadMode.MAIN, sticky = true)
    public void observer(EventBean bean) {
        Toast.makeText(this, Thread.currentThread().getName() + "\n" + bean.toString(), Toast.LENGTH_SHORT).show();
    }
}
