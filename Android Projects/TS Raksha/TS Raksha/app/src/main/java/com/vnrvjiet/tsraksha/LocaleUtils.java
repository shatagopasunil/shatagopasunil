package com.vnrvjiet.tsraksha;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

import static com.vnrvjiet.tsraksha.Constants.LANGUAGE;

class LocaleUtils {
    static final String ENG = "en";
    static final String TEL = "te";
    static final String HIN = "hi";
    
    static void initialize(Context context, String defaultLanguage) {
        setLocale(context, defaultLanguage);
    }

    public static boolean setLocale(Context context, String language) {
        return updateResources(context, language);
    }
    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            context.createConfigurationContext(configuration);
        } else {
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        }
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return true;
    }

    private static SharedPreferences getDefaultSharedPreference(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(Application.getInstance().getApplicationContext()) != null)
            return PreferenceManager.getDefaultSharedPreferences(Application.getInstance().getApplicationContext());
        else
            return null;
    }

    public static void setSelectedLanguageId(String id){
        final SharedPreferences prefs = getDefaultSharedPreference(Application.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LANGUAGE, id);
        editor.apply();
    }
    public static String getSelectedLanguageId(){
        return getDefaultSharedPreference(Application.getInstance().getApplicationContext())
                .getString(LANGUAGE, ENG);
    }


}
