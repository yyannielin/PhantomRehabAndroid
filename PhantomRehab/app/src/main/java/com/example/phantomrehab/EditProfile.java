package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    EditText user, email, pw;

    DatabaseReference reff;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //initialize variables

        user = findViewById(R.id.enter_username);
        email = findViewById(R.id.enter_email);
        pw = findViewById(R.id.enter_pw);

//        user.setText("new name");
        user.setText(loadProfile_user());
        email.setText(loadProfile_email());
        pw.setText(loadProfile_pw());

    }


    //save new profile info
    public void save(View view) {

        //initialize database
        phone = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);

        //retrieve info entered by user
        user.getText();
        email.getText();
        pw.getText();

        //store info in database
//        Map<String, Object> userUpdates = new HashMap<>();
//        userUpdates.put(date, chrono_val);
//
//        reff.updateChildren(userUpdates);
//
//        Toast.makeText(getApplicationContext(), "Your profile information is updated.",
//                Toast.LENGTH_SHORT).show();

    }

    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
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
