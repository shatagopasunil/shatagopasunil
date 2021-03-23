package com.vnrvjiet.tsraksha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vnrvjiet.tsraksha.Models.VideoModel;
import com.vnrvjiet.tsraksha.R;
import com.vnrvjiet.tsraksha.VideoActivity;
import static com.vnrvjiet.tsraksha.Constants.LINK;
import static com.vnrvjiet.tsraksha.Constants.NAME;

public class VideoAdapter extends FirebaseRecyclerAdapter<VideoModel, VideoAdapter.VideoHolder> {
    private Context context;

    public VideoAdapter(@NonNull FirebaseRecyclerOptions<VideoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VideoHolder holder, int position, @NonNull final VideoModel model) {
        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
        String url = model.getUrl();
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra(LINK,model.getUrl());
                intent.putExtra(NAME,model.getTitle());
                context.startActivity(intent);
            }
        });
        String imageUrl = "http://img.youtube.com/vi/" + url + "/hqdefault.jpg";
        Glide.with(context).load(imageUrl).placeholder(R.drawable.video_updates_icon).into(holder.thumbnail);
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_update, parent, false);
        context = parent.getContext();
        return new VideoHolder(view);
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        private ImageView playButton, thumbnail;
        private TextView title, date;

        VideoHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_update_image);
            playButton = itemView.findViewById(R.id.video_update_play);
            title = itemView.findViewById(R.id.video_update_title);
            date = itemView.findViewById(R.id.video_update_date);
        }
    }
}
