package com.vad.vad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;


public class UserRecyclerAdapter extends FirebaseRecyclerAdapter<UserRecyclerModel, UserRecyclerAdapter.UserViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    
    private Context context;
    public UserRecyclerAdapter(@NonNull FirebaseRecyclerOptions<UserRecyclerModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserRecyclerModel model) {
        holder.name.setText(model.getName());
        holder.licence.setText(model.getLicence());
        holder.status.setText(model.getStatus());
        if(model.getStatus().equals("Active")) {
            holder.activeBtn.setBackgroundColor(Color.GREEN);
        }else{
            holder.activeBtn.setBackgroundColor(Color.RED);
        }
        holder.changeAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("Active", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getRef(position).child("status").setValue("Active").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(new SharedPreferenceManager(context).getBikeKey()).child("Detect")
                                        .child(model.getLicence()).setValue(model.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        holder.activeBtn.setBackgroundColor(Color.GREEN);
                                    }
                                });
                            }
                        });
                    }
                });
                builder.setNeutralButton("Inactive", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getRef(position).child("status").setValue("Inactive").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("Users").child(new SharedPreferenceManager(context).getBikeKey()).child("Detect")
                                        .child(model.getLicence()).setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        holder.activeBtn.setBackgroundColor(Color.RED);
                                    }
                                });
                            }
                        });
                    }
                });
                builder.show();
            }
        });
        if(getRef(position).getKey().equals("Admin")){
            holder.changeAccess.setVisibility(View.INVISIBLE);
        }
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setMessage("Do you want to delete the user?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String temp = getRef(position).getKey();
                        getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(new SharedPreferenceManager(context).getBikeKey()).child("Detect")
                                            .child(model.getLicence()).setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(temp.equals("Admin")){
                                                SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(context);
                                                sharedPreferenceManager.setLicence("Unregistered");
                                                sharedPreferenceManager.setUserName("Unregistered");
                                            }
                                            Toast.makeText(context, "User Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(context, "Error! Try Again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recycler_layout, parent, false);
        return new UserViewHolder(view1);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name, licence, status;
        private ImageButton activeBtn;
        private Button changeAccess, remove;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_name);
            licence = itemView.findViewById(R.id.recycler_licence);
            status = itemView.findViewById(R.id.recycler_status);
            activeBtn = itemView.findViewById(R.id.recycler_btn_color);
            changeAccess = itemView.findViewById(R.id.recycler_change_access);
            remove = itemView.findViewById(R.id.recycler_remove);
        }
    }
}
