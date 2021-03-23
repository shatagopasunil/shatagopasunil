package com.vnrvjiet.tsraksha;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class HospitalAddressBox {
    private Activity activity;
    private AlertDialog dialog;
    private TextView hospitalName;
    private Button map;
    private boolean type;

    public HospitalAddressBox(Activity activity,boolean type) {
        this.activity = activity;
        this.type = type;
    }

    public void displayAlertDialog(String msg)
    {
        View view = activity.getLayoutInflater().inflate(R.layout.hospital_address_box,null);
        TextView hospitalAddress = view.findViewById(R.id.hospital_address);
        TextView hospitalTitle = view.findViewById(R.id.hospital_title);
        hospitalName = view.findViewById(R.id.hospital_name_text);
        map = view.findViewById(R.id.hospital_map_btn);
        hospitalAddress.setText(msg);
        if(type) {
            hospitalTitle.setText(activity.getResources().getString(R.string.address));
            hospitalName.setVisibility(View.GONE);
            map.setVisibility(View.GONE);
        }
        else {
            hospitalTitle.setText(activity.getResources().getString(R.string.nearest_isolation));
            hospitalName.setVisibility(View.VISIBLE);
            map.setVisibility(View.VISIBLE);
        }
        Button ok = view.findViewById(R.id.hospital_ok);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.CustomAlertDialog);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    void  setHospitalName(String name)
    {
        hospitalName.setText(name);
    }
    void setMapLink(final String link, final String lat, final String longi)
    {
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    mapsIntent.setPackage("com.google.android.apps.maps");
                    activity.startActivity(mapsIntent);
                } catch (Exception e) {
                    try {
                        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + lat + "," + longi));
                        mapsIntent.setPackage("com.google.android.apps.maps");
                        activity.startActivity(mapsIntent);
                    } catch (Exception ex) {
                        Toast.makeText(activity, "Link expired or Google Maps not installed", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });
    }
}
