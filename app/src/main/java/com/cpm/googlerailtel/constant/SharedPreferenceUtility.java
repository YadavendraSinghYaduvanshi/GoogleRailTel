package com.cpm.googlerailtel.constant;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by neerajg on 03-01-2018.
 */

public class SharedPreferenceUtility
{
    public static final String PREFS_NAME = "MyPrefsFile";
    private static SharedPreferenceUtility instance;
    private static SharedPreferences sh_pref;
    private static SharedPreferences.Editor prefsEditor;

    public SharedPreferenceUtility(Context context) {
        sh_pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceUtility getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceUtility(context);
        }
        return instance;
    }

    public void setStringData(String key, String value) {
        prefsEditor = sh_pref.edit();
        prefsEditor .putString(key, value);
        prefsEditor.commit();
    }

    public String getStringData(String key) {
        if (sh_pref!= null) {
            return sh_pref.getString(key, "");
        }
        return "";
    }

}

