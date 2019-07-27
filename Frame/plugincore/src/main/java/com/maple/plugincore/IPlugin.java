package com.maple.plugincore;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * @author maple on 2019/6/20 16:11.
 * @version v1.0
 * @see 1040441325@qq.com
 * 生命周期规范
 */
public interface IPlugin {
    void attach(Activity proxyActivity);

    void onCreate(@Nullable Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

}
