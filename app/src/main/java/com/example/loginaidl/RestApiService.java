package com.example.loginaidl;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class RestApiService extends Service {
    public static final int ACTION_LOGIN = 0;
    public static final int ACTION_CREATE = 1;
    public static final int ACTION_FETCH = 2;
    public static final int ACTION_UPDATE = 3;

    private static final String REST_API_URL = "https://mirror-android-test.herokuapp.com";
    private static final String TAG = "IntentService";
    private UserDB curUser = null;
    final RemoteCallbackList<ILoginInterfaceCallback> mCallbacks = new RemoteCallbackList<ILoginInterfaceCallback>();

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    private final ILoginInterface.Stub binder = new ILoginInterface.Stub() {
        @Override
        public void createAccount(final String username, final String password) throws RemoteException {
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
                    User userResponse = response.body();

                    if (response.isSuccessful()) {
                        UserDB user = new UserDB();
                        user.setUsername(userResponse.getUsername());
                        user.setUid(Integer.valueOf(userResponse.getId()));
                        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                        db.userDao().insertAll(user);

                        try {
                            Message message = new Message();
                            message.what = ACTION_CREATE;
                            Bundle data = new Bundle();
                            data.putString("response", "success");
                            message.setData(data);
                            mHandler.sendMessage(message);

                            login(username, password);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(TAG, "create unsuccessful " + response.errorBody().toString());
                        Message message = new Message();
                        message.what = ACTION_CREATE;
                        Bundle data = new Bundle();
                        data.putString("response", "failure");
                        message.setData(data);
                        mHandler.sendMessage(message);
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
                        AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());
                        UserDB userdb = db.userDao().find(username);
                        userdb.setToken(loginResponse.access_token);
                        db.userDao().update(userdb);

                        curUser = userdb;

                        Message message = new Message();
                        message.what = ACTION_LOGIN;
                        Bundle data = new Bundle();
                        data.putString("response", "success");
                        message.setData(data);
                        mHandler.sendMessage(message);

                        fetch();
                    } else {
                        Log.e(TAG, "login error response!");
                        Message message = new Message();
                        message.what = ACTION_LOGIN;
                        Bundle data = new Bundle();
                        data.putString("response", "failure");
                        message.setData(data);
                        mHandler.sendMessage(message);
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
            if (!curUser.getToken().isEmpty()) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(REST_API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                LoginApi api = retrofit.create(LoginApi.class);
                Call<User> call = api.fetch("JWT " + curUser.getToken(), String.valueOf(curUser.getUid()));
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Message message = new Message();
                        message.what = ACTION_FETCH;
                        Bundle data = new Bundle();

                        if (response.isSuccessful()) {
                            Log.d(TAG, "login fetch success");
                            User user = response.body();

                            data.putString("response", "success");
                            data.putStringArray("values", new String[] {user.getAge(), user.getHeight()});
                            message.setData(data);
                            mHandler.sendMessage(message);
                        } else {
                            data.putString("response", "failure");
                            message.setData(data);
                            mHandler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "login fetch failed!");

                        Message message = new Message();
                        message.what = ACTION_FETCH;
                        Bundle data = new Bundle();
                        data.putString("response", "failure");
                        message.setData(data);
                        mHandler.sendMessage(message);
                    }
                });
            } else {
                Log.e(TAG, "empty token, can't fetch!");
            }
        }

        @Override
        public void update(int age, int height) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(REST_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            LoginApi api = retrofit.create(LoginApi.class);
            Call<User> call = api.update(new PatchBody(age, height),"JWT " + curUser.getToken(), String.valueOf(curUser.getUid()));
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    Message message = new Message();
                    message.what = ACTION_UPDATE;
                    Bundle data = new Bundle();

                    if (response.isSuccessful()) {
                        Log.d(TAG, "login update success");
                        User user = response.body();
//
                        data.putString("response", "success");
                        message.setData(data);
                        mHandler.sendMessage(message);
                    } else {
                        data.putString("response", "failure");
                        message.setData(data);
                        mHandler.sendMessage(message);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "login fetch failed!");

                    Message message = new Message();
                    message.what = ACTION_UPDATE;
                    Bundle data = new Bundle();
                    data.putString("response", "failure");
                    message.setData(data);
                    mHandler.sendMessage(message);
                }
            });
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
                            mCallbacks.getBroadcastItem(i).onResult(ACTION_LOGIN, msg.getData().getString("response"), null);
                            break;
                        case ACTION_CREATE:
                            mCallbacks.getBroadcastItem(i).onResult(ACTION_CREATE, msg.getData().getString("response"), null);
                            break;
                        case ACTION_FETCH:
                            mCallbacks.getBroadcastItem(i).onResult(ACTION_FETCH, msg.getData().getString("response"), msg.getData().getStringArray("values"));
                            break;
                        case ACTION_UPDATE:
                            mCallbacks.getBroadcastItem(i).onResult(ACTION_UPDATE, msg.getData().getString("response"), null);
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

        @GET("/users/{id}")
        Call<User> fetch(@Header("Authorization") String jwt, @Path("id") String id);

        @PATCH("/users/{id}")
        Call<User> update(@Body PatchBody patchBody, @Header("Authorization") String jwt, @Path("id") String id);
    }

    private class CreateBody {
        final String username;
        final String password;

        private CreateBody(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    private class PatchBody {
        final int age;
        final int height;

        private PatchBody(int age, int height) {
            this.age = age;
            this.height = height;
        }
    }
}
