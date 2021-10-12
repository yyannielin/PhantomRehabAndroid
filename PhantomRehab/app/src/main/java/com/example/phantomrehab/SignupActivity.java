package com.example.phantomrehab;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText Username, Phone, Email, Password, RePassword;
    private Button SignUp;
    private ImageView PlayIcon, MuteIcon;
    private FirebaseAuth fAuth;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initialize variables
        Username = findViewById(R.id.enter_username);
        Phone = findViewById(R.id.enter_phone);
        Email = findViewById(R.id.enter_email);
        Password = findViewById(R.id.enter_pw);
        RePassword = findViewById(R.id.confirm_pw);
        SignUp = findViewById(R.id.btn_signup);


        //register the account in firebase
        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });


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


    private void signup() {

        //get values

        String user = Username.getText().toString().trim();
        String phone = Phone.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String pw = Password.getText().toString().trim();
        String r_pw = RePassword.getText().toString().trim();

        //set requirement for inputs

        if (TextUtils.isEmpty(user)){
            Username.setError("Username is required.");
            return;
        }

        if ((TextUtils.isEmpty(phone)) || (phone.length() != 10)){
            Phone.setError("Valid cell number is required.");
            return;
        }

        if (TextUtils.isEmpty(email)){
            Email.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(pw)){
            Password.setError("Password is required.");
            return;
        }

        if (pw.length() < 6){
            Password.setError("Password must be at least 6 characters.");
        }

        if ((TextUtils.isEmpty(r_pw)) || (!pw.equals(r_pw))){
            RePassword.setError("Please reenter your password.");
            return;
        }

        //register the user in Firebase

        fAuth.createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignupActivity.this, "User created.",Toast.LENGTH_SHORT).show();

                            //store information in database
                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("users");

                            UserHelper helper = new UserHelper(user, email, pw, phone);
                            reference.child(phone).child("User Information").setValue(helper); //use unique phone number as ID

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        }

                        else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Authentication failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

