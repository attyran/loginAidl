package com.example.loginaidl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText mAgeText;
    private EditText mHeightText;
    private Button mFetchButton;
    private Button mUpdateButton;
    private Button mLogoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        mAgeText = findViewById(R.id.input_age);
        mHeightText = findViewById(R.id.input_height);
        mFetchButton = findViewById(R.id.button_fetch);
        mFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.Instance(getApplicationContext()).fetch();
            }
        });

        mUpdateButton = findViewById(R.id.button_update);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.Instance(getApplicationContext()).update(Integer.valueOf(mAgeText.getText().toString()), Integer.valueOf(mHeightText.getText().toString()));
            }
        });

        mLogoutButton = findViewById(R.id.button_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LoginManager.Instance(this).registerCallback(mCallback);
    }

    private ILoginInterfaceCallback mCallback = new ILoginInterfaceCallback.Stub() {
        public void onResult(int callType, String response, String[] values) {
            if (callType == RestApiService.ACTION_FETCH) {
                if (response.equals("success")) {
                    Log.d(TAG, "successful fetch");

                    mAgeText.setText(values[0]);
                    mHeightText.setText(values[1]);

                    Toast.makeText(getApplicationContext(), "Fetch successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Fetch unsuccessful", Toast.LENGTH_SHORT).show();
                }
            } else if (callType == RestApiService.ACTION_UPDATE) {
                if (response.equals("success")) {
                    Log.d(TAG, "successful update");
                    Toast.makeText(getApplicationContext(), "Patch successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Patch unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
