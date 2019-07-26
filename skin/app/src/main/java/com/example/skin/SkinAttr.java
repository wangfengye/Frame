package com.example.skin;

import android.content.Context;

/**
 * Created by maple on 2019/7/26 14:38
 */
public class SkinAttr {
    private String attrName;
    private int resId;
    private String typeName;
    private String valueName;

    public SkinAttr(String attrName, int resId, String typeName, String valueName) {
        this.attrName = attrName;
        this.resId = resId;
        this.typeName = typeName;
        this.valueName = valueName;
    }

    public String getAttrName() {
        return attrName;
    }

    public int getResId() {
        return resId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getValueName() {
        return valueName;
    }
}
