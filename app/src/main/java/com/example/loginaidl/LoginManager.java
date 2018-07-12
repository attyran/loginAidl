package com.example.loginaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class LoginManager {
    public static final String TAG = "LoginManager";

    private static LoginManager mInstance;
    private Context mContext;
    private ILoginInterface loginAidl;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            loginAidl = ILoginInterface.Stub.asInterface(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            loginAidl = null;
        }
    };

    private LoginManager(Context context) {
        this.mContext = context;
    }

    public static LoginManager Instance(Context context) {
        if (mInstance == null) {
            mInstance = new LoginManager(context);
        }
        return mInstance;
    }

    public void bindService() {
        Intent intent = new Intent(mContext, RestApiService.class);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        mContext.unbindService(connection);
    }

    public void registerCallback(ILoginInterfaceCallback callback) {
        try {
            loginAidl.registerCallback(callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void login(String username, String password) {
        try {
            loginAidl.login(username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void createAccount(String username, String password) {
        try {
            loginAidl.createAccount(username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void fetch() {
        try {
            loginAidl.fetch();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void update(int age, int height) {
        try {
            loginAidl.update(age, height);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
