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
}
