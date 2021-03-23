package com.vnrvjiet.tsraksha;


import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
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
import com.vnrvjiet.tsraksha.Services.TrackingService;

import static com.vnrvjiet.tsraksha.Constants.ACTION_START_LOCATION;
import static com.vnrvjiet.tsraksha.Constants.ACTION_STOP_LOCATION;
import static com.vnrvjiet.tsraksha.Constants.DISTANCE;
import static com.vnrvjiet.tsraksha.Constants.EMPTY;
import static com.vnrvjiet.tsraksha.Constants.LATITUDE;
import static com.vnrvjiet.tsraksha.Constants.LOAD;
import static com.vnrvjiet.tsraksha.Constants.LONGITUDE;
import static com.vnrvjiet.tsraksha.Constants.POSITIVE;
import static com.vnrvjiet.tsraksha.Constants.TIME;

public class HomeFragment extends Fragment {

    private Context context;
    private Switch trackingSwitch;
    private LinearLayout tracingLayout;
    private static final int LOCATION_PERMISSION_REQUEST = 9759;
    private static final int LOCATION_REQUEST_NUMBER = 152;
    private int distance, time;
    private TextView mTracingDistance, mTracingTime;
    private static final int LOAD_FRAGMENT_CODE = 898;
    private Resources res;
    private int[] progressTimes = {5, 10, 15, 30, 45, 60, 120, 180, 240, 300};
    private int[] progressDistances = {50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000, 10000};
    private CustomAlertDialog customAlertDialog;

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        res = getResources();
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(res.getString(R.string.app_name));
        initializeFields(view);
        return view;
    }

    private void initializeFields(View view) {
        customAlertDialog =  new CustomAlertDialog(getActivity());
        Button statusOfAPlace = view.findViewById(R.id.status_of_a_place);
        statusOfAPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatusOfAPlace();
            }
        });
        Button covidPositive = view.findViewById(R.id.covid_positive_portal);
        covidPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(context, CovidPortalActivity.class), LOAD_FRAGMENT_CODE);
            }
        });
        mTracingDistance = view.findViewById(R.id.tracing_distance);
        mTracingTime = view.findViewById(R.id.tracing_time);
        tracingLayout = view.findViewById(R.id.tracing_layout);
        distance = 500;
        time = 5000;
        mTracingDistance.setText(setDistanceText(distance));
        mTracingTime.setText(setTimeText(time));
        trackingSwitch = view.findViewById(R.id.tracking_button);
        boolean val = isLocationServiceRunning();
        ImageView changeTracingSettings = view.findViewById(R.id.change_tracing_settings);
        changeTracingSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSettingsForTracing();
            }
        });
        trackingSwitch.setChecked(val);
        setColorForModes(val);
        trackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    startLocationService();
                } else {
                    stopLocationService();
                }
                setColorForModes(trackingSwitch.isChecked());
            }
        });
    }

    private void showStatusOfAPlace() {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
            if (checkLocationEnabled())
                askRange();
            else
                showLocationOn();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_NUMBER);
        }
    }


    private void askRange() {
        View view = LayoutInflater.from(context).inflate(R.layout.status_of_place_range, null);
        final EditText distance = view.findViewById(R.id.distance_range);
        Button search = view.findViewById(R.id.search_range);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view).setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = distance.getText().toString();
                if (val.isEmpty()) {
                    distance.setError(res.getString(R.string.enter_distance));
                    return;
                }
                final int value = Integer.valueOf(val);
                if (value > 10000) {
                    distance.setError(res.getString(R.string.distance_ten));
                    return;
                }
                dialog.dismiss();
                customAlertDialog.showAlertDialog(res.getString(R.string.retrieve_cases), 1);
                getLocation(value);
            }
        });
    }

    private void changeSettingsForTracing() {
        View view = LayoutInflater.from(context).inflate(R.layout.tracing_info_layout, null);
        final SeekBar tracingTimeBar = view.findViewById(R.id.tracking_time_seek_bar);
        final SeekBar tracingDistanceBar = view.findViewById(R.id.tracking_distance_seek_bar);
        final TextView tracingDistance = view.findViewById(R.id.tracking_distance_upto);
        final TextView tracingTime = view.findViewById(R.id.tracking_time_interval);
        tracingDistance.setText(setDistanceText(distance));
        tracingTime.setText(setTimeText(time));
        tracingDistanceBar.setProgress(binarySearchElement(distance, progressDistances));
        tracingTimeBar.setProgress(binarySearchElement(time / 1000, progressTimes));
        tracingDistanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tracingDistance.setText(setDistanceText(progressDistances[i]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tracingTimeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tracingTime.setText(setTimeText(progressTimes[i] * 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                time = progressTimes[(tracingTimeBar.getProgress())] * 1000;
                distance = progressDistances[tracingDistanceBar.getProgress()];
                mTracingDistance.setText(setDistanceText(distance));
                mTracingTime.setText(setTimeText(time));
            }
        });
        builder.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }


    private String setTimeText(int time) {
        if (time < 60000)
            return time / 1000 + " sec";
        return time / 60000 + " min";
    }


    private String setDistanceText(int distanceValue) {
        if (distanceValue < 1000)
            return distanceValue + " mtrs";
        return distanceValue / 1000 + " km";
    }


    private void setColorForModes(boolean val) {
        if (val) {
            tracingLayout.setVisibility(View.GONE);
        } else {
            tracingLayout.setVisibility(View.VISIBLE);
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (TrackingService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground)
                        return true;
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                if (checkLocationEnabled()) {
                    Intent intent = new Intent(context.getApplicationContext(), TrackingService.class);
                    intent.putExtra(DISTANCE, distance);
                    intent.putExtra(TIME, time);
                    intent.setAction(ACTION_START_LOCATION);
                    context.startService(intent);
                } else {
                    trackingSwitch.setChecked(false);
                    showLocationOn();
                }
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(context.getApplicationContext(), TrackingService.class);
            intent.setAction(ACTION_STOP_LOCATION);
            context.startService(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startLocationService();
                else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    trackingSwitch.setChecked(false);
                } else {
                    trackingSwitch.setChecked(false);
                    showBox();
                }
                break;
            case LOCATION_REQUEST_NUMBER:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    showStatusOfAPlace();
                else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    showBox();
                }
                break;
        }
    }

    private void showBox() {
        new AlertDialog.Builder(context).setMessage(res.getString(R.string.denied_location))
                .setCancelable(true)
                .setPositiveButton(res.getString(R.string.settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                }).create().show();
    }

    private boolean checkLocationEnabled() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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
            return false;
        }
        return true;
    }

    private void showLocationOn() {
        new androidx.appcompat.app.AlertDialog.Builder(context)
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
    }

    private int binarySearchElement(int num, int[] arr) {
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (num == arr[mid])
                return mid;
            else if (num > arr[mid]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_FRAGMENT_CODE) {
            if (data != null) {
                boolean value = data.getBooleanExtra(LOAD, false);
                if (value) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeQuarantineOptions()).commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FaqsFragment()).commit();
                }
            }

        }
    }

    private void getLocation(final int value) {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(
                locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int locationIndex = locationResult.getLocations().size() - 1;
                            Location location = new Location(EMPTY);
                            location.setLatitude(locationResult.getLocations().get(locationIndex).getLatitude());
                            location.setLongitude(locationResult.getLocations().get(locationIndex).getLongitude());
                            showResult(location, value);
                        } else {
                            customAlertDialog.dismissAlertDialog();
                            Toast.makeText(context, res.getString(R.string.read_loc_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                }, Looper.getMainLooper());

    }

    private void showResult(final Location currentLocation, final int value) {
        DatabaseReference positiveRef = FirebaseDatabase.getInstance().getReference().child(POSITIVE);
        final String uid = FirebaseAuth.getInstance().getUid();
        positiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Location location = new Location(EMPTY);
                int cnt = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    location.setLatitude(dataSnapshot.child(LATITUDE).getValue(Double.class));
                    location.setLongitude(dataSnapshot.child(LONGITUDE).getValue(Double.class));
                    if (currentLocation.distanceTo(location) <= value && !uid.equals(dataSnapshot.getKey())) {
                        ++cnt;
                    }
                }
                customAlertDialog.dismissAlertDialog();
                new AlertDialog.Builder(context).setMessage(res.getString(R.string.show_casing,cnt,value))
                        .setPositiveButton(res.getString(R.string.ok), null).setCancelable(true).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
