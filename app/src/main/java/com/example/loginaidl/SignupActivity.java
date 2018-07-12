package com.example.loginaidl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        LoginManager.Instance(this).registerCallback(mCallback);
    }

    private void createAccount() {
        Log.d(TAG, "createAccount");
        LoginManager.Instance(this).createAccount(mEmailText.getText().toString(), mPasswordText.getText().toString());
    }

    private ILoginInterfaceCallback mCallback = new ILoginInterfaceCallback.Stub() {
        public void onResult(int callType, String response, String[] values) {
            if (callType == RestApiService.ACTION_CREATE) {
                if (response.equals("success")) {
                    Log.d(TAG, "successful response");
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Create unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
