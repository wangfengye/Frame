package com.maple.ioc;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maple.ioc.utils.BaseActivity;
import com.maple.ioc.utils.ContentView;
import com.maple.ioc.utils.EventBaseOnclick;
import com.maple.ioc.utils.InjectView;
import com.maple.ioc.utils.OnClick;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
    @InjectView(R.id.tv_main)
    TextView mTv;

    @Override
    protected void onStart() {
        super.onStart();
        mTv.setText("inject view success");
    }

    @OnClick({R.id.btn_fir})
    void click(View view) {
        Toast.makeText(this, "inject event 1 success", Toast.LENGTH_SHORT).show();
    }

    @EventBaseOnclick({R.id.btn_sec})
    void click2(View view) {
        Toast.makeText(this, "inject event 2 success", Toast.LENGTH_SHORT).show();
    }
}
