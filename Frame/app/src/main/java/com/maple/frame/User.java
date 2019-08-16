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
    @DbField(value = "id",key =true)
    private Long id;
    @DbField("name")
    private String name;

    public User() {
    }

    public User(String name) {
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
