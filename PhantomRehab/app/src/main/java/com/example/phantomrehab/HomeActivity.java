package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    ImageView MuteIcon, PlayIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        //color management
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            TextView navbar = findViewById(R.id.navbar);
            navbar.setBackgroundColor(getColor());

            ImageView tabbar_icon = findViewById(R.id.home);
            if (getColor() == getResources().getColor(R.color.purple_theme)){ tabbar_icon.setImageResource(R.drawable.home_purple);}
            else if (getColor() == getResources().getColor(R.color.teal_theme)){ tabbar_icon.setImageResource(R.drawable.home_teal);}
            else if (getColor() == getResources().getColor(R.color.green_theme)){ tabbar_icon.setImageResource(R.drawable.home_green);}
        }

        // music control
        // play music if user returns from video activity where music stops by default
//        startService(new Intent(getApplicationContext(), MusicService.class));

        MuteIcon = findViewById(R.id.mute);
        PlayIcon = findViewById(R.id.volume);

        musicPref();

        MuteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mute();
            }
        });

        PlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
    }


    //music control
    private void mute() {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        storeMusicPref(false);

        //update UI
        PlayIcon.setVisibility(View.VISIBLE);
        MuteIcon.setVisibility(View.GONE);
    }

    private void play() {
        startService(new Intent(getApplicationContext(), MusicService.class));
        storeMusicPref(true);

        //update UI
        MuteIcon.setVisibility(View.VISIBLE);
        PlayIcon.setVisibility(View.INVISIBLE);
    }

    private void musicPref() {
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
    }

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


    //button control
    public void toMed(View view) {
        startActivity(new Intent(getApplicationContext(), MedActivity.class));
    }

    public void toReminder(View view) {
        startActivity(new Intent(getApplicationContext(), ReminderActivity.class));
    }

    public void toFAQ(View view) {
        startActivity(new Intent(getApplicationContext(), FAQsActivity.class));
    }

    //tab bar control
    public void toHome(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

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


    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }
}
