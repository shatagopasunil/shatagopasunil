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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button markMyAttendance;
    private static final int LOCATION_REQUEST_CODE = 123;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    private double mLatitude, mLongitude, mDistance;
    private String mSubject, mClass;
    private AlertDialog closeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        markMyAttendance = findViewById(R.id.mark_my_attendance);
        markMyAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                ArrayList<String> arrayList = new ArrayList<>();
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_attendance_layout, null);
                Spinner selectClass = view.findViewById(R.id.select_class);
                Spinner selectSubject = view.findViewById(R.id.select_subject);
                reference.child("Classes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long i = 0, cnt = snapshot.getChildrenCount();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ++i;
                            arrayList.add(dataSnapshot.getKey());
                            if(i == cnt){
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                        MainActivity.this, android.R.layout.simple_spinner_item, arrayList);
                                selectClass.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                selectClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(MainActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                        reference.child("Classes").child(arrayList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> classes = new ArrayList<>();
                                long i = 0, cnt = snapshot.getChildrenCount();
                                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ++i;
                                    classes.add(dataSnapshot.getKey());
                                    if(i == cnt){
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                MainActivity.this, android.R.layout.simple_spinner_item, classes);
                                        selectSubject.setAdapter(adapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view).setCancelable(true).setMessage("View Attendance")
                        .setPositiveButton("Mark", null).setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                closeDialog = dialog;
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reference.child("Locations").child(selectClass.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mLatitude = snapshot.child("latitude").getValue(Double.class);
                                mLongitude = snapshot.child("longitude").getValue(Double.class);
                                mDistance = snapshot.child("distance").getValue(Double.class);
                                mSubject = selectSubject.getSelectedItem().toString();
                                mClass = selectClass.getSelectedItem().toString();
                                fun();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fun(){
        if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
            locationEnabled();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    private void markAttendance(Location curLocation) {
        Location location = new Location("");
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);
        if (curLocation.distanceTo(location) <= mDistance) {
            DatabaseReference ref = reference.child("Classes").child(mClass).child(mSubject);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("status").getValue(String.class).equals("active")){
                        ref.child("Attendance").child(new SharedPreferenceManager(MainActivity.this).getRollNo()).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
                                closeDialog.dismiss();
                            }
                        });
                    }else{
                        Toast.makeText(MainActivity.this, "Attendance was not yet started", Toast.LENGTH_SHORT).show();
                        closeDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Toast.makeText(this, "You are not in the class", Toast.LENGTH_SHORT).show();
            closeDialog.dismiss();
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

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null)
            sendUserToActivity(LoginActivity.class);
        else if(new SharedPreferenceManager(MainActivity.this).getIsTeacher())
            sendUserToActivity(TeacherActivity.class);
    }
    private void sendUserToActivity(Class cls) {
        startActivity(new Intent(MainActivity.this, cls));
        finish();
    }
}