package com.example.loginaidl;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText mAgeText;
    private EditText mHeightText;
    private Button mFetchButton;
    private Button mUpdateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAgeText = findViewById(R.id.input_age);
        mHeightText = findViewById(R.id.input_height);
        mFetchButton = findViewById(R.id.button_fetch);
        mFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mUpdateButton = findViewById(R.id.button_update);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

}
