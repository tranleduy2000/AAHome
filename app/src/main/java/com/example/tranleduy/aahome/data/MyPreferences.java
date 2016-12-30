package com.example.tranleduy.aahome.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tranleduy on 21-May-16.
 */
public class MyPreferences {
    public static final String FILE_NAME = "my_sharedpreferences";
    public static final String QUICK_CONNECT = "quick_connect";

    public static final String AUTO_LOGIN = "auto_login";
    public static final String USER_NAME = "username";
    public static final String PASSWORD = "password";
    public static final String REMEMBER_PASS = "remember_pass";

    public static final String NULL_STRING = "";
    public static final int NULL_INT = -1;
    public static final boolean NULL_BOOL = false;

    public static final String IP_ADDRESS = "ip_address";
    public static final String PORT = "port";

    public static final String BUTTON_KEY = "DBT";
    public static final String DIRECTION_KEY = "DDI";
    public static final String TOGGLE_LED_KEY = "DTG";

    public static final String TEMP_LAST = "TEMP_LAST";
    public static final String HUMI_LAST = "HUMI_LAST";

    public static final String DEVICE = "device";
    public static final String MODE = "MODE";
    private static final String USER = "tranleduy1233";
    private static final String PASS = "tranleduy";

    public static final String SSID = "SSID";
    public static final String PASS_WIFI = "PASSWIFI";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public MyPreferences(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static String getUSER() {
        return USER;
    }

    public static String getPASS() {
        return PASS;
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, NULL_INT);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, NULL_STRING);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, NULL_BOOL);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }
}
