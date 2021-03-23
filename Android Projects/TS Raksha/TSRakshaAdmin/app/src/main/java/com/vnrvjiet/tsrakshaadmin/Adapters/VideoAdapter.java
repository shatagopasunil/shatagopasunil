package com.vnrvjiet.tsrakshaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vnrvjiet.tsrakshaadmin.Models.VideoModel;
import com.vnrvjiet.tsrakshaadmin.R;

public class VideoAdapter extends FirebaseRecyclerAdapter<VideoModel, VideoAdapter.VideoHolder> {
    private Context context;
    private String imageUrl,url;

    public VideoAdapter(@NonNull FirebaseRecyclerOptions<VideoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VideoHolder holder, final int position, @NonNull VideoModel model) {
        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
        url = model.getUrl();
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
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        imageUrl = "http://img.youtube.com/vi/" + url.substring(url.length() - 11) + "/hqdefault.jpg";
        Glide.with(context).load(imageUrl).placeholder(R.drawable.video_updates_icon).into(holder.thumbnail);
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_update, parent, false);
        context = parent.getContext();
        return new VideoHolder(view);
    }

    public class VideoHolder extends RecyclerView.ViewHolder {
        private ImageView playButton, thumbnail;
        private TextView title, date;

        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_update_image);
            playButton = itemView.findViewById(R.id.video_update_play);
            title = itemView.findViewById(R.id.video_update_title);
            date = itemView.findViewById(R.id.video_update_date);
        }
    }
}
