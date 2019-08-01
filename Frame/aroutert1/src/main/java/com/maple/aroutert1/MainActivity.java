package com.maple.aroutert1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.ann_butterknife.Route;
import com.maple.arouter.ARouter;
@Route("route1")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aroutert2_activity_main);
        ARouter.init();
    }

    public void jump(View view) {
        ARouter.getInstance().jump(MainActivity.this, "route2");
    }
}
