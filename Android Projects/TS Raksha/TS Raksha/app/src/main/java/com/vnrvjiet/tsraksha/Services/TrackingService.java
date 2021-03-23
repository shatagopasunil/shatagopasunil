package com.vnrvjiet.tsraksha.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vnrvjiet.tsraksha.MainActivity;
import com.vnrvjiet.tsraksha.R;

import static com.vnrvjiet.tsraksha.Constants.ACTION_START_LOCATION;
import static com.vnrvjiet.tsraksha.Constants.ACTION_STOP_LOCATION;
import static com.vnrvjiet.tsraksha.Constants.CHANNEL_ID;
import static com.vnrvjiet.tsraksha.Constants.CHANNEL_NAME;
import static com.vnrvjiet.tsraksha.Constants.DISTANCE;
import static com.vnrvjiet.tsraksha.Constants.EMPTY;
import static com.vnrvjiet.tsraksha.Constants.FOUND_CHANNEL_ID;
import static com.vnrvjiet.tsraksha.Constants.FOUND_CHANNEL_NAME;
import static com.vnrvjiet.tsraksha.Constants.LATITUDE;
import static com.vnrvjiet.tsraksha.Constants.LONGITUDE;
import static com.vnrvjiet.tsraksha.Constants.POSITIVE;
import static com.vnrvjiet.tsraksha.Constants.TIME;

public class TrackingService extends Service {

    private int distance, time;
    private DatabaseReference positivePatientsRef;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                Location location = new Location("");
                location.setLatitude(locationResult.getLastLocation().getLatitude());
                location.setLongitude(locationResult.getLastLocation().getLongitude());
                getNearestLocation(location);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        positivePatientsRef = FirebaseDatabase.getInstance().getReference().child(POSITIVE);
        positivePatientsRef.keepSynced(true);
    }

    private String uid = "";

    private void getNearestLocation(final Location location) {
        final Location tempLocation = new Location(EMPTY);
        positivePatientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    tempLocation.setLatitude(Double.valueOf(snapshot1.child(LATITUDE).getValue().toString()));
                    tempLocation.setLongitude(Double.valueOf(snapshot1.child(LONGITUDE).getValue().toString()));
                    if(tempLocation.distanceTo(location) < distance && !snapshot1.getKey().equals(uid)){
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
                        Notification notification = new NotificationCompat.Builder(getApplicationContext(), FOUND_CHANNEL_ID)
                                .setContentTitle(FOUND_CHANNEL_NAME)
                                .setContentText("Be alert ! The nearest positive case is located within " + distance + " mtrs.")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setStyle(new NotificationCompat.BigTextStyle())
                                .build();
                        notificationManager.notify(23,notification);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void startLocService()
    {
        uid = FirebaseAuth.getInstance().getUid();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(CHANNEL_NAME)
                .setContentText(getResources().getString(R.string.tracing_running))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(time);
        locationRequest.setFastestInterval(time);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
        );
        startForeground(1, notification);
    }

    private void stopLocService()
    {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if(action != null)
            {
                if(action.equals(ACTION_START_LOCATION)){
                    distance = intent.getIntExtra(DISTANCE,5000);
                    time = intent.getIntExtra(TIME,5000);
                    startLocService();
                }else if(action.equals(ACTION_STOP_LOCATION)){
                    stopLocService();
                }
            }
        }
        return START_NOT_STICKY;
    }
}
