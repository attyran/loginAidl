package com.example.loginaidl;
import com.example.loginaidl.ILoginInterfaceCallback;

interface ILoginInterface {
    void createAccount(String username, String password);
    void login(String username, String password);
    void fetch();
    void update(int age, int height);
    void registerCallback(ILoginInterfaceCallback cb);
}
