package com.example.tranleduy.aahome.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tranleduy on 23-May-16.
 */
public class MyJsonReader {
    public static JSONObject parseJsonFromText(String jsonText) throws JSONException {
        JSONObject json = new JSONObject(jsonText);
        return json;
    }
}
