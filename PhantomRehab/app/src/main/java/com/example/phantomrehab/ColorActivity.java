package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ColorActivity extends AppCompatActivity {

    TextView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        //color management
        navbar = findViewById(R.id.navbar);
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
        }

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


    //color management
    private void storeColor(int color) {
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("color",color);
        editor.apply();
    }

    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }

    public void changeToBlue(View view) {
        navbar.setBackgroundColor(getResources().getColor(R.color.blue_theme));
        storeColor(getResources().getColor(R.color.blue_theme));

        restartApp();
    }

    public void changeToTeal(View view) {
        navbar.setBackgroundColor(getResources().getColor(R.color.teal_theme));
        storeColor(getResources().getColor(R.color.teal_theme));

        restartApp();
    }

    public void changeToGreen(View view) {
        navbar.setBackgroundColor(getResources().getColor(R.color.green_theme));
        storeColor(getResources().getColor(R.color.green_theme));

        restartApp();
    }

    public void changeToPurple(View view) {
        navbar.setBackgroundColor(getResources().getColor(R.color.purple_theme));
        storeColor(getResources().getColor(R.color.purple_theme));

        restartApp();
    }

    private void restartApp() {
        startActivity(new Intent(getApplicationContext(), ColorActivity.class));
        finish();
    }

    // return to settings
    public void ret(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
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
