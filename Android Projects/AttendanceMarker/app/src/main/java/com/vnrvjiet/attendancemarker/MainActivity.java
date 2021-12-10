package com.vnrvjiet.attendancemarker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private Button markMyAttendance, addLocation;
    private static final int LOCATION_REQUEST_CODE = 123;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addLocation = findViewById(R.id.addLocation);
        markMyAttendance = findViewById(R.id.mark_my_attendance);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_locations, null);
                new AlertDialog.Builder(MainActivity.this).setView(view1).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String lat = ((EditText) (view1.findViewById(R.id.get_latitude))).getText().toString();
                        if (lat.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Enter latitude", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String lon = ((EditText) (view1.findViewById(R.id.get_longitude))).getText().toString();
                        if (lon.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Enter longitude", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new SharedPreferenceManager(MainActivity.this).setLat(lat).setLon(lon);
                        Toast.makeText(MainActivity.this, "Location uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        markMyAttendance.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                    locationEnabled();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }
        });
    }

    private void markAttendance(Location curLocation) {
        Location location = new Location("");
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(MainActivity.this);
        if (sharedPreferenceManager.getLat().isEmpty()) {
            Toast.makeText(this, "Please add location and Mark Attendance", Toast.LENGTH_SHORT).show();
            return;
        }
        location.setLatitude(Double.parseDouble(sharedPreferenceManager.getLat()));
        location.setLongitude(Double.parseDouble(sharedPreferenceManager.getLon()));
        if (curLocation.distanceTo(location) <= 100) {
            Toast.makeText(this, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You are not in the location", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                    .setMessage("Turn on GPS")
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

    private void getLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
                locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationIndex = locationResult.getLocations().size() - 1;
                            Location location = new Location("");
                            location.setLatitude(locationResult.getLocations().get(locationIndex).getLatitude());
                            location.setLongitude(locationResult.getLocations().get(locationIndex).getLongitude());
                            markAttendance(location);
                        } else {
                            Toast.makeText(MainActivity.this, "Error in getting location\nTry again", Toast.LENGTH_SHORT).show();
                        }
                    }

                }, Looper.getMainLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationEnabled();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                new android.app.AlertDialog.Builder(MainActivity.this).setMessage("Location Denied")
                        .setCancelable(true)
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).create().show();
            }
        }
    }
}