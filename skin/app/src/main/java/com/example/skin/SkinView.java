package com.example.skin;

import android.view.View;

import java.util.List;

/**
 * Created by maple on 2019/7/26 14:37
 */
public class SkinView {
    private List<SkinAttr> mAttrs;
    private View view;

    public SkinView(View view,List<SkinAttr> mAttrs) {
        this.mAttrs = mAttrs;
        this.view = view;
    }

    public List<SkinAttr> getAttrs() {
        return mAttrs;
    }

    public View getView() {
        return view;
    }
}
