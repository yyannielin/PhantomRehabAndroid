package com.example.phantomrehab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HardFinish extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_finish);
    }

    public void ret(View view) {
        startActivity(new Intent(getApplicationContext(),ChooseLevel.class));
    }
}
