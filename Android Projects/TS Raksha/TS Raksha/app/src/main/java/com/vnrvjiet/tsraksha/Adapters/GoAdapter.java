package com.vnrvjiet.tsraksha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vnrvjiet.tsraksha.GoLayout;
import com.vnrvjiet.tsraksha.Models.GoModel;
import com.vnrvjiet.tsraksha.R;

public class GoAdapter extends FirebaseRecyclerAdapter<GoModel, GoAdapter.GoViewHolder> {
    private Context context;

    public GoAdapter(@NonNull FirebaseRecyclerOptions<GoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GoViewHolder holder, int position, @NonNull final GoModel model) {
        holder.goTitle.setText(model.getTitle());
        holder.goDate.setText(model.getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new GoLayout(model.getUrl(),model.getTitle())).addToBackStack(null).commit();
            }
        });
    }

    @NonNull
    @Override
    public GoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.go_single_layout,parent,false);
        context = view.getContext();
        return new GoViewHolder(view);
    }

    public class GoViewHolder extends RecyclerView.ViewHolder {
        private TextView goTitle, goDate;

        public GoViewHolder(@NonNull View itemView) {
            super(itemView);
            goTitle = itemView.findViewById(R.id.go_title);
            goDate = itemView.findViewById(R.id.go_date);
        }
    }
}
