package com.vnrvjiet.tsraksha.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vnrvjiet.tsraksha.Models.TestingCenterModel;
import com.vnrvjiet.tsraksha.R;

public class TestingCenterAdapter extends FirebaseRecyclerAdapter<TestingCenterModel, TestingCenterAdapter.TestingViewHolder> {
    public TestingCenterAdapter(@NonNull FirebaseRecyclerOptions<TestingCenterModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TestingViewHolder holder, int position, @NonNull TestingCenterModel model) {
        holder.name.setText(model.getName());
        holder.type.setText(model.getType());
    }

    @NonNull
    @Override
    public TestingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_testing_center,parent,false);
        return new TestingViewHolder(view);
    }

    class TestingViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView type;
        TestingViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.testing_center_name);
            type = itemView.findViewById(R.id.type_of_test);
        }
    }
}
