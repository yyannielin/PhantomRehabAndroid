package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    EditText Phone, Password;
    ImageView PlayIcon, MuteIcon;
    TextView tvUsername, tvPhone, tvPassword, tvEmail;
    RelativeLayout Verification, ProfileInfo;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //verification
        Phone = findViewById(R.id.phone);
//        Password = findViewById(R.id.pw);

        Verification = findViewById(R.id.verification);
        ProfileInfo = findViewById(R.id.profile_info);

        tvUsername = findViewById(R.id.enter_username);
        tvPhone = findViewById(R.id.enter_phone);
        tvPassword = findViewById(R.id.enter_pw);
        tvEmail = findViewById(R.id.enter_email);

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

    //verification
    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
    }

    public void verify(View view) {

        String dbPhone = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(dbPhone);

        //retrieve info
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //retrieve info from database
                String dbName = snapshot.child("user").getValue().toString();
                String dbEmail = snapshot.child("email").getValue().toString();
                String dbPassword = snapshot.child("pw").getValue().toString();

                String phone = Phone.getText().toString();
//                String pw = Password.getText().toString();

                if (dbPhone.equals(phone)){

                    Toast.makeText(ProfileActivity.this, "Identity verified.",Toast.LENGTH_SHORT).show();

                    Verification.setVisibility(View.GONE);
                    ProfileInfo.setVisibility(View.VISIBLE);

                    tvUsername.setText(dbName);
                    tvEmail.setText(dbEmail);
                    tvPassword.setText(dbPassword);
                    tvPhone.setText(phone);

                    //store info in shared_preference in case user wants to edit info

                    storeProfile_user(dbName);
                    storeProfile_email(dbEmail);
                    storeProfile_pw(dbPassword);

                }

                else {
                    Phone.setError("Cell is wrong.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    //edit info
    public void edit(View view) {
        startActivity(new Intent(getApplicationContext(), EditProfile.class));
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
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
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
}