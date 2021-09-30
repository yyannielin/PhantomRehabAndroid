package com.example.phantomrehab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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

public class MainActivity extends AppCompatActivity {

    private EditText Username, Password, Cell;
    private Button Login;

    private FirebaseAuth fAuth;

    private ImageView PlayIcon, MuteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization
        Username = (EditText) findViewById(R.id.enter_username);
        Cell = (EditText) findViewById(R.id.enter_phone);
        Password = (EditText) findViewById(R.id.enter_pw);
        Login = (Button) findViewById(R.id.btn_login);

        fAuth = FirebaseAuth.getInstance();

        MuteIcon = findViewById(R.id.mute);
        PlayIcon = findViewById(R.id.volume);

        //set up authentification
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((Username.getText() != null) || (Password.getText() != null)) {

                    String user = Username.getText().toString();
                    String pw = Password.getText().toString();

                    validate(user, pw);
                }
            }
        });

        //play background music upon launching app
        startService(new Intent(getApplicationContext(), MusicService.class));

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


    //store user-root

    private void storeRoot() {
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("root", Cell.getText().toString());
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
                                Toast.makeText(MainActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, "Login failed. " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

        storeRoot();
    }

    public void register(View view) {
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
    }
}