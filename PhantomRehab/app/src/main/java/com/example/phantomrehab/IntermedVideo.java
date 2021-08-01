package com.example.phantomrehab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class IntermedVideo extends AppCompatActivity {

    private YouTubePlayerView Video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermed_video);

        //stop background music
        stopService(new Intent(getApplicationContext(), MusicService.class));

        Video = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(Video);
    }

    public void ret(View view) {
        startActivity(new Intent(getApplicationContext(), IntermedMain.class));
    }

    public void done(View view) {
        startActivity(new Intent(getApplicationContext(), IntermedFinish.class));
    }

    public void toSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }
}
