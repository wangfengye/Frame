package com.maple.mvvm;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.LruCache;
import android.view.WindowManager;

import com.maple.mvvm.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static Activity a;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Debug.startMethodTracing("sample");
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        user = new User();
        user.setName("maple");
        user.setPassword("666666");
        user.setHeader(R.mipmap.ic_launcher);
        ArrayList<User.Food> foods = new ArrayList<>();
        User.Food food1 = new User.Food(R.mipmap.ic_launcher_round, "a", "1");
        User.Food food2 = new User.Food(R.mipmap.ic_launcher_round, "b", "2");
        foods.add(food1);
        foods.add(food2);
        user.setAdapter(new ComonAdapter<>(foods, R.layout.item_food, BR.food));
        binding.setUser(user);
        a = this;//模拟内存泄漏
        // wakelock
        PowerManager pw = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pw.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mvvm:mylocktag");
        wl.acquire(1000);//设置超时自动释放锁
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (wl.isHeld()) wl.release();
        //JobScheduler
        ComponentName jobService = new ComponentName(this,JobAction.class);
        JobInfo jobInfo = new JobInfo.Builder(1, jobService)//任务id,对应的处理服务
                .setPersisted(true)//重启保留
                .setRequiresCharging(true)//是否充电.
                .setRequiresDeviceIdle(true)//设备空闲状态.
                /*  .addTriggerContentUri(uri)//监听uri对应数据改变.触发任务执行.
                  .setTriggerContentMaxDelay(1)//监听变化到任务执行的最大延时.
                  .setTriggerContentUpdateDelay(1)//监听触发后,但任务未执行,若content发生改变,重置延时时间*/
                .build();
                JobScheduler scheduler= (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                scheduler.schedule(jobInfo);

    }
    public static class JobAction extends JobService{

        @Override
        public boolean onStartJob(JobParameters params) {
            Log.i(TAG, "onStartJob: "+params.toString());
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                user.setName(user.getName() + "1");
                user.setPassword(user.getPassword() + "1");

            }
        }, 2000);

    }

    public static final String TAG = "MainActivity";
    public static User mU;

    public static void getInstance() {
        if (mU == null) {
            mU = new User();
            Log.i(TAG, "init: ");
        } else {
            Log.i(TAG, "has: ");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();//仅finish,进程未销毁,重新打开 静态实例仍存在
        Debug.stopMethodTracing();
        System.exit(0);
    }
}
