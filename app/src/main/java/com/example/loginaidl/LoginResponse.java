package com.example.loginaidl;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("access_token")
    public String access_token;

    public String getAccessToken() {
        return access_token;
    }

    public void setId(String access_token) {
        this.access_token = access_token;
    }
}
