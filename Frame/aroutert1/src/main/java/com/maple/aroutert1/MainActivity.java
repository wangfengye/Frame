package com.maple.aroutert1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.maple.arouter.ARouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ARouter.init();
        ARouter.getInstance().addRoute("jump1", this.getClass());
        ARouter.getInstance().addRoute("jump2", com.maple.aroutert2.MainActivity.class);
    }

    public void jump(View view) {
        ARouter.getInstance().jump(MainActivity.this, "jump2");
    }
}
