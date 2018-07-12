package com.example.loginaidl;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class RestApiService extends Service {
    public static final int ACTION_LOGIN = 0;

    private static final String REST_API_URL = "https://mirror-android-test.herokuapp.com";
    private static final String TAG = "IntentService";
    private User mUser = null;
    final RemoteCallbackList<ILoginInterfaceCallback> mCallbacks = new RemoteCallbackList<ILoginInterfaceCallback>();

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    private final ILoginInterface.Stub binder = new ILoginInterface.Stub() {
        @Override
        public void createAccount(String username, String password) throws RemoteException {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(REST_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            LoginApi api = retrofit.create(LoginApi.class);

            Call<User> call = api.create(new CreateBody(username, password));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Log.d(TAG, "onResponse");

                    if (response.isSuccessful()) {
                        mUser = response.body();
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "query failed!");
                }
            });
        }

        @Override
        public void login(final String username, String password) throws RemoteException {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(REST_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            LoginApi api = retrofit.create(LoginApi.class);

            Call<LoginResponse> call = api.login(new CreateBody(username, password));
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    Log.d(TAG, "login onResponse");
                    LoginResponse loginResponse = response.body();

                    if (response.isSuccessful() && loginResponse != null) {
                        if (mUser == null) {
                            mUser = new User();
                            mUser.username = username;
                        }
                        mUser.token = loginResponse.access_token;

                        Message message = new Message();
                        message.what = ACTION_LOGIN;
                        Bundle data = new Bundle();
                        data.putString("response", "success");
                        message.setData(data);
                        mHandler.sendMessage(message);
                    } else {
                        Log.e(TAG, "login error response!");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e(TAG, "login query failed!");
                }
            });
        }

        @Override
        public void fetch() {
            if (!mUser.token.isEmpty()) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(REST_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                LoginApi api = retrofit.create(LoginApi.class);
                Call<ResponseBody> call = api.fetch(new FetchBody(mUser.username, mUser.token));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e(TAG, "login fetch success");
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "login fetch failed!");
                    }
                });

            } else {
                Log.e(TAG, "empty token, can't fetch!");
            }
        }

        @Override
        public void registerCallback(ILoginInterfaceCallback callback) {
            if (callback != null)
                mCallbacks.register(callback);
        }
    };

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int numCallbacks = mCallbacks.beginBroadcast();
            for (int i = 0; i < numCallbacks; i++) {
                try {
                    switch (msg.what) {
                        case ACTION_LOGIN:
                            mCallbacks.getBroadcastItem(i).onResult( ACTION_LOGIN, msg.getData().getString("response"));
                            break;
                        default:
                            super.handleMessage(msg);
                            return;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbacks.finishBroadcast();
        }
    };

    public interface LoginApi {
        @POST("/users")
        Call<User> create(@Body CreateBody loginInfo);

        @POST("/auth")
        Call<LoginResponse> login(@Body CreateBody loginInfo);

        @GET("/fetch")
        Call<ResponseBody> fetch(@Header FetchBody fetchInfo);
    }

    private class CreateBody {
        final String username;
        final String password;

        private CreateBody(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    private class FetchBody {
        final String username;
        final String token;

        private FetchBody(String username, String token) {
            this.username = username;
            this.token = token;
        }
    }
}
