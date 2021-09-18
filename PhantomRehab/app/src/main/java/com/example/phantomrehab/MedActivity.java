package com.example.phantomrehab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class MedActivity  extends AppCompatActivity {

    private YouTubePlayerView YouTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med);

        //stop background music
        stopService(new Intent(getApplicationContext(), MusicService.class));

        YouTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(YouTubePlayerView);
    }

    public void done(View view) {
        startActivity(new Intent(getApplicationContext(),MedFinish.class));
    }

    public void toSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }
}
