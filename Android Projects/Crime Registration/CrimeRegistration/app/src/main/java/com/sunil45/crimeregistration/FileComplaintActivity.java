package com.sunil45.crimeregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FileComplaintActivity extends AppCompatActivity {
    private Spinner spinner;
    private FirebaseAuth myAuth;
    private DatabaseReference userRef, complaintRef;
    private String date,time,categoryComplaint,ampm, uid;
    private Calendar c;
    private EditText otherCategory,address,mVictim,addInfo, addPinCode;
    private Button dateSelector,timeSelector;
    private int mYear,mMonth,mDay,mHour,mMinute;
    private ImageButton mapButton;
    private ProgressDialog loadingBar;
    private static final int LOCATION_REQUEST_CODE = 9579;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_complaint);
        c=Calendar.getInstance();
        myAuth=FirebaseAuth.getInstance();
        uid=myAuth.getCurrentUser().getUid();
        mapButton = findViewById(R.id.map_btn);
        otherCategory=(EditText)findViewById(R.id.other_category);
        addInfo=(EditText)findViewById(R.id.add_info);
        addPinCode = findViewById(R.id.pincode);
        complaintRef = FirebaseDatabase.getInstance().getReference().child("Complaints").push();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Complaints");
        loadingBar = new ProgressDialog(this);
        address=(EditText)findViewById(R.id.address);
        spinner=(Spinner)findViewById(R.id.category_complaint);
        dateSelector=(Button) findViewById(R.id.date_selector);
        timeSelector=(Button) findViewById(R.id.time_selector);
        mVictim=(EditText)findViewById(R.id.victim);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ContextCompat.checkSelfPermission(FileComplaintActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                    locationEnabled();
                } else {
                    ActivityCompat.requestPermissions(FileComplaintActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }
        });
        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog=new DatePickerDialog(FileComplaintActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        date=i2+"/"+i1+"/"+i;
                        dateSelector.setText(date);
                    }
                },
                        mYear = c.get(Calendar.YEAR),
                        mMonth = c.get(Calendar.MONTH),
                        mDay = c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        timeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(FileComplaintActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        ampm = (i < 12) ? "AM" : "PM";
                        if(ampm.equals("PM"))
                            i-=12;
                        if(i==0)
                            i=12;
                        if(i1<10)
                            time=i+":0"+i1+" "+ampm;
                        else
                            time=i+":"+i1+" "+ampm;
                        timeSelector.setText(time);
                    }
                },
                        mHour = c.get(Calendar.HOUR_OF_DAY),
                        mMinute = c.get(Calendar.MINUTE),false);
                timePickerDialog.show();
            }
        });
        findViewById(R.id.complaint_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitComplaint();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItem().toString().equals("Others"))
                    otherCategory.setVisibility(View.VISIBLE);
                else
                    otherCategory.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void submitComplaint() {
        final String pin = addPinCode.getText().toString();
        String add=address.getText().toString();
        String category=otherCategory.getText().toString();
        String addInformation=addInfo.getText().toString();
        categoryComplaint = spinner.getSelectedItem().toString();
        String victim=mVictim.getText().toString();
        if(categoryComplaint.equals("Others")) {
            if(category.isEmpty())
                Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show();
            else
            categoryComplaint = category;
        }
        if(categoryComplaint.equals("--Select--"))
            Toast.makeText(this, "Select Category of Complaint", Toast.LENGTH_SHORT).show();
        else if(categoryComplaint.equals("Others") && category.equals(""))
                Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show();
        else if(dateSelector.getText().equals("Select Date"))
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();
        else if(timeSelector.getText().equals("Select Time"))
            Toast.makeText(this, "Select Time", Toast.LENGTH_SHORT).show();
        else if(add.isEmpty())
            Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
        else if(add.length()<10)
            Toast.makeText(this, "Address must be atleast of length 10", Toast.LENGTH_SHORT).show();
        else if(pin.isEmpty())
            Toast.makeText(this, "Enter Pincode", Toast.LENGTH_SHORT).show();
        else if(pin.length() != 6)
            Toast.makeText(this, "Enter valid pincode", Toast.LENGTH_SHORT).show();
        else
        {
            loadingBar.setCancelable(false);
            loadingBar.show();
            loadingBar.setMessage("Please wait while filing your complaint");
            final HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("category", categoryComplaint);
            hashMap.put("date", date);
            hashMap.put("time", time);
            hashMap.put("address", add);
            hashMap.put("victim", victim);
            hashMap.put("additional", addInformation);
            hashMap.put("userid", uid);
            hashMap.put("pincode", pin);
            HashMap<String, Object> statusMap = new HashMap<>();
            statusMap.put("1", "Complaint Registered");
            statusMap.put("progress", 0);
            hashMap.put("Status", statusMap);
            userRef.child(complaintRef.getKey()).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        complaintRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    final DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Report").child(String.valueOf(c.get(Calendar.YEAR)))
                                            .child(String.valueOf(c.get(Calendar.MONTH))).child(pin);
                                    tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            long t = 1;
                                            if(dataSnapshot.exists()){
                                                t = dataSnapshot.getValue(Long.class) + 1;
                                            }
                                            tempRef.setValue(t).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(FileComplaintActivity.this, "Data Submitted Successfully....", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(FileComplaintActivity.this,HomeActivity.class));
                                                        finish();
                                                    }else{
                                                        Toast.makeText(FileComplaintActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }else{
                                    Toast.makeText(FileComplaintActivity.this, "Something error occured", Toast.LENGTH_SHORT).show();
                                }
                                loadingBar.dismiss();
                            }
                        });
                    }else{
                        Toast.makeText(FileComplaintActivity.this, "Something error occurred.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
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
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        loadingBar.show();
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
                locationRequest, new LocationCallback() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(FileComplaintActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationIndex = locationResult.getLocations().size() - 1;
                            Location location = new Location("");
                            location.setLatitude(locationResult.getLocations().get(locationIndex).getLatitude());
                            location.setLongitude(locationResult.getLocations().get(locationIndex).getLongitude());
                            Geocoder gc = new Geocoder(FileComplaintActivity.this);
                            if(Geocoder.isPresent()){
                                List<Address> list = null;
                                try {
                                    list = gc.getFromLocation(location.getLatitude() , location.getLongitude(),1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                assert list != null;
                                Address a = list.get(0);
                                address.setText(a.getLocality() + ", " + a.getSubAdminArea() + ", " + a.getAdminArea());
                                addPinCode.setText(a.getPostalCode());
                            }
                            loadingBar.dismiss();
                        }
                    }
                }, Looper.getMainLooper());

    }
}
