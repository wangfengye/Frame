package com.maple.jsonframework;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by maple on 2019/8/30 14:36
 */
public class Cai {
    private int code;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        if (data.getClass().isAssignableFrom(JSONObject.class)) {
            JSONObject jsonObject = (JSONObject) data;
            this.data = JSON.parseObject(jsonObject.toJSONString(), A.class);
        } else {
            this.data = data;
        }
    }

    public static class A {
        String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }
}
