package com.example.loginaidl;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class RestApiReceiver extends ResultReceiver {
    private Listener listener;

    public RestApiReceiver(Handler handler) {
        super(handler);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
    }

    public static interface Listener {
        void onReceiveResult(int resultCode, Bundle resultData);
    }
}
