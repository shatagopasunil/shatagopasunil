package com.vnrvjiet.tsrakshaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vnrvjiet.tsrakshaadmin.Models.GoModel;
import com.vnrvjiet.tsrakshaadmin.R;

public class GoAdapter extends FirebaseRecyclerAdapter<GoModel, GoAdapter.GoViewHolder> {

    private Context context;

    public GoAdapter(@NonNull FirebaseRecyclerOptions<GoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final GoViewHolder holder, final int position, @NonNull final GoModel model) {
        holder.goText.setText(model.getTitle());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getRef(position).removeValue();
                        notifyDataSetChanged();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @NonNull
    @Override
    public GoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_go,parent,false);
        context = view.getContext();
        return new GoViewHolder(view);
    }

    public class GoViewHolder extends RecyclerView.ViewHolder {
        private TextView goText;
        public GoViewHolder(@NonNull View itemView) {
            super(itemView);
            goText = itemView.findViewById(R.id.go_text);
        }
    }
}
