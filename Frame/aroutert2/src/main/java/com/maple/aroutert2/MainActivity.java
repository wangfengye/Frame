package com.maple.aroutert2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ann_butterknife.Route;
import com.maple.arouter.ARouter;
@Route("route2")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aroutert2_activity_main);
        // ARouter.getInstance().addRoute("jump2", this.getClass());
        Toast.makeText(this,"route2",Toast.LENGTH_SHORT).show();
    }

    public void jump(View view) {
        ARouter.getInstance().jump(MainActivity.this, "route");
    }
}
