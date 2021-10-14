package com.sunil45.crimeregadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ComplaintsAdapter extends FirebaseRecyclerAdapter<ComplaintsModel, ComplaintsAdapter.ComplaintsViewHolder> {

    private Context context;

    public ComplaintsAdapter( FirebaseRecyclerOptions<ComplaintsModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder( ComplaintsAdapter.ComplaintsViewHolder holder, int position, ComplaintsModel model) {
        String trackKey = getRef(position).getKey();
        holder.trackingId.setText(trackKey.substring(getRef(position).getKey().length() - 4));
        holder.category.setText(model.getCategory());
        holder.percent.setText(model.getStatus().get("progress").toString() + "%");
        holder.dateTime.setText(model.getTime() + ", " + model.getDate());
        holder.singleCaseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.complaint_container, new ViewComplaintFragment(model, trackKey)).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public ComplaintsViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.complaints_recycler_layout, parent, false);
        return new ComplaintsViewHolder(view);
    }

    public class ComplaintsViewHolder extends RecyclerView.ViewHolder {
        private TextView category, dateTime, trackingId, percent;
        private LinearLayout singleCaseLayout;
        public ComplaintsViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category_name);
            trackingId = itemView.findViewById(R.id.tracking_id);
            dateTime = itemView.findViewById(R.id.data_time);
            percent = itemView.findViewById(R.id.percent_completed);
            singleCaseLayout = itemView.findViewById(R.id.single_case_layout);
        }
    }
}
