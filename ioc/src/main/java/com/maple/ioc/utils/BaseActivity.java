package com.maple.ioc.utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author maple on 2019/7/5 15:56.
 * @version v1.0
 * @see 1040441325@qq.com
 */
public class BaseActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectManager.inject(this);
    }

}
