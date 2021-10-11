package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FAQsActivity extends AppCompatActivity{

    ImageView up1, up2, up3, up4, up5, down1, down2, down3, down4, down5;
    TextView f1, f2, f3, f4, f5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        //color management
        TextView navbar = findViewById(R.id.navbar);
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
        }

        //initialize variables
        up1 = findViewById(R.id.up1);
        up2 = findViewById(R.id.up2);
        up3 = findViewById(R.id.up3);
        up4 = findViewById(R.id.up4);
        up5 = findViewById(R.id.up5);

        down1 = findViewById(R.id.down1);
        down2 = findViewById(R.id.down2);
        down3 = findViewById(R.id.down3);
        down4 = findViewById(R.id.down4);
        down5 = findViewById(R.id.down5);

        f1 = findViewById(R.id.f1);
        f2 = findViewById(R.id.f2);
        f3 = findViewById(R.id.f3);
        f4 = findViewById(R.id.f4);
        f5 = findViewById(R.id.f5);

        down1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                show();
            }
        });

        down2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                show2();
            }
        });

        down3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                show3();
            }
        });

        down4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                show4();
            }
        });

        down5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                show5();
            }
        });

        up1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        up2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hide2();
            }
        });

        up3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hide3();
            }
        });

        up4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hide4();
            }
        });

        up5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hide5();
            }
        });

    }


    //individual cases
    private void show() {
        f1.setVisibility(View.VISIBLE);
        up1.setVisibility(View.VISIBLE);
        down1.setVisibility(View.GONE);
    }

    private void hide() {
        f1.setVisibility(View.GONE);
        up1.setVisibility(View.GONE);
        down1.setVisibility(View.VISIBLE);
    }

    private void show2() {
        f2.setVisibility(View.VISIBLE);
        up2.setVisibility(View.VISIBLE);
        down2.setVisibility(View.GONE);
    }

    private void hide2() {
        f2.setVisibility(View.GONE);
        up2.setVisibility(View.GONE);
        down2.setVisibility(View.VISIBLE);
    }

    private void show3() {
        f3.setVisibility(View.VISIBLE);
        up3.setVisibility(View.VISIBLE);
        down3.setVisibility(View.GONE);
    }

    private void hide3() {
        f3.setVisibility(View.GONE);
        up3.setVisibility(View.GONE);
        down3.setVisibility(View.VISIBLE);
    }

    private void show4() {
        f4.setVisibility(View.VISIBLE);
        up4.setVisibility(View.VISIBLE);
        down4.setVisibility(View.GONE);
    }

    private void hide4() {
        f4.setVisibility(View.GONE);
        up4.setVisibility(View.GONE);
        down4.setVisibility(View.VISIBLE);
    }

    private void show5() {
        f5.setVisibility(View.VISIBLE);
        up5.setVisibility(View.VISIBLE);
        down5.setVisibility(View.GONE);
    }

    private void hide5() {
        f5.setVisibility(View.GONE);
        up5.setVisibility(View.GONE);
        down5.setVisibility(View.VISIBLE);
    }


    //tab bar control
    public void toHome(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

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


    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }
}
