package com.maple.ioc;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ann_butterknife.BindClick;
import com.example.ann_butterknife.BindView;
import com.example.ann_butterknife.ButterKnife;


/**
 * 编译器代码生成方式实现.
 */
public class Main2Activity extends AppCompatActivity {
    @BindView(R.id.tv_main)
    TextView mTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        mTv.setText("编译期实现方案");
    }
    @BindClick(R.id.btn_fir)
    void click(View view) {
        Toast.makeText(this, "ButterKnife event 1 success", Toast.LENGTH_SHORT).show();

    }

    @BindClick(R.id.btn_sec)
    void click2(View view) {
        Toast.makeText(this, "ButterKnife event 2 success", Toast.LENGTH_SHORT).show();
    }
}
