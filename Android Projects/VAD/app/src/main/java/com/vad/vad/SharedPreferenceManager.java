package com.vad.vad;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private Context context;
    public static final String MyPREFERENCES = "SharedPreferences";
    public static final String USER_REG = "user";
    public static final String BIKE_KEY = "key";
    public static final String LICENCE = "licence";
    public static final String NAME = "name";
    private SharedPreferences sharedPreferences;

    SharedPreferenceManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public boolean getUserReg(){
        return sharedPreferences.getBoolean(USER_REG, false);
    }

    public void setUserReg(boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(USER_REG, value);
        editor.apply();
    }
    public void setBikeKey(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BIKE_KEY, key);
        editor.apply();
    }

    public String getBikeKey(){
        return  sharedPreferences.getString(BIKE_KEY, "");
    }


    public String getUserName(){
        return sharedPreferences.getString(NAME, "Unregistered");
    }

    public String getLicence(){
        return sharedPreferences.getString(LICENCE, "Unregistered");
    }

    public void setUserName(String val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, val);
        editor.apply();
    }

    public void setLicence(String val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LICENCE, val);
        editor.apply();
    }

}
