package com.example.spendwise.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL =
            "http://10.0.2.2/expense_api/api/";

    private static Retrofit retrofit;

    private ApiClient() {
    }

    public static synchronized Retrofit getClient(Context context) {
        if (retrofit == null) {
            Context appContext = context.getApplicationContext();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        Request.Builder builder = original.newBuilder()
                                .header("Accept", "application/json");

                        String token = new TokenStore(appContext).getToken();

                        if (token != null && !token.isEmpty()) {
                            builder.header(
                                    "Authorization",
                                    "Bearer " + token
                            );
                        }

                        return chain.proceed(builder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }

        return retrofit;
    }

    public static ExpenseApi getApi(Context context) {
        return getClient(context).create(ExpenseApi.class);
    }
}
