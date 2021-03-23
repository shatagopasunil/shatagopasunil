package com.vnrvjiet.tsraksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
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

import java.util.HashMap;

import static com.vnrvjiet.tsraksha.Constants.EMAIL;
import static com.vnrvjiet.tsraksha.Constants.LATITUDE;
import static com.vnrvjiet.tsraksha.Constants.LOAD;
import static com.vnrvjiet.tsraksha.Constants.LOCATION;
import static com.vnrvjiet.tsraksha.Constants.LONGITUDE;
import static com.vnrvjiet.tsraksha.Constants.POSITIVE;
import static com.vnrvjiet.tsraksha.Constants.REASON;
import static com.vnrvjiet.tsraksha.Constants.SUPPORT;
import static com.vnrvjiet.tsraksha.Constants.TEL;
import static com.vnrvjiet.tsraksha.Constants.TITLE;

public class CovidPortalActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 123;
    private CustomAlertDialog customAlertDialog;
    private String uid;
    private DatabaseReference positiveRef;
    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_portal);
        initializeFields();
    }

    private void initializeFields() {
        res = getResources();
        uid = FirebaseAuth.getInstance().getUid();
        final TextView reportUpload = findViewById(R.id.report_upload);
        reportUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child(SUPPORT).child(LOCATION).child(uid);
                requestRef.keepSynced(true);
                View view1 = LayoutInflater.from(CovidPortalActivity.this).inflate(R.layout.report_location_view, null);
                final EditText mReason = view1.findViewById(R.id.reason_location);
                final EditText mEmail = view1.findViewById(R.id.email_location);
                final RadioGroup mGroup = view1.findViewById(R.id.report_location_group);
                AlertDialog.Builder builder = new AlertDialog.Builder(CovidPortalActivity.this);
                builder.setCancelable(true);
                builder.setView(view1);
                builder.setPositiveButton(res.getString(R.string.submit), null);
                builder.setNegativeButton(res.getString(R.string.cancel), null);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String email = mEmail.getText().toString().trim();
                        final String reason = mReason.getText().toString().trim();
                        final String title = mGroup.getCheckedRadioButtonId() == R.id.removal_location ? "Removal of location" : "Modification of location";
                        if (email.isEmpty() && reason.isEmpty()) {
                            mEmail.setError(res.getString(R.string.enter_email));
                            mReason.setError(res.getString(R.string.enter_reason));
                        } else if (reason.isEmpty())
                            mReason.setError(res.getString(R.string.enter_reason));
                        else if (email.isEmpty())
                            mEmail.setError(res.getString(R.string.enter_email));
                        else if (!validateEmail(email))
                            mEmail.setError(res.getString(R.string.valid_email));
                        else {
                            dialog.dismiss();
                            final CustomAlertDialog customAlertDialog = new CustomAlertDialog(CovidPortalActivity.this);
                            customAlertDialog.showAlertDialog(res.getString(R.string.submit_request), 1);
                            requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Toast.makeText(CovidPortalActivity.this, res.getString(R.string.submit_already), Toast.LENGTH_LONG).show();
                                        customAlertDialog.dismissAlertDialog();
                                    } else {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put(EMAIL, email);
                                        hashMap.put(REASON, reason);
                                        hashMap.put(TITLE, title);
                                        requestRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                    Toast.makeText(CovidPortalActivity.this, res.getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(CovidPortalActivity.this, res.getString(R.string.error_try_after_some_time), Toast.LENGTH_LONG).show();
                                                customAlertDialog.dismissAlertDialog();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });
            }
        });
        ImageView callEmergency = findViewById(R.id.call_emergency);
        callEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(TEL + 104));
                startActivity(intent);
            }
        });
        ImageView goHome = findViewById(R.id.go_home_back);
        Button goToHomeQuarantine = findViewById(R.id.go_to_home_quarantine);
        Button uploadLocation = findViewById(R.id.upload_location_to_db);
        Button readFaqs = findViewById(R.id.read_faqs);
        readFaqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntent(false);
                MainActivity.navigationMenu.setCheckedItem(R.id.faqs);
            }
        });
        uploadLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positiveRef = FirebaseDatabase.getInstance().getReference().child(POSITIVE).child(uid);
                positiveRef.keepSynced(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(CovidPortalActivity.this);
                builder.setMessage(res.getString(R.string.upload_location));
                builder.setCancelable(true);
                builder.setPositiveButton(res.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if ((ContextCompat.checkSelfPermission(CovidPortalActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                            locationEnabled();
                        } else {
                            ActivityCompat.requestPermissions(CovidPortalActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
                        }
                    }
                });
                builder.setNegativeButton(res.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        goToHomeQuarantine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendIntent(true);
                MainActivity.navigationMenu.setCheckedItem(R.id.home_quarantine);
            }
        });
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
                    .setMessage(res.getString(R.string.turn_on_gps))
                    .setPositiveButton(res.getString(R.string.settings), new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton(res.getString(R.string.cancel), null)
                    .show();
        } else {
            customAlertDialog = new CustomAlertDialog(this);
            customAlertDialog.showAlertDialog(res.getString(R.string.uploading_location), 1);
            positiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        customAlertDialog.dismissAlertDialog();
                        new AlertDialog.Builder(CovidPortalActivity.this).setCancelable(true).setMessage("Your location has been already updated. If you want to modify or remove your location. Click on below link.")
                                .setPositiveButton(res.getString(R.string.ok), null).show();
                    } else {
                        getLocation();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
                locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(CovidPortalActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationIndex = locationResult.getLocations().size() - 1;
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(LATITUDE, locationResult.getLocations().get(locationIndex).getLatitude());
                            hashMap.put(LONGITUDE, locationResult.getLocations().get(locationIndex).getLongitude());
                            positiveRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(CovidPortalActivity.this, res.getString(R.string.location_success), Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(CovidPortalActivity.this, res.getString(R.string.read_loc_error), Toast.LENGTH_SHORT).show();
                                    customAlertDialog.dismissAlertDialog();
                                }
                            });
                        } else {
                            customAlertDialog.dismissAlertDialog();
                            Toast.makeText(CovidPortalActivity.this, res.getString(R.string.error_try_after_some_time), Toast.LENGTH_SHORT).show();
                        }
                    }

                }, Looper.getMainLooper());

    }

    private void sendIntent(boolean value) {
        Intent intent = new Intent();
        intent.putExtra(LOAD, value);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean validateEmail(String sEmail) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!sEmail.trim().matches(emailPattern))
            return false;
        else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationEnabled();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                new android.app.AlertDialog.Builder(CovidPortalActivity.this).setMessage(res.getString(R.string.denied_location))
                        .setCancelable(true)
                        .setPositiveButton(res.getString(R.string.settings), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", CovidPortalActivity.this.getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).create().show();
            }
        }
    }
}
