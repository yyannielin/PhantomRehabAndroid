package com.example.phantomrehab;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class BeginnerIntro extends AppCompatActivity {

    ImageView Select, Deselect,
            PlayIcon, MuteIcon;

    private boolean Show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginner_intro);

        //stopwatch control
        Select = findViewById(R.id.selected);
        Deselect = findViewById(R.id.deselected);

        hideStopwatch();

        Deselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStopwatch();
            }
        });

        Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStopwatch();
            }
        });


        //manage music
        MuteIcon = findViewById(R.id.mute);
        PlayIcon = findViewById(R.id.volume);

        MuteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), MusicService.class));

                //update UI
                PlayIcon.setVisibility(View.VISIBLE);
                MuteIcon.setVisibility(View.GONE);
            }
        });

        PlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), MusicService.class));

                //update UI
                MuteIcon.setVisibility(View.VISIBLE);
                PlayIcon.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void toInfo(View view) {
        startActivity(new Intent(getApplicationContext(), BeginnerInfo.class));
    }

    public void begin(View view) {
        startActivity(new Intent(getApplicationContext(), BeginnerMain.class));
    }

    //stopwatch control
    private void showStopwatch() {
        Show = true;
        storeStopwatchVar();

        //update UI
        Select.setVisibility(View.VISIBLE);
        Deselect.setVisibility(View.INVISIBLE);
    }

    private void hideStopwatch() {
        Show = false;
        storeStopwatchVar();

        //update UI
        Select.setVisibility(View.INVISIBLE);
        Deselect.setVisibility(View.VISIBLE);
    }

    private void storeStopwatchVar() {
        SharedPreferences sharedPreferences = getSharedPreferences("Stopwatch", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("stopwatch", Show);
        editor.apply();
    }

    private boolean getStopwatchVar(){
        SharedPreferences sharedPreferences = getSharedPreferences("Stopwatch", MODE_PRIVATE);
        boolean show = sharedPreferences.getBoolean("stopwatch", Show);
        return show;
    }

    //tab bar control
    public void toProfile(View view) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
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
}
