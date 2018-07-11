package com.example.loginaidl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends AppCompatActivity implements RestApiReceiver.Listener {
    private static final String TAG = "SignupActivity";

    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mSignupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailText = findViewById(R.id.signup_email);
        mPasswordText = findViewById(R.id.signup_password);
        mSignupButton = findViewById(R.id.button_create);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        Log.d(TAG, "createAccount");
        try {
            MainActivity.loginAidl.createAccount(mEmailText.getText().toString(), mPasswordText.getText().toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "onReceiveResult");
    }
}
