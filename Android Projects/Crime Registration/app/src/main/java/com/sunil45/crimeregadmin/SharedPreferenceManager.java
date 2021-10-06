package com.sunil45.crimeregadmin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    public static final String IS_REGISTERED = "registered";
    public SharedPreferences sharedPreferences;
    public static final String MY_SHARED_PREFERENCES = "sharedPreferences";
    private Activity activity;

    SharedPreferenceManager(Activity activity){
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void putRegister(boolean val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_REGISTERED, val);
        editor.apply();
    }

    public boolean getRegister(){
        return sharedPreferences.getBoolean(IS_REGISTERED, false);
    }
}
