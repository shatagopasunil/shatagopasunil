package com.vnrvjiet.attendancemarker;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private Context context;
    public static final String MyPREFERENCES = "SharedPreferences";
    public static final String LAT = "latitude";
    public static final String LON = "longitude";
    private SharedPreferences sharedPreferences;

    SharedPreferenceManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public String getLat(){
        return sharedPreferences.getString(LAT, "");
    }

    public SharedPreferenceManager setLat(String val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAT, val);
        editor.apply();
        return this;
    }

    public String getLon(){
        return sharedPreferences.getString(LON, "");
    }

    public SharedPreferenceManager setLon(String val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LON, val);
        editor.apply();
        return this;
    }
}