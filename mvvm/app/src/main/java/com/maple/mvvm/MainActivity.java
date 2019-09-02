package com.maple.mvvm;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maple.mvvm.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
       ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
       user = new User();
       user.setName("maple");
       user.setPassword("666666");
       user.setHeader(R.mipmap.ic_launcher);
        ArrayList<User.Food> foods = new ArrayList<>();
        User.Food food1 = new User.Food(R.mipmap.ic_launcher_round,"a","1");
        User.Food food2 = new User.Food(R.mipmap.ic_launcher_round,"b","2");
        foods.add(food1);
        foods.add(food2);
       user.setAdapter(new ComonAdapter<>(foods,R.layout.item_food,BR.food));
       binding.setUser(user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                user.setName(user.getName()+"1");
                user.setPassword(user.getPassword()+"1");
            }
        },2000);

    }
}
