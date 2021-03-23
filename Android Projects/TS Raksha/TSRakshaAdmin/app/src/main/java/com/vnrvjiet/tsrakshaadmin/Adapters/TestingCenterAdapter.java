package com.vnrvjiet.tsrakshaadmin.Adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TestingCenterAdapter extends FirebaseRecyclerAdapter<TestingCenterModel, TestingCenterAdapter.TestingViewHolder> {
    private Context context;
    public TestingCenterAdapter(@NonNull FirebaseRecyclerOptions<TestingCenterModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TestingViewHolder holder, final int position, @NonNull TestingCenterModel model) {
        holder.name.setText(model.getName());
        holder.type.setText(model.getType());
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
    public TestingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_testing_center,parent,false);
        context = view.getContext();
        return new TestingViewHolder(view);
    }

    public class TestingViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView type;
        public TestingViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.testing_center_name);
            type = itemView.findViewById(R.id.type_of_test);
        }
    }
}
