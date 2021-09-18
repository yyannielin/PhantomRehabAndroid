package com.example.phantomrehab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class BeginnerFinish extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginner_finish);
    }

    public void next(View view) {
        startActivity(new Intent(getApplicationContext(),ChooseLevel.class));
    }
}
