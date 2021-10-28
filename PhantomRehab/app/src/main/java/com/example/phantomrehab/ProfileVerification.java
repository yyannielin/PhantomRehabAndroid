package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileVerification extends AppCompatActivity {

    EditText Phone, Password;
    ImageView PlayIcon, MuteIcon;

    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

    //verification
    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
    }

    public void verify(View view) {

        String root = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users")
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

                Phone = findViewById(R.id.phone);
                Password = findViewById(R.id.pw);
                String email = Phone.getText().toString();
                String pw = Password.getText().toString();

                if (dbEmail.equals(email)){
                    reauth(email, pw);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Reauthenticated failed. Email or password is wrong.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    private void reauth(String s_email, String s_cur_pw) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

//        final boolean[] boo = {false};

        AuthCredential credential = EmailAuthProvider.getCredential(s_email, s_cur_pw);

        if (user != null) {

            user.reauthenticate(credential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Reauthenticated successfully.",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), EditProfile.class));
//                            boo[0] = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Reauthenticated failed. Email or password is wrong.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
            ;
        }

//        return boo[0];
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

    private String loadProfile_phone(){
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