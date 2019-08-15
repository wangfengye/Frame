package com.maple.frame;

import com.maple.frame.dbUtil.DbField;
import com.maple.frame.dbUtil.Entity;

/**
 * @author maple on 2019/6/14 16:47.
 * @version v1.0
 * @see 1040441325@qq.com
 */
@Entity("user")
public class User {
    @DbField("name")
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
