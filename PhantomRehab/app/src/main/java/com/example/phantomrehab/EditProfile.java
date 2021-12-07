package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

//procedure
//1. save user information in shared_preference in ProfileActivity
//2. load information through setText method in EditProfile
//3. user will choose to edit some information or not
//4. retrieve new information and update in database

public class EditProfile extends AppCompatActivity {

    private EditText user, email, db_phone;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //color management
        TextView navbar = findViewById(R.id.navbar);
        Button save = findViewById(R.id.edit);
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
            save.setBackgroundTintList(ColorStateList.valueOf(getColor()));

            ImageView tabbar_icon = findViewById(R.id.profile);
            if (getColor() == getResources().getColor(R.color.purple_theme)){ tabbar_icon.setImageResource(R.drawable.profile_purple);}
            else if (getColor() == getResources().getColor(R.color.teal_theme)){ tabbar_icon.setImageResource(R.drawable.profile_teal);}
            else if (getColor() == getResources().getColor(R.color.green_theme)){ tabbar_icon.setImageResource(R.drawable.profile_green);}
        }

        //initialize variables

        user = findViewById(R.id.enter_username);
        email = findViewById(R.id.enter_email);
        db_phone = findViewById(R.id.enter_phone);

        user.setText(loadProfile_user());
        email.setText(loadProfile_email());
        db_phone.setText(loadProfile_phone());


        //manage music
        ImageView MuteIcon, PlayIcon;
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


    //save new profile info

    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
    }

    public void editPw(View view) {
        startActivity(new Intent(getApplicationContext(), EditPassword.class));
    }

    public void save(View view) {

        //initialize database
        String phone = loadRoot();
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);

        //retrieve info entered by user
        String s1 = user.getText().toString();
        String s2 = email.getText().toString();
        String s3 = loadProfile_pw();
        String s4 = db_phone.getText().toString();

        //verify new info is valid
        boolean v = valid(s1,s2,s3,s4);

        if (v){

            //update email in fauth
            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

            assert fuser != null;
            fuser.updateEmail(s2)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), "Your email address updated.",
//                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            //store info in database
            Map<String, Object> userUpdates = new HashMap<>();
            UserHelper helper = new UserHelper(s1, s2, s3, s4);

            userUpdates.put("User Information", helper);

            reff.updateChildren(userUpdates);

            Toast.makeText(getApplicationContext(), "Your profile information has been updated.",
                    Toast.LENGTH_SHORT).show();

//            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
    }

    private boolean valid(String s1, String s2, String s3, String s4) {

        if (TextUtils.isEmpty(s1)){
            user.setError("Username is required.");
            return false;
        }

        if (TextUtils.isEmpty(s2)){
            email.setError("Email is required.");
            return false;
        }

//        if (TextUtils.isEmpty(s3)){
//            pw.setError("Password is required.");
//            return false;
//        }
//
//        if (s3.length() < 6){
//            pw.setError("Password must be at least 6 characters.");
//            return false;
//        }

        if ((TextUtils.isEmpty(s4)) || (s4.length() != 10)){
            db_phone.setError("Valid cell number is required.");
            return false;
        }

        return true;

    }


    //load profile info
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

    private String loadProfile_phone() {
        SharedPreferences sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        String s = sharedPreferences.getString("phone", "");
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
