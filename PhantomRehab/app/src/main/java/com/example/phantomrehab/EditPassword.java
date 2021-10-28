package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditPassword extends AppCompatActivity {

    EditText new_pw, re_pw;
    Button update;

    String s_email, s_new_pw, s_re_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        new_pw = findViewById(R.id.et_new_pw);
        re_pw = findViewById(R.id.et_re_pw);
        update = findViewById(R.id.btn_update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePw();
            }
        });


        //manage music
        ImageView MuteIcon = findViewById(R.id.mute);
        ImageView PlayIcon = findViewById(R.id.volume);

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

    //update pw
    private void changePw() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        s_new_pw = new_pw.getText().toString();
        s_re_pw = re_pw.getText().toString();

        if (TextUtils.isEmpty(s_new_pw)){
            new_pw.setError("Password is required.");
        }

        else if (s_new_pw.length() < 6){
            new_pw.setError("Password must be at least 6 characters.");
        }

        else if (!s_new_pw.equals(s_re_pw)){
            re_pw.setError("Password doesn't match.");
        }

        else{

            if (user != null) {
                user.updatePassword(s_new_pw)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Password updated successfully.",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(), EditProfile.class));
                            }
                        })

                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Password reset failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                ;
            }
        }
    }

    private String loadProfile_email(){
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        String s = sharedPreferences.getString("email", "");
        return s;
    }

    //tab bar control
    public void toProfile(View view) {
        startActivity(new Intent(getApplicationContext(), EditProfile.class));
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