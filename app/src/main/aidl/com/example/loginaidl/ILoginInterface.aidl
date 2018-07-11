package com.example.loginaidl;

interface ILoginInterface {
    void createAccount(String username, String password);
    void login(String username, String password);
}
