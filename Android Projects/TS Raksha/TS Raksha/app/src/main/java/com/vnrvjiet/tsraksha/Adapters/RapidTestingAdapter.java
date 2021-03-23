package com.vnrvjiet.tsraksha.Adapters;

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
import com.vnrvjiet.tsraksha.Models.TestingCenterModel;
import com.vnrvjiet.tsraksha.R;

public class RapidTestingAdapter extends FirebaseRecyclerAdapter<TestingCenterModel, RapidTestingAdapter.RapidViewHolder> {

    public RapidTestingAdapter(@NonNull FirebaseRecyclerOptions<TestingCenterModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final RapidViewHolder holder, int position, @NonNull TestingCenterModel model) {
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
    }

    @NonNull
    @Override
    public RapidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rapid_antigen_layout,parent,false);
        return new RapidViewHolder(view);
    }

    class RapidViewHolder extends RecyclerView.ViewHolder {
        private TextView districtName,districtNames;
        private ImageView imageView;
        private LinearLayout linearLayout;
        RapidViewHolder(@NonNull View itemView) {
            super(itemView);
            districtName = itemView.findViewById(R.id.testing_district);
            districtNames = itemView.findViewById(R.id.testing_districts);
            imageView = itemView.findViewById(R.id.open_close_testing);
            linearLayout = itemView.findViewById(R.id.expandable_testing);
        }
    }
}
