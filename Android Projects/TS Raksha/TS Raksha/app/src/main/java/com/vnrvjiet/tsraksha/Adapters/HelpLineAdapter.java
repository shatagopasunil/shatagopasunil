package com.vnrvjiet.tsraksha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vnrvjiet.tsraksha.Models.HelpLineModel;
import com.vnrvjiet.tsraksha.R;

public class HelpLineAdapter extends FirebaseRecyclerAdapter<HelpLineModel, HelpLineAdapter.HelpLineViewHolder> {
    private Context context;
    public HelpLineAdapter(@NonNull FirebaseRecyclerOptions<HelpLineModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HelpLineViewHolder holder, int position, @NonNull final HelpLineModel model) {
        holder.helpLineTitle.setText(getRef(position).getKey());
        if(!model.getPhone1().isEmpty())
        {
            holder.phone1Layout.setVisibility(View.VISIBLE);
            holder.phone1.setText(model.getPhone1());
            holder.call1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeCall(model.getPhone1());
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

    class HelpLineViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout phone1Layout, phone2Layout, emailLayout;
        private ImageView call1, call2;
        private TextView helpLineTitle;
        private TextView phone1, phone2, email;

        HelpLineViewHolder(@NonNull View itemView) {
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
