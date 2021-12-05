package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class IntermedFinish extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermed_finish);

        //color management
//        TextView navbar = findViewById(R.id.navbar);
        Button ret = (Button) findViewById(R.id.btn_return);

        if (getColor() != getResources().getColor(R.color.blue_theme)){
//            navbar.setBackgroundColor(getColor());
            ret.setBackgroundTintList(ColorStateList.valueOf(getColor()));
        }
    }

    public void ret(View view) {
        startActivity(new Intent(getApplicationContext(),ChooseLevel.class));
    }

    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }
}
