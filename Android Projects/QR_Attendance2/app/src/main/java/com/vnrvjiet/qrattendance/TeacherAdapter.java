package com.vnrvjiet.qrattendance;

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

public class TeacherAdapter extends FirebaseRecyclerAdapter<TeacherModel, TeacherAdapter.TeacherViewHolder> {
    private Context context;
    public TeacherAdapter(@NonNull FirebaseRecyclerOptions<TeacherModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final TeacherViewHolder holder, int position, @NonNull TeacherModel model) {
        holder.teacherName.setText(model.getName());
        holder.deleteTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context).setCancelable(true)
                        .setMessage("Are you sure you want to delete teacher?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getRef(position).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context, "Teacher deleted successfully", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(context, "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_teachers,parent,false);
        return new TeacherViewHolder(view);
    }

    public class TeacherViewHolder extends RecyclerView.ViewHolder {
        private TextView teacherName;
        private ImageView deleteTeacher;
        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherName = itemView.findViewById(R.id.teacher_name);
            deleteTeacher = itemView.findViewById(R.id.delete_teacher);
        }
    }
}