package com.example.loginaidl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mSignupLink;

    public static ILoginInterface loginAidl = null;

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
                try {
                    MainActivity.loginAidl.login(mEmailText.getText().toString(), mPasswordText.getText().toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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

        if (loginAidl == null) {
            Intent intent = new Intent(this, RestApiService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        AppDatabase.getAppDatabase(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            loginAidl = ILoginInterface.Stub.asInterface(service);
            try {
                loginAidl.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            loginAidl = null;
        }
    };

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
