package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.Inet4Address;

public class ChooseLevel extends AppCompatActivity implements View.OnClickListener {

    //database management
    DatabaseReference reff;

    //UI
    private Button Beginner, Intermed, Hard, IntermedLock, HardLock;
    private ImageView PlayIcon, MuteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        //initialize variables
        Beginner = findViewById(R.id.btn_beginner);
        Intermed = findViewById(R.id.intermed_unlock);
        IntermedLock = findViewById(R.id.intermed_lock);
        Hard = findViewById(R.id.hard_unlock);
        HardLock = findViewById(R.id.hard_lock);

        Beginner.setOnClickListener(this);
        Intermed.setOnClickListener(this);
        Hard.setOnClickListener(this);

        MuteIcon = findViewById(R.id.mute);
        PlayIcon = findViewById(R.id.volume);


        //level monitor

        //load user info --> lat, mot, and mir --> status == 1 means passed, status < 1 means not passed
        //load user root and database
        String phone = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);

        //retrieve info
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //retrieve info from database and update UI based on the user's current level
                if (snapshot.child("Motor Imagery/MotThresh").exists()){
                    String dbLat = snapshot.child("Motor Imagery/MotThresh").getValue().toString();

                    //update UI
                    IntermedLock.setVisibility(View.INVISIBLE);
                    Intermed.setVisibility(View.VISIBLE);

                    HardLock.setVisibility(View.INVISIBLE);
                    Hard.setVisibility(View.VISIBLE);
                }

                else if (snapshot.child("Laterality Training/LatThresh").exists()){
                    String dbLat = snapshot.child("Laterality Training/LatThresh").getValue().toString();
//                    Toast.makeText(getApplicationContext(), "lat:"+ dbLat, Toast.LENGTH_SHORT).show();

                    //update UI
                    IntermedLock.setVisibility(View.INVISIBLE);
                    Intermed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        // music control
        // play music if user returns from video activity where music stops by default
        startService(new Intent(getApplicationContext(), MusicService.class));

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


    //master control
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_beginner:
                startActivity(new Intent(this,BeginnerIntro.class));
                break;
            case R.id.intermed_unlock:
                startActivity(new Intent(this,IntermedMain.class));
                break;
            case R.id.hard_unlock:
                startActivity(new Intent(this,HardMain.class));
                break;
        }
    }


    //music control
    private void mute() {
        stopService(new Intent(getApplicationContext(), MusicService.class));

        //update UI
        PlayIcon.setVisibility(View.VISIBLE);
        MuteIcon.setVisibility(View.GONE);
    }

    private void play() {
        startService(new Intent(getApplicationContext(), MusicService.class));

        //update UI
        MuteIcon.setVisibility(View.VISIBLE);
        PlayIcon.setVisibility(View.INVISIBLE);
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


    //level monitor
    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
    }

}

