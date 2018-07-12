package com.example.loginaidl;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    public String id;

    @SerializedName("username")
    public String username;

    @SerializedName("age")
    public String age;

    @SerializedName("height")
    public String height;

    public String token;

    public User(String id, String username, String age, String height) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.height = height;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
