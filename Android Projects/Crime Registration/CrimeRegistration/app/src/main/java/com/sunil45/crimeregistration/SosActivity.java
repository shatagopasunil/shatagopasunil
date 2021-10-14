package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;

public class SosActivity extends AppCompatActivity {
    private ImageView sosBtn;
    private MediaPlayer mp;
    private String latitude, longitude, url, name, number;
    private TextView alertTv;
    private FirebaseAuth myAuth;
    private int count, tcount;
    private DatabaseReference sosReference;
    private static final int LOCATION_REQUEST_CODE = 9579;
    private TextView govt, pri, govPri, nearestIsolation;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        myAuth = FirebaseAuth.getInstance();
        String uid = myAuth.getCurrentUser().getUid();
        sosReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("SosList");
        sosReference.keepSynced(true);
        mp = MediaPlayer.create(SosActivity.this, R.raw.sample);
        sosBtn = (ImageView) findViewById(R.id.soSButton);
        alertTv = (TextView) findViewById(R.id.alert_text);
        sosBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mp.isPlaying()) {
                    stop();
                } else {
                    mp.start();
                    alertTv.setText("Long Press to Stop");
                    if ((ContextCompat.checkSelfPermission(SosActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                        locationEnabled();
                    } else {
                        ActivityCompat.requestPermissions(SosActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
                    }

                }
                return false;
            }
        });
    }

//    private void getLocation() {
//        final LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(3000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
//                locationRequest, new LocationCallback() {
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        super.onLocationResult(locationResult);
//                        LocationServices.getFusedLocationProviderClient(SosActivity.this).removeLocationUpdates(this);
//                        if (locationResult != null && locationResult.getLocations().size() > 0) {
//                            int locationIndex = locationResult.getLocations().size() - 1;
//                            latitude = String.valueOf(locationResult.getLocations().get(locationIndex).getLatitude());
//                            longitude = String.valueOf(locationResult.getLocations().get(locationIndex).getLongitude());
//                            url = "http://maps.google.com/maps?q=" + latitude + "," + longitude;
//                        } else {
//                        }
//                    }
//
//                }, Looper.getMainLooper());
//        getLocation(0);
//    }

    private void getLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(SosActivity.this).requestLocationUpdates(
                locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(SosActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationIndex = locationResult.getLocations().size() - 1;
                            Location location = new Location("");
                            location.setLatitude(locationResult.getLocations().get(locationIndex).getLatitude());
                            location.setLongitude(locationResult.getLocations().get(locationIndex).getLongitude());
                            url = "http://maps.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
                            sosReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        count = 0;
                                        tcount = 0;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            tcount += 1;
                                            name = snapshot.getKey();
                                            number = (String) snapshot.getValue();
                                            if (sendSms(name, number))
                                                count += 1;
                                        }
                                        Toast.makeText(SosActivity.this, count + "/" + tcount + " messages are sent..", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(SosActivity.this, "No members were found..", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(SosActivity.this, "Error in getting location", Toast.LENGTH_SHORT).show();
                        }
                    }

                }, Looper.getMainLooper());

    }

    @Override
    public void onBackPressed() {
        stop();
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }

    private void stop() {
        finish();
        mp.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_id:
                startActivity(new Intent(this,HomeActivity.class));
                finishAffinity();
                return true;
            case R.id.members_id:
                startActivity(new Intent(this,MembersList.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean sendSms(String name,String number)
    {
        try{
            SmsManager smgr = SmsManager.getDefault();
            smgr.sendTextMessage(number,null,"Alert! "+name+"\nI'm in risk now.Please help me through my location : \n\n"
                    +url,null,null);
            return  true;
        }
        catch (Exception e){
            return false;
        }
    }
    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(this)
                    .setMessage("Turn on Location")
                    .setPositiveButton("Settings", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            getLocation();
        }
    }
}
