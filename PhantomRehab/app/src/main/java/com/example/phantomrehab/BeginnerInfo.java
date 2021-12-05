package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BeginnerInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginner_info);

        //color management
        TextView navbar = findViewById(R.id.navbar);
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
        }
    }

    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        return sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
    }
}