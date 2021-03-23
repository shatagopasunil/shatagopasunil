package com.vnrvjiet.tsrakshaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vnrvjiet.tsrakshaadmin.Models.TestingCenterModel;
import com.vnrvjiet.tsrakshaadmin.R;

public class RapidTestingAdapter extends FirebaseRecyclerAdapter<TestingCenterModel, RapidTestingAdapter.RapidViewHolder> {
    private Context context;

    public RapidTestingAdapter(@NonNull FirebaseRecyclerOptions<TestingCenterModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RapidViewHolder holder, final int position, @NonNull TestingCenterModel model) {
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.districtNames.getVisibility() == View.GONE) {
                    holder.districtNames.setVisibility(View.VISIBLE);
                    holder.imageView.setImageResource(R.drawable.open_details);
                }
                else {
                    holder.districtNames.setVisibility(View.GONE);
                    holder.imageView.setImageResource(R.drawable.close_details);

                }
            }
        });
        holder.districtName.setText(model.getName());
        holder.districtNames.setText(model.getType());
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
    }

    @NonNull
    @Override
    public RapidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rapid_antigen_layout,parent,false);
        context = view.getContext();
        return new RapidViewHolder(view);
    }

    public class RapidViewHolder extends RecyclerView.ViewHolder {
        private TextView districtName,districtNames;
        private ImageView imageView;
        private LinearLayout linearLayout;
        public RapidViewHolder(@NonNull View itemView) {
            super(itemView);
            districtName = itemView.findViewById(R.id.testing_district);
            districtNames = itemView.findViewById(R.id.testing_districts);
            imageView = itemView.findViewById(R.id.open_close_testing);
            linearLayout = itemView.findViewById(R.id.expandable_testing);
        }
    }
}

