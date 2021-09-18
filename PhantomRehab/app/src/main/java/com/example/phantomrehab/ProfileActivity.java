package com.example.phantomrehab;

import android.content.Intent;
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
    TextView tvUsername, tvPhone, tvPassword, tvEmail;
    RelativeLayout Verification, ProfileInfo;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //verification
        Phone = findViewById(R.id.phone);
        Password = findViewById(R.id.pw);

        Verification = findViewById(R.id.verification);
        ProfileInfo = findViewById(R.id.profile_info);

        tvUsername = findViewById(R.id.enter_username);
        tvPhone = findViewById(R.id.enter_phone);
        tvPassword = findViewById(R.id.enter_pw);
        tvEmail = findViewById(R.id.enter_email);

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

    //verification
    public void verify(View view) {
        String phone = Phone.getText().toString().trim();

        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //retrieve info from database
                String dbName = snapshot.child("user").getValue().toString();
                String dbEmail = snapshot.child("email").getValue().toString();
                String dbPassword = snapshot.child("pw").getValue().toString();

                String phone = Phone.getText().toString();
                String pw = Password.getText().toString();

                if (dbPassword.equals(pw)){

                    Toast.makeText(ProfileActivity.this, "Identity verified.",Toast.LENGTH_SHORT).show();

                    Verification.setVisibility(View.GONE);
                    ProfileInfo.setVisibility(View.VISIBLE);

                    tvUsername.setText(dbName);
                    tvEmail.setText(dbEmail);
                    tvPassword.setText(pw);
                    tvPhone.setText(phone);
                }
                else {
                    Password.setError("Cell or password is wrong.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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