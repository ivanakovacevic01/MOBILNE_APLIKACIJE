package com.example.eventapp;
import android.content.Context;
import android.content.SharedPreferences;
public class SharedPreferencesManager {
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_FIREBASE_TOKEN = "firebase_token";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_EMAIL = "user_email";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private SharedPreferencesManager() {
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        if (editor == null) {
            editor = getSharedPreferences(context).edit();
        }
        return editor;
    }

    public static void saveToken(Context context, String token) {
        getEditor(context).putString(KEY_FIREBASE_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        return getSharedPreferences(context).getString(KEY_FIREBASE_TOKEN, null);
    }

    public static void clearToken(Context context) {
        getEditor(context).remove(KEY_FIREBASE_TOKEN).apply();
    }


    public static void saveUserRole(Context context, String role) {
        getEditor(context).putString(KEY_USER_ROLE, role).apply();
    }

    public static String getUserRole(Context context) {
        return getSharedPreferences(context).getString(KEY_USER_ROLE, null);
    }

    public static void clearUserRole(Context context) {
        getEditor(context).remove(KEY_USER_ROLE).apply();
    }

    public static void saveEmail(Context context, String email) {
        getEditor(context).putString(KEY_USER_EMAIL, email).apply();
    }

    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, null);
    }

    public static void clearEmail(Context context) {
        getEditor(context).remove(KEY_USER_EMAIL).apply();
    }
}
