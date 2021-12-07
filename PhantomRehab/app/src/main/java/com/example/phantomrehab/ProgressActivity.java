package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity  extends AppCompatActivity {

    ImageView PlayIcon, MuteIcon;
    ProgressBar LatProgress, MotProgress, MirProgress;
    TextView LatText, MotText, MirText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        //color management
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            TextView navbar = findViewById(R.id.navbar);
            navbar.setBackgroundColor(getColor());

            ImageView tabbar_icon = findViewById(R.id.progress);
            if (getColor() == getResources().getColor(R.color.purple_theme)){ tabbar_icon.setImageResource(R.drawable.process_purple);}
            else if (getColor() == getResources().getColor(R.color.teal_theme)){ tabbar_icon.setImageResource(R.drawable.process_teal);}
            else if (getColor() == getResources().getColor(R.color.green_theme)){ tabbar_icon.setImageResource(R.drawable.process_green);}
        }


        //display progress
        LatProgress = findViewById(R.id.pb_lat);
        LatText = findViewById(R.id.pb_lat_text);
        MotProgress = findViewById(R.id.pb_mot);
        MotText = findViewById(R.id.mot_text);
        MirProgress = findViewById(R.id.pb_mir);
        MirText = findViewById(R.id.mir_text);

        updateLatProg();
        updateMotProg();
        updateMirProg();


        //manage music
        MuteIcon = findViewById(R.id.mute);
        PlayIcon = findViewById(R.id.volume);

        if (!getMusicPref()) {
            //update UI
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


    //progress control

    private void updateLatProg() {
        int lat_count = loadDayCounter();
        int lat_prog = lat_count*100/7;

//        Toast.makeText(getApplicationContext(),"count: " + lat_count,
//                Toast.LENGTH_SHORT).show();

        LatProgress.setProgress(lat_prog);

        String formatted = getString(R.string.prog_text, lat_prog);
        LatText.setText(formatted);
    }

    private void updateMotProg() {
        int mot_count = loadMotDayCounter();
        int mot_prog = mot_count*100/14;
        String formatted = getString(R.string.mot_prog_text, mot_prog);

        MotProgress.setProgress(mot_prog);
        MotText.setText(formatted);

//        Toast.makeText(getApplicationContext(),"count: " + mot_count,
//                Toast.LENGTH_SHORT).show();
    }

    private void updateMirProg() {
        int mir_count = loadMirDayCounter();
        int mir_prog = mir_count*100/14;
        String formatted = getString(R.string.mir_prog_text, mir_prog);

        MirProgress.setProgress(mir_prog);
        MirText.setText(formatted);

//        Toast.makeText(getApplicationContext(),"count: " + mir_count,
//                Toast.LENGTH_SHORT).show();
    }

    private int loadDayCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("DayCounter", MODE_PRIVATE);
        int count = sharedPreferences.getInt("day_counter", 1);
        return count;
    }

    private int loadMotDayCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("MotDayCounter", MODE_PRIVATE);
        int count = sharedPreferences.getInt("mot_day_counter", 0);
        return count;
    }

    private int loadMirDayCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("MirDayCounter", MODE_PRIVATE);
        int count = sharedPreferences.getInt("mir_day_counter", 0);
        return count;
    }


    //tab bar control

    public void toProfile(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    public void toProgress(View view) {
        startActivity(new Intent(getApplicationContext(),ProgressActivity.class));
    }

    public void toGMI(View view) {
        startActivity(new Intent(getApplicationContext(),ChooseLevel.class));
    }

    public void toSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    public void toHome(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }


    //music management
    private void storeMusicPref(boolean pref) {
        SharedPreferences sharedPreferences = getSharedPreferences("Music", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("music",pref);
        editor.apply();
    }

    private boolean getMusicPref(){
        SharedPreferences sharedPreferences = getSharedPreferences("Music", MODE_PRIVATE);
        return sharedPreferences.getBoolean("music", true);
    }

    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }
}