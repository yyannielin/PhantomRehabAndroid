package com.example.phantomrehab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class MedActivity  extends AppCompatActivity {

    private YouTubePlayerView Video1, Video2, Video3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med);

        //stop background music
        stopService(new Intent(getApplicationContext(), MusicService.class));

        Video1 = findViewById(R.id.youtube_player_view1);
        getLifecycle().addObserver(Video1);

        Video2 = findViewById(R.id.youtube_player_view2);
        getLifecycle().addObserver(Video2);

        Video3 = findViewById(R.id.youtube_player_view3);
        getLifecycle().addObserver(Video3);

        Video1.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                String videoId="L3-SlMJHrms"; //Chronic pain meditation | Natural Pain Relief | Relaxation for Pain
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        Video2.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                String videoId="Agb8wtAN8qc"; //Easing Pain - Guided Imagery - Relaxation Techniques
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        Video3.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                String videoId="_7rPk25nHEY"; //Cells healing the body - Free from Illness, pain and disease - Guided meditation
                youTubePlayer.cueVideo(videoId, 0);
            }
        });
    }

    public void done(View view) {
        startActivity(new Intent(getApplicationContext(),MedFinish.class));
    }


    //tab bar control
    public void toProfile(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    public void toProgress(View view) {
        startActivity(new Intent(getApplicationContext(),ProgressActivity.class));
    }

    public void toSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    public void toHome(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    public void toGMI(View view) {
        startActivity(new Intent(getApplicationContext(),ChooseLevel.class));
    }
}
