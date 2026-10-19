package com.example.spendwise.network;

import android.content.Context;
import android.text.TextUtils;

import com.example.spendwise.dto.ApiEnvelope;
import com.example.spendwise.dto.AuthDtos;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ApiAuthSession {

    public interface ResultCallback {
        void onSuccess(AuthDtos.UserDto user);
        void onError(String message);
    }

    private ApiAuthSession() {
    }

    public static void login(
            Context context,
            String email,
            String password,
            ResultCallback callback
    ) {
        ExpenseApi api = ApiClient.getApi(context);

        api.login(new AuthDtos.LoginRequest(email, password))
                .enqueue(new Callback<ApiEnvelope<AuthDtos.AuthData>>() {
                    @Override
                    public void onResponse(
                            Call<ApiEnvelope<AuthDtos.AuthData>> call,
                            Response<ApiEnvelope<AuthDtos.AuthData>> response
                    ) {
                        if (!response.isSuccessful()
                                || response.body() == null
                                || !response.body().isSuccess()
                                || response.body().getData() == null
                                || response.body().getData().user == null
                                || TextUtils.isEmpty(
                                response.body().getData().token
                        )) {
                            callback.onError(readError(response));
                            return;
                        }

                        AuthDtos.AuthData data = response.body().getData();

                        new TokenStore(context).saveSession(data.token, data.user.id, data.user.fullName, data.user.email);

                        callback.onSuccess(data.user);
                    }

                    @Override
                    public void onFailure(
                            Call<ApiEnvelope<AuthDtos.AuthData>> call,
                            Throwable t
                    ) {
                        callback.onError(
                                "Network error. Check your connection and API URL."
                        );
                    }
                });
    }

    public static void register(
            Context context,
            String fullName,
            String email,
            String password,
            ResultCallback callback
    ) {
        ExpenseApi api = ApiClient.getApi(context);

        AuthDtos.RegisterRequest request =
                new AuthDtos.RegisterRequest(fullName, email, password);

        api.register(request).enqueue(
                new Callback<ApiEnvelope<Map<String, Object>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiEnvelope<Map<String, Object>>> call,
                            Response<ApiEnvelope<Map<String, Object>>> response
                    ) {
                        if (!response.isSuccessful()) {
                            callback.onError(readError(response));
                            return;
                        }

                      
                        login(context, email, password, callback);
                    }

                    @Override
                    public void onFailure(
                            Call<ApiEnvelope<Map<String, Object>>> call,
                            Throwable t
                    ) {
                        callback.onError(
                                "Cannot reach the server. Check your connection."
                        );
                    }
                }
        );
    }

    public static void logout(Context context, Runnable onComplete) {
        TokenStore store = new TokenStore(context);

        if (!store.isLoggedIn()) {
            store.clearSession();
            if (onComplete != null) onComplete.run();
            return;
        }

        ApiClient.getApi(context).logout().enqueue(
                new Callback<ApiEnvelope<Map<String, Object>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiEnvelope<Map<String, Object>>> call,
                            Response<ApiEnvelope<Map<String, Object>>> response
                    ) {
                      
                        store.clearSession();
                        if (onComplete != null) onComplete.run();
                    }

                    @Override
                    public void onFailure(
                            Call<ApiEnvelope<Map<String, Object>>> call,
                            Throwable t
                    ) {
                        store.clearSession();
                        if (onComplete != null) onComplete.run();
                    }
                }
        );
    }

    private static String readError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                if (!body.isEmpty()) return body;
            }
        } catch (IOException ignored) {
        }

        return "Request failed (HTTP " + response.code() + ").";
    }
}
