package com.vnrvjiet.tsraksha;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import static com.vnrvjiet.tsraksha.Constants.LINK;
import static com.vnrvjiet.tsraksha.Constants.NAME;

public class VideoActivity extends YouTubeBaseActivity {
    private String url;
    private YouTubePlayer.PlayerStateChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(VideoActivity.this);
        setContentView(R.layout.activity_video);
        url = getIntent().getStringExtra(LINK);
        String title = getIntent().getStringExtra(NAME);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.video_play);
        TextView videoTitle = findViewById(R.id.video_title);
        videoTitle.setText(title);
        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(url);
                youTubePlayer.setPlayerStateChangeListener(listener);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        listener = new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {
                onBackPressed();
            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        };
        youTubePlayerView.initialize(getResources().getString(R.string.api_key), onInitializedListener);
        ImageButton videoClose = findViewById(R.id.video_close);
        videoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
