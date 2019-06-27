package com.maple.livedatabus;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveDataBus.get().with("A",String.class).observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private int counter;
    public void postData(View view) {
        counter++;
        LiveDataBus.get().with("A",String.class)
                .postValue("post-->data: "+counter);
    }

    public void goSec(View view) {
        startActivity(new Intent(MainActivity.this,SecActivity.class));
    }
}
