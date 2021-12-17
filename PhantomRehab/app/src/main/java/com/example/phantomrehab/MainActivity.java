package com.example.phantomrehab;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText Username, Password, Cell;
    private Button Login;

    private DatabaseReference reff;
    private FirebaseAuth fAuth;

    private ImageView PlayIcon, MuteIcon;
    private TextView SignUp, ForgetPw;

    private String UID;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //color management
        TextView navbar = findViewById(R.id.navbar);
        Login = (Button) findViewById(R.id.btn_login);

        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
            Login.setBackgroundTintList(ColorStateList.valueOf(getColor()));
        }

        //initialization
        Username = (EditText) findViewById(R.id.enter_username);
//        Cell = (EditText) findViewById(R.id.enter_phone);
        Password = (EditText) findViewById(R.id.enter_pw);
        SignUp = (TextView) findViewById(R.id.sign_up);
//        ForgetPw = findViewById(R.id.forget_pw);

        fAuth = FirebaseAuth.getInstance();

        MuteIcon = findViewById(R.id.mute);
        PlayIcon = findViewById(R.id.volume);

        //register for a new account
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

        //set up authentification
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((Username.getText() != null) || (Password.getText() != null)) {

                    //email-password verification
                    String user = Username.getText().toString();
                    String pw = Password.getText().toString();
//                    String cell = Cell.getText().toString();

                    validate(user,pw);

//                    storeCellIndex(false);
//                    validateCell(cell, user); //this causes error for admin login

//                    if (loadCellIndex()) {validate(user, pw);}
//                    else {
//                        Toast.makeText(getApplicationContext(), "Login failed. Wrong cell number.",
//                                Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });

        //play background music upon launching app

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

//        startService(new Intent(getApplicationContext(), MusicService.class));

        MuteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), MusicService.class));

                //update UI
                PlayIcon.setVisibility(View.VISIBLE);
                MuteIcon.setVisibility(View.GONE);

                //save status to shared_preference
                storeMusicPref(false);
            }
        });

        PlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), MusicService.class));
                storeMusicPref(true);

                //update UI
                MuteIcon.setVisibility(View.VISIBLE);
                PlayIcon.setVisibility(View.INVISIBLE);
            }
        });
    }

    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
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


    //store user-root

    private void storeRoot() {
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("root", UID);
        editor.apply();
    }

    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");

//        Toast.makeText(getApplicationContext(), "root is"+root,
//                Toast.LENGTH_SHORT).show();

        return root;
    }


    //login validation

    private void validate(String user, String pw) {

        //admin login (connection not needed)
        if ((user.equals("admin")) && (pw.equals("1234"))) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {

            //user login
            fAuth.signInWithEmailAndPassword(user, pw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser fUser = fAuth.getCurrentUser();

                                if (fUser != null){ UID = fUser.getUid(); }
                                storeRoot();

//                                Toast.makeText(getApplicationContext(), "root is"+UID, Toast.LENGTH_SHORT).show();

                                Toast.makeText(MainActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();

//                                startActivity(new Intent(getApplicationContext(), CheckUser.class));
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, "Login failed. " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void forgetPw(View view) {
        return;
    }

//    private void validateCell(String cell, String user) {
//
//        //validate cell number:
//        // 1. access database (can database be accessed successfully?) and retrieve email
//        // 2. compare entered email with retrieved email
//
//        reff = FirebaseDatabase.getInstance().getReference().child("users");
//
//        reff.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.child(cell).exists()) {
//                    String dbEmail = snapshot.child(cell).child("User Information/email").getValue().toString();
//                    if (dbEmail.equals(user)){
//                        storeCellIndex(true);
////                        Toast.makeText(getApplicationContext(), "exist", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else {
//                    storeCellIndex(false);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }
//
//    private void storeCellIndex(boolean b) {
//        SharedPreferences sharedPreferences = getSharedPreferences("CellIndex", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("cell_index", b);
//        editor.apply();
//    }
//
//    private boolean loadCellIndex(){
//        SharedPreferences sharedPreferences = getSharedPreferences("CellIndex", MODE_PRIVATE);
//        boolean b = sharedPreferences.getBoolean("cell_index", false);
//        return b;
//    }

//    public void register(View view) {
//        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
//    }
}