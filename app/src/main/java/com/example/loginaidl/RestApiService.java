package com.example.loginaidl;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class RestApiService extends Service {
    private static final String REST_API_URL = "https://mirror-android-test.herokuapp.com";
    private static final String TAG = "IntentService";
    private User mUser = null;

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
    };

    public interface LoginApi {
        @POST("/users")
        Call<User> create(@Body CreateBody loginInfo);
    }

    private class CreateBody {
        final String username;
        final String password;

        public CreateBody(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
