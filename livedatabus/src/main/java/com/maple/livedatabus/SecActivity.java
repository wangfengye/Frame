package com.maple.livedatabus;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SecActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        LiveDataBus.get().with("A",String.class).observe(SecActivity.this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Toast.makeText(SecActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
