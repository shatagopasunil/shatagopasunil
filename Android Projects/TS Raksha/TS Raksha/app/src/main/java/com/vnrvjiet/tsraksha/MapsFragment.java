package com.vnrvjiet.tsraksha;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.vnrvjiet.tsraksha.Constants.GOVERNMENT;
import static com.vnrvjiet.tsraksha.Constants.HEALTH_FACILITIES;
import static com.vnrvjiet.tsraksha.Constants.ISOLATION_CENTERS;
import static com.vnrvjiet.tsraksha.Constants.LATITUDE;
import static com.vnrvjiet.tsraksha.Constants.LONGITUDE;
import static com.vnrvjiet.tsraksha.Constants.NAME;
import static com.vnrvjiet.tsraksha.Constants.PRIVATE;
import static com.vnrvjiet.tsraksha.Constants.UPLOAD;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private Activity activity;
    private GoogleMap map;
    private DatabaseReference locationRefs;
    private int value;
    private String s;

    public MapsFragment(int value) {
        this.value = value;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        activity = getActivity();
        ((AppCompatActivity)activity).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map_fragment);
        if(mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if(value == 3)
        {
            viewInMap(GOVERNMENT, BitmapDescriptorFactory.HUE_ROSE);
            viewInMap(PRIVATE, BitmapDescriptorFactory.HUE_BLUE);
        }
        else if(value == 2)
            viewInMap(PRIVATE,BitmapDescriptorFactory.HUE_BLUE);
        else
            viewInMap(GOVERNMENT, BitmapDescriptorFactory.HUE_ROSE);
    }

    private void viewInMap(String st, final float colorChange) {
        locationRefs = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(HEALTH_FACILITIES).child(ISOLATION_CENTERS).child(st);
        locationRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    double lat = Double.valueOf(dataSnapshot.child(LATITUDE).getValue().toString());
                    double longi = Double.valueOf(dataSnapshot.child(LONGITUDE).getValue().toString());
                    String s = dataSnapshot.child(NAME).getValue().toString();
                    LatLng pos = new LatLng(lat,longi);
                    map.addMarker(new MarkerOptions().position(pos).title(s).icon(BitmapDescriptorFactory.defaultMarker(colorChange)));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,7f));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}
