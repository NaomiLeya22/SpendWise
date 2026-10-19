package com.example.spendwise.network;

import android.content.Context;
import android.content.SharedPreferences;
public class TokenStore {

    private static final String PREFS = "expense_api_session";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "server_user_id";
    private static final String KEY_USER_NAME = "server_user_name";
    private static final String KEY_EMAIL = "server_user_email";

    private final SharedPreferences preferences;

    public TokenStore(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveSession(
            String token,
            long userId,
            String fullName,
            String email
    ) {
        preferences.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_USER_NAME, fullName)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public long getServerUserId() {
        return preferences.getLong(KEY_USER_ID, -1L);
    }

    public String getFullName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}
