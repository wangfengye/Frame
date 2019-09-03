package com.maple.mvvm;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by maple on 2019/8/29 13:54
 */
public class User extends BaseObservable {
    private String name;
    private String password;
    private int header;
    private ComonAdapter<Food> adapter;

    @Bindable
    public ComonAdapter<Food> getAdapter() {
        return adapter;
    }

    public void setAdapter(ComonAdapter<Food> adapter) {
        this.adapter = adapter;
        notifyPropertyChanged(BR.adapter);
    }

    @Bindable
    public int getHeader() {
        return header;
    }

    public void setHeader(int header) {
        this.header = header;
        notifyPropertyChanged(BR.header);
    }


    @Bindable//标记监听
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    //自定义属性,xml中使用app:header=""调用,
    @BindingAdapter("bind:header")
    public static void getImage(ImageView view, int id) {
        view.setImageResource(id);
    }

    public static class Food extends BaseObservable {
        private int img;
        private String desc;
        private String keys;

        public Food(int img, String desc, String keys) {
            this.img = img;
            this.desc = desc;
            this.keys = keys;
        }

        @Bindable
        public int getImg() {
            return img;
        }

        public void setImg(int img) {
            this.img = img;
            notifyPropertyChanged(BR.food);
        }

        @BindingAdapter("bind:img")
        public static void img(ImageView view, int id) {
            view.setImageResource(id);
        }

        @Bindable
        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
            notifyPropertyChanged(BR.desc);
        }

        @Bindable
        public String getKeys() {
            return keys;
        }

        public void setKeys(String keys) {
            this.keys = keys;
            notifyPropertyChanged(BR.keys);
        }
        public void clickText(View v){
            MainActivity.getInstance();
            Toast.makeText(v.getContext(), desc, Toast.LENGTH_SHORT).show();
        }
    }

}
