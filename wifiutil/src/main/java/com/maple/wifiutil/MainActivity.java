package com.maple.wifiutil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.maple.wifiutil.utils.NetType;
import com.maple.wifiutil.utils.NetWork;
import com.maple.wifiutil.utils.NetworkManager;
import com.maple.wifiutil.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetworkManager.init(getApplication());//实际操作建议在Application中初始化
        NetworkManager.get().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.get().unRegister(this);
    }

    @NetWork(NetType.WIFI)
    public void lisWifi(NetType type) {
        Log.i(TAG, "lisWifi: " + (type == NetType.NONE ? "断开" : "连接"));
    }

    @NetWork(NetType.CMWAP)
    public void lisCMWAP(NetType type) {
        Log.i(TAG, "lisCMWAP: " + (type == NetType.NONE ? "断开" : "连接"));
    }

    @NetWork(NetType.AUTO)
    public void lisAUTO(NetType type) {
        Log.i(TAG, "lisAUTO: " + (type == NetType.NONE ? "断开" : "连接"));
    }

    public void goSettting(View view) {
        NetworkUtil.openSetting(this, 666);
    }
}
