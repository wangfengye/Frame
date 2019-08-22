package com.maple.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    ITest test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test=new TestRxjava();
        Switch s=findViewById(R.id.sw);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                test= isChecked?new TestMe():new TestRxjava();
            }
        });
        findViewById(R.id.btn_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test.testDemo();
            }
        });
    }


}

