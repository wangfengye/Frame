package com.maple.aroutert2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.maple.arouter.ARouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ARouter.getInstance().addRoute("jump2", this.getClass());
        Toast.makeText(this,"2",Toast.LENGTH_SHORT).show();
    }

    public void jump(View view) {
        ARouter.getInstance().jump(MainActivity.this, "jump1");
    }
}
