package com.example.phantomrehab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private EditText Username, Password;
    private Button Login;
    private TextView LoginError, SignUp;

    private FirebaseAuth fAuth;

    MediaPlayer player;
    private ImageView PlayIcon, MuteIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization
        Username = (EditText) findViewById(R.id.enter_username);
        Password = (EditText) findViewById(R.id.enter_pw);
        Login = (Button) findViewById(R.id.btn_login);
        LoginError = (TextView) findViewById(R.id.login_error);
        LoginError.setText("");
        SignUp = (TextView) findViewById(R.id.sign_up);

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

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        //play background music upon launching app
//        play();
        startService(new Intent(getApplicationContext(), MusicService.class));

        MuteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pause();
                stopService(new Intent(getApplicationContext(), MusicService.class));

                //update UI
                PlayIcon.setVisibility(View.VISIBLE);
                MuteIcon.setVisibility(View.GONE);
            }
        });

        PlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                play();
                startService(new Intent(getApplicationContext(), MusicService.class));

                //update UI
                MuteIcon.setVisibility(View.VISIBLE);
                PlayIcon.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void validate(String user, String pw){

        //admin login (connection not needed)
        if ((user.equals("admin")) && (pw.equals("1234"))){
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
//                                LoginError.setText(R.string.login_error);
                            }
                        }
                    });
        }
    }

    private void register(){
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
    }

    public void toSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    // manage background music
    // The following methods are in MusicService.class and won't be used here;
    public void play(){
        if (player == null){
            player = MediaPlayer.create(this,R.raw.bgm_dreamy_piano);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }
        player.setLooping(true);
        player.start();
    }

    public void pause(){
        if (player != null){
            player.pause();
        }
    }

    public void stop (View v){
        stopPlayer();
    }

    private void stopPlayer(){
        if (player != null){
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopPlayer();
    }
}