package com.vnrvjiet.tsrakshaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsrakshaadmin.LoadingBar;
import com.vnrvjiet.tsrakshaadmin.Models.Hospitals;
import com.vnrvjiet.tsrakshaadmin.R;

import java.util.Locale;

public class HospitalAdapter extends FirebaseRecyclerAdapter<Hospitals, HospitalAdapter.HospitalViewHolder> {
    private static  Context context;
    private static LoadingBar loadingBar;

    public HospitalAdapter(@NonNull FirebaseRecyclerOptions<Hospitals> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final HospitalViewHolder holder, final int position, @NonNull final Hospitals model) {
        holder.hospitalName.setText(model.getName());
        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Address");
                builder.setMessage(model.getAddress());
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
                builder.show();
                return true;
            }
        });
        holder.mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse(model.getLocation());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout, parent, false);
        context = view.getContext();
        return new HospitalViewHolder(view);
    }

    public class HospitalViewHolder extends RecyclerView.ViewHolder {
        private TextView hospitalName;
        private ImageButton mapsButton, infoButton;

        public HospitalViewHolder(@NonNull final View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospital_name);
            mapsButton = itemView.findViewById(R.id.maps_btn);
            infoButton = itemView.findViewById(R.id.info_btn);
        }
    }
}
