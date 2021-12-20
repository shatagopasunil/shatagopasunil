package com.vnrvjiet.attendancemarker;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private Context context;
    public static final String MyPREFERENCES = "SharedPreferences";
    public static final String IS_TEACHER = "isTeacher";
    public static final String ROLL_NO = "rollNo";
    private SharedPreferences sharedPreferences;

    SharedPreferenceManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public boolean getIsTeacher(){
        return sharedPreferences.getBoolean(IS_TEACHER, false);
    }

    public SharedPreferenceManager setIsTeacher(boolean val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_TEACHER, val);
        editor.apply();
        return this;
    }
    public SharedPreferenceManager setRollNo(String val){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ROLL_NO, val);
        editor.apply();
        return this;
    }
    public String getRollNo(){
        return sharedPreferences.getString(ROLL_NO, "");
    }
}