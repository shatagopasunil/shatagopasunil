package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vnrvjiet.tsraksha.Adapters.VideoAdapter;
import com.vnrvjiet.tsraksha.Models.VideoModel;

import static com.vnrvjiet.tsraksha.Constants.UPLOAD;
import static com.vnrvjiet.tsraksha.Constants.VIDEO_UPDATES;


public class VideoUpdatesFragment extends Fragment {
    private RecyclerView videoRecycler;
    private DatabaseReference videoRef;
    private Context context;

    public VideoUpdatesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_updates, container, false);
        context = getContext();
        Application.getInstance().initAppLanguage(context);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(getResources().getString(R.string.video_updates));
        initializeFields(view);
        settingAdapter();
        return view;
    }

    private void settingAdapter() {
        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>().setQuery(videoRef, VideoModel.class).build();
        VideoAdapter adapter = new VideoAdapter(options);
        videoRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    private void initializeFields(View view) {
        videoRef = FirebaseDatabase.getInstance().getReference().child(UPLOAD).child(VIDEO_UPDATES);
        videoRecycler = view.findViewById(R.id.video_updates_recycler);
        videoRecycler.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        videoRecycler.setLayoutManager(manager);
    }

}
