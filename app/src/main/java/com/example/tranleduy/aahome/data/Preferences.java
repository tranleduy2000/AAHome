package com.example.tranleduy.aahome.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ${DUY} on 21-May-16.
 */
public class Preferences {
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
    public static final String VALUE_LIGHT_SENSOR_LAST = "VALUE_LIGHT_SENSOR_LAST";

    public static final String DEVICE = "device";
    public static final String MODE = "MODE";
    public static final String LAST_PAGE = "LAST_PAGE";

    public static final String SSID = "SSID";
    public static final String PASS_WIFI = "PASSWIFI";

    public static final String KEY_DEV = "key_developer";

    //tab security
    public static final String IS_SECURITY_ON = "IS_SECURITY_ON";
    public static final String IS_OPEN_DOOR = "IS_OPEN_DOOR";
    public static final String IS_CLOSE_DOOR = "IS_CLOSE_DOOR";
    public static final String IS_FIRE_ON = "IS_FIRE_ON";


    public static final String IS_AUTO_LIGHT = "IS_AUTO_LIGHT";
    public static final String IS_AUTO_LIGHT_DIGITAL = "IS_AUTO_LIGHT_DIGITAL";
    public static final String IS_AUTO_LIGHT_ANALOG = "IS_AUTO_LIGHT_ANALOG";
    public static final String IS_AUTO_LIGHT_PIR = "IS_AUTO_LIGHT_PIR";
    public static final String VALUE_LIGHT_ANALOG = "VALUE_LIGHT_ANALOG";


    private static final String USER = "tranleduy1233";
    private static final String PASS = "tranleduy";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public Preferences(Context context) {
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
