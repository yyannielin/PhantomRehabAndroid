package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    TextView tvUsername, tvPhone, tvPassword, tvEmail;
    ImageView MuteIcon, PlayIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        //color management
        TextView navbar = findViewById(R.id.navbar);
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
        }

        tvUsername = findViewById(R.id.enter_username);
        tvPhone = findViewById(R.id.enter_phone);
        tvPassword = findViewById(R.id.enter_pw);
        tvEmail = findViewById(R.id.enter_email);

        loadInfo();

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

    private void loadInfo() {
        String root = loadRoot();
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("users")
                .child(root).child("User Information");

        //retrieve info
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //retrieve info from database
                String dbName = snapshot.child("user").getValue().toString();
                String dbEmail = snapshot.child("email").getValue().toString();
                String dbPassword = snapshot.child("pw").getValue().toString();
                String dbPhone = snapshot.child("phone").getValue().toString();

                storeProfile_user(dbName);
                storeProfile_email(dbEmail);
                storeProfile_pw(dbPassword);
                storeProfile_phone(dbPhone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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


    //store user-profile

    private void storeProfile_user(String user) {
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", user);

//        Toast.makeText(getApplicationContext(), "user_stored", Toast.LENGTH_SHORT).show();

        editor.apply();
    }

    private void storeProfile_email(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    private void storeProfile_pw(String pw) {
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pw", pw);
        editor.apply();
    }

    private void storeProfile_phone(String pw) {
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", pw);
        editor.apply();
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

    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }
}
