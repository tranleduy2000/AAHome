package com.example.tranleduy.aahome;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by ${DUY} on 15-Jun-16.
 */
public class Settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.layout_settings);
    }
}
