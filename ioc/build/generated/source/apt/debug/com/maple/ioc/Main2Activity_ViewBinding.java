package com.maple.ioc;

import com.example.ann_butterknife.IBinder;

public class Main2Activity_ViewBinding implements IBinder<com.maple.ioc.Main2Activity> {
    @Override
    public void bind(final com.maple.ioc.Main2Activity target) {
        target.mTv = (android.widget.TextView) target.findViewById(2131165327);
        target.findViewById(2131165218).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                target.click(v);
            }
        });
        target.findViewById(2131165219).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                target.click2(v);
            }
        });
    }
}
