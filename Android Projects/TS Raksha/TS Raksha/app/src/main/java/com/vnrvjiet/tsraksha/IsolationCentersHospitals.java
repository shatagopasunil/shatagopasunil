package com.vnrvjiet.tsraksha;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vnrvjiet.tsraksha.Adapters.HospitalAdapter;
import com.vnrvjiet.tsraksha.Models.Hospitals;

import java.util.HashMap;


import static com.vnrvjiet.tsraksha.Constants.ADDRESS;
import static com.vnrvjiet.tsraksha.Constants.EMPTY;
import static com.vnrvjiet.tsraksha.Constants.GOVERNMENT;
import static com.vnrvjiet.tsraksha.Constants.HEALTH_FACILITIES;
import static com.vnrvjiet.tsraksha.Constants.ISOLATION_CENTERS;
import static com.vnrvjiet.tsraksha.Constants.LATITUDE;
import static com.vnrvjiet.tsraksha.Constants.LOCATION;
import static com.vnrvjiet.tsraksha.Constants.LONGITUDE;
import static com.vnrvjiet.tsraksha.Constants.NAME;
import static com.vnrvjiet.tsraksha.Constants.PRIVATE;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;


public class IsolationCentersHospitals extends Fragment {

    private boolean val;
    private RecyclerView isolationRecycler;
    private DatabaseReference hospitalRef, tempRef;
    private Context context;
    private CustomAlertDialog customAlertDialog;
    private Location location;
    private HospitalAddressBox addressBox;
    private double value;
    private double maxValue = Double.MAX_VALUE;
    private HashMap<String, String> hashMap = new HashMap<>();
    private AlertDialog dialog;
    private FusedLocationProviderClient locationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST = 9579;
    private TextView govt, pri, govPri, nearestIsolation;

    IsolationCentersHospitals(boolean val) {
        this.val = val;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_isolation_centers_hospitals, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        setHasOptionsMenu(true);
        initializeFields(view);
        FirebaseRecyclerOptions<Hospitals> options = new FirebaseRecyclerOptions.Builder<Hospitals>().setQuery(hospitalRef.orderByChild(NAME), Hospitals.class).build();
        HospitalAdapter adapter = new HospitalAdapter(options, getActivity());
        isolationRecycler.setAdapter(adapter);
        adapter.startListening();
        return view;
    }

    private void initializeFields(View view) {
        addressBox = new HospitalAddressBox(getActivity(), false);
        customAlertDialog = new CustomAlertDialog(getActivity());
        isolationRecycler = view.findViewById(R.id.isolation_hospital_recycler_view);
        isolationRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        tempRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(HEALTH_FACILITIES).child(ISOLATION_CENTERS);
        String s;
        if (val)
            s = GOVERNMENT;
        else
            s = PRIVATE;
        hospitalRef = tempRef.child(s);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_hospital, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nearest_option:
                if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) == PackageManager.PERMISSION_GRANTED) {
                    visitToNearestIsolationCenter();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                }
                break;
            case R.id.view_in_maps:
                showNearestAlertDialogOptions();
                viewNearestIsolation(false);
                break;
        }
        return true;
    }

    private void viewNearestIsolation(final Boolean flag) {
        if (flag)
            nearestIsolation.setText(getResources().getString(R.string.nearest_isolation));
        else
            nearestIsolation.setText(getResources().getString(R.string.view_all_in_maps));
        govt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (flag) {
                    customAlertDialog.showAlertDialog(getResources().getString(R.string.fetching_nearest), 1);
                    getNearestCenter(GOVERNMENT, maxValue, true);
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MapsFragment(1)).addToBackStack(null).commit();
                }
            }
        });
        pri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (flag) {
                    customAlertDialog.showAlertDialog(getResources().getString(R.string.fetching_nearest), 1);
                    getNearestCenter(PRIVATE, maxValue, true);
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MapsFragment(2)).addToBackStack(null).commit();
                }
            }
        });
        govPri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (flag) {
                    customAlertDialog.showAlertDialog(getResources().getString(R.string.fetching_nearest), 1);
                    getNearestCenter(GOVERNMENT, maxValue, false);
                    getNearestCenter(PRIVATE, value, true);
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new MapsFragment(3)).addToBackStack(null).commit();
                }
            }
        });
    }

    private void showNearestAlertDialogOptions() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        getLocation();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.nearest_isolation_alert, null);
        govt = view.findViewById(R.id.nearest_government);
        pri = view.findViewById(R.id.nearest_private);
        govPri = view.findViewById(R.id.nearest_gov_pri);
        nearestIsolation = view.findViewById(R.id.view_in_maps);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void getNearestCenter(String s, final Double aValue, final boolean showOrNot) {
        tempRef.child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = 0, childernCount = snapshot.getChildrenCount();
                double distance = aValue;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    hashMap.put(LATITUDE, dataSnapshot.child(LATITUDE).getValue().toString());
                    hashMap.put(LONGITUDE, dataSnapshot.child(LONGITUDE).getValue().toString());
                    double v1 = Double.valueOf(hashMap.get(LATITUDE));
                    double v2 = Double.valueOf(hashMap.get(LONGITUDE));
                    Location tempLocation = new Location(EMPTY);
                    tempLocation.setLatitude(v1);
                    tempLocation.setLongitude(v2);
                    try {
                        double tempDistance = tempLocation.distanceTo(location);
                        if (tempDistance < distance) {
                            distance = tempDistance;
                            value = distance;
                            hashMap.put(ADDRESS, dataSnapshot.child(ADDRESS).getValue().toString());
                            hashMap.put(LOCATION, dataSnapshot.child(LOCATION).getValue().toString());
                            hashMap.put(NAME, dataSnapshot.child(NAME).getValue().toString());
                        }
                        ++count;
                        if (childernCount == count) {
                            setAlertDialogBox(hashMap, showOrNot);
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, getResources().getString(R.string.read_loc_error), Toast.LENGTH_SHORT).show();
                        customAlertDialog.dismissAlertDialog();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setAlertDialogBox(HashMap<String, String> hashMap, boolean showOrNot) {
        if (showOrNot) {
            customAlertDialog.dismissAlertDialog();
            addressBox.displayAlertDialog(hashMap.get(ADDRESS));
            addressBox.setHospitalName(hashMap.get(NAME));
            addressBox.setMapLink(hashMap.get(LOCATION), hashMap.get(LATITUDE), hashMap.get(LONGITUDE));
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        setMenuVisibility(false);
    }

    private void getLocation() {
        Task<Location> task = locationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location slocation) {
                if (slocation != null) {
                    location = new Location(EMPTY);
                    location.setLongitude(slocation.getLongitude());
                    location.setLatitude(slocation.getLatitude());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            visitToNearestIsolationCenter();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
        } else {
            new AlertDialog.Builder(context).setMessage(getResources().getString(R.string.denied_location))
                    .setCancelable(true)
                    .setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
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
    }

    private void visitToNearestIsolationCenter() {
        if (checkLocationEnabled()) {
            showNearestAlertDialogOptions();
            viewNearestIsolation(true);
        } else {
            showLocationOn();
        }
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
                .setMessage("Enable Location and Try again")
                .setPositiveButton("Settings", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
