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

import static com.example.loginaidl.MainActivity.loginAidl;

public class SignupActivity extends AppCompatActivity {
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

        try {
            loginAidl.registerCallback(mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void createAccount() {
        Log.d(TAG, "createAccount");
        try {
            loginAidl.createAccount(mEmailText.getText().toString(), mPasswordText.getText().toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ILoginInterfaceCallback mCallback = new ILoginInterfaceCallback.Stub() {
        public void onResult(int callType, String response) {
            if (callType == RestApiService.ACTION_CREATE) {
                if (response.equals("success")) {
                    Log.d(TAG, "successful response");
                    finish();
                }
            }
        }
    };
}
