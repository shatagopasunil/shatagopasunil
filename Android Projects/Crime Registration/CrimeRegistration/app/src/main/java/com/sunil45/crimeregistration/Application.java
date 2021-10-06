package com.sunil45.crimeregistration;

import com.google.firebase.database.FirebaseDatabase;

public class Application extends android.app.Application {
    private static Application applicationInstance;

    public static synchronized Application getInstance() {
        return applicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
