package com.vnrvjiet.tsrakshaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.vnrvjiet.tsrakshaadmin.Models.HelpLineModel;
import com.vnrvjiet.tsrakshaadmin.R;

public class HelpLineAdapter extends FirebaseRecyclerAdapter<HelpLineModel, HelpLineAdapter.HelpLineViewHolder> {
    private Context context;
    public HelpLineAdapter(@NonNull FirebaseRecyclerOptions<HelpLineModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HelpLineViewHolder holder, final int position, @NonNull final HelpLineModel model) {
        holder.helpLineTitle.setText(getRef(position).getKey());
        if(!model.getPhone1().isEmpty())
        {
            holder.phone1Layout.setVisibility(View.VISIBLE);
            holder.phone1.setText(model.getPhone1());
            holder.call1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //makeCall(model.getPhone1());
                }
            });
        }
        if(!model.getPhone2().isEmpty())
        {
            holder.phone2Layout.setVisibility(View.VISIBLE);
            holder.phone2.setText(model.getPhone2());
            holder.call2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeCall(model.getPhone2());
                }
            });

        }
        if(!model.getEmail().isEmpty())
        {
            holder.emailLayout.setVisibility(View.VISIBLE);
            holder.email.setText(model.getEmail());
        }
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

    private void makeCall(String phone1) {
        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phone1));
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public HelpLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.helpline_layout,parent,false);
        return new HelpLineViewHolder(view);
    }

    public class HelpLineViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout phone1Layout, phone2Layout, emailLayout;
        private ImageView call1, call2;
        private TextView helpLineTitle;
        private TextView phone1, phone2, email;

        public HelpLineViewHolder(@NonNull View itemView) {
            super(itemView);
            phone1Layout = itemView.findViewById(R.id.helpline_phone1_layout);
            phone2Layout = itemView.findViewById(R.id.helpline_phone2_layout);
            emailLayout = itemView.findViewById(R.id.helpline_email_layout);
            call1 = itemView.findViewById(R.id.helpline_call1);
            call2 = itemView.findViewById(R.id.helpline_call2);
            helpLineTitle = itemView.findViewById(R.id.helpline_title);
            phone1 = itemView.findViewById(R.id.helpline_phone1);
            phone2 = itemView.findViewById(R.id.helpline_phone2);
            email = itemView.findViewById(R.id.helpline_email);
        }
    }
}
