package com.hbezxl.helper.Pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class User {
    private int age;
    private String name;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    private float[] sizes;

    public User() {
    }

    public User(int age, String name, Date startTime, float[] sizes) {
        this.age = age;
        this.name = name;
        this.startTime = startTime;
        this.sizes = sizes;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public float[] getSizes() {
        return sizes;
    }

    public void setSizes(float[] sizes) {
        this.sizes = sizes;
    }
}
