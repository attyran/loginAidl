package com.example.loginaidl;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.IBinder;
//import android.util.Log;
//
//public class LoginManager {
//    private static final String TAG = "LoginManager";
//    private Context mContext;
//    private LoginManager mInstance;
//    protected ILoginInterface loginAidl = null;
//
//    public LoginManager(Context context) {
//        this.mContext = context;
//        this.mInstance = this;
//    }
//
//    public static LoginManager Instance {
//        if (mInstance != null) {
//            return mInstance;
//        }
//    }
//
//    public void bindService() {
//        Intent intent = new Intent(mContext, RestApiService.class);
//        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
//    }
//
//    public void createAccount() {
//
//    }
//
//    private ServiceConnection connection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d(TAG, "onServiceConnected");
//            loginAidl = ILoginInterface.Stub.asInterface(service);
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.d(TAG, "onServiceDisconnected");
//            loginAidl = null;
//        }
//    };
//}
