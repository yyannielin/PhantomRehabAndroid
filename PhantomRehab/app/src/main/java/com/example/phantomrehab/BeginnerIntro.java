package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BeginnerIntro extends AppCompatActivity {

    ImageView Select, Deselect,
            PlayIcon, MuteIcon;

    private boolean Show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginner_intro);

        //color management
        TextView navbar = findViewById(R.id.navbar);
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
        }

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
        ImageView PlayIcon, MuteIcon;
        MuteIcon = findViewById(R.id.mute); //click to mute
        PlayIcon = findViewById(R.id.volume);

        if (!getMusicPref()) {
            //update UI
//            Toast.makeText(getApplicationContext(), "music_pref = false", Toast.LENGTH_SHORT).show();

            MuteIcon.setVisibility(View.GONE);
            PlayIcon.setVisibility(View.VISIBLE);
        }
        else {
            //if music_pref is true, autoplay music when returning from a video activity
            startService(new Intent(getApplicationContext(), MusicService.class));
        }

        MuteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mute on click of btn; display mute icon (click to play); current status is play
                stopService(new Intent(getApplicationContext(), MusicService.class));
                storeMusicPref(false);

                //update UI
                PlayIcon.setVisibility(View.VISIBLE);
                MuteIcon.setVisibility(View.GONE);
            }
        });

        PlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), MusicService.class));
                storeMusicPref(true);

                //update UI
                MuteIcon.setVisibility(View.VISIBLE);
                PlayIcon.setVisibility(View.GONE);
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
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    public void toGMI(View view) {
        startActivity(new Intent(getApplicationContext(), ChooseLevel.class));
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

    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }

    //music management
    private void storeMusicPref(boolean pref) {
        SharedPreferences sharedPreferences = getSharedPreferences("Music", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("music",pref);
        editor.apply();
//        Toast.makeText(getApplicationContext(), "music_pref stored", Toast.LENGTH_SHORT).show();
    }

    private boolean getMusicPref(){
        SharedPreferences sharedPreferences = getSharedPreferences("Music", MODE_PRIVATE);
        return sharedPreferences.getBoolean("music", true);
    }
}
