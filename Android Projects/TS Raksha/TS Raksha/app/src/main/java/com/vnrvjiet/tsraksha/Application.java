package com.vnrvjiet.tsraksha;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.database.FirebaseDatabase;

import static com.vnrvjiet.tsraksha.Constants.CHANNEL_ID;
import static com.vnrvjiet.tsraksha.Constants.CHANNEL_NAME;
import static com.vnrvjiet.tsraksha.Constants.FOUND_CHANNEL_ID;
import static com.vnrvjiet.tsraksha.Constants.FOUND_CHANNEL_NAME;

public class Application extends android.app.Application {
    private static Application applicationInstance;

    public static synchronized Application getInstance() {
        return applicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
        createNotificationChannel();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
            NotificationChannel foundChannel = new NotificationChannel(
                    FOUND_CHANNEL_ID, FOUND_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager foundManager = getSystemService(NotificationManager.class);
            foundManager.createNotificationChannel(foundChannel);
        }
    }

    public void initAppLanguage(Context context) {
        LocaleUtils.initialize(context, LocaleUtils.getSelectedLanguageId());
    }

}
