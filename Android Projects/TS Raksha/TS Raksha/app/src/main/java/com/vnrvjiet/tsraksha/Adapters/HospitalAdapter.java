package com.vnrvjiet.tsraksha.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vnrvjiet.tsraksha.HospitalAddressBox;
import com.vnrvjiet.tsraksha.Models.Hospitals;
import com.vnrvjiet.tsraksha.R;

public class HospitalAdapter extends FirebaseRecyclerAdapter<Hospitals, HospitalAdapter.HospitalViewHolder> {
    private HospitalAddressBox hospitalAddressBox;
    private Activity activity;

    public HospitalAdapter(@NonNull FirebaseRecyclerOptions<Hospitals> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull final HospitalViewHolder holder, int position, @NonNull final Hospitals model) {
        holder.name.setText(model.getName());
        holder.maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getLocation()));
                    mapsIntent.setPackage("com.google.android.apps.maps");
                    activity.startActivity(mapsIntent);
                } catch (Exception e) {
                    try {
                        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + model.getLatitude() + "," + model.getLongitude()));
                        mapsIntent.setPackage("com.google.android.apps.maps");
                        activity.startActivity(mapsIntent);
                    } catch (Exception ex) {
                        Toast.makeText(activity, "Link expired or Google Maps not installed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hospitalAddressBox = new HospitalAddressBox(activity,true);
                hospitalAddressBox.displayAlertDialog(model.getAddress());
            }
        });
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_layout, parent, false);
        return new HospitalViewHolder(view);
    }

    class HospitalViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView maps, info;

        HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.hospital_name);
            maps = itemView.findViewById(R.id.maps_btn);
            info = itemView.findViewById(R.id.info_btn);
        }
    }
}
