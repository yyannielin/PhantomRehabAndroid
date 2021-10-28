package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tvUsername, tvPhone, tvPassword, tvEmail;
    ImageView MuteIcon, PlayIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        tvUsername = findViewById(R.id.enter_username);
        tvPhone = findViewById(R.id.enter_phone);
        tvPassword = findViewById(R.id.enter_pw);
        tvEmail = findViewById(R.id.enter_email);

        String dbName = loadProfile_user();
        String dbEmail = loadProfile_email();
        String dbPassword = loadProfile_pw();
        String dbPhone = loadProfile_phone();

        //update textview to display user info
        tvUsername.setText(dbName);
        tvEmail.setText(dbEmail);
        tvPassword.setText("dbPassword123456");
        tvPhone.setText(dbPhone);


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


    //button control
    public void edit(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileVerification.class));
    }


    //load root node
    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
    }


    //load other user info
    private String loadProfile_phone() {
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        String s = sharedPreferences.getString("phone", "");
        return s;
    }

    private String loadProfile_user(){
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        String s = sharedPreferences.getString("user", "");
        return s;
    }

    private String loadProfile_email(){
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        String s = sharedPreferences.getString("email", "");
        return s;
    }

    private String loadProfile_pw(){
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        String s = sharedPreferences.getString("pw", "");
        return s;
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
}
