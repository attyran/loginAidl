package com.example.loginaidl;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mSignupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onStart() {
        super.onStart();
        mEmailText = findViewById(R.id.input_email);
        mPasswordText = findViewById(R.id.input_password);
        mLoginButton = findViewById(R.id.button_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.Instance(getApplicationContext()).registerCallback(mCallback);
                LoginManager.Instance(getApplicationContext()).login(mEmailText.getText().toString(), mPasswordText.getText().toString());
            }
        });

        mSignupLink = findViewById(R.id.link_signup);
        mSignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        LoginManager loginManager = LoginManager.Instance(this);
        loginManager.bindService();

        AppDatabase.getAppDatabase(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.Instance(this).unbindService();
    }

    private ILoginInterfaceCallback mCallback = new ILoginInterfaceCallback.Stub() {
        public void onResult(int callType, String response, String[] values) {
            if (callType == RestApiService.ACTION_LOGIN) {
                if (response.equals("success")) {
                    Log.d(TAG, "successful login");

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Login unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
