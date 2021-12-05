package com.example.phantomrehab;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HardMain extends AppCompatActivity {

    private Button Next;

    //level management

    private String today;
    private boolean pass;

    //database management

    DatabaseReference reff;
    private String phone;

    //stopwatch control

    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    ImageView pause, stop;
    TextView start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard);

        //level control
        today = getToday();

        //database control
        phone = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users")
                .child(phone).child("Mirror Therapy");

        //stopwatch control
        chronometer = findViewById(R.id.chronometer);
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopwatch();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseStopwatch();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStopwatch();
            }
        });

        //manage music
        ImageView PlayIcon, MuteIcon;
        MuteIcon = findViewById(R.id.mute); //click to mute
        PlayIcon = findViewById(R.id.volume);

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

        MuteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mute on click of btn; display mute icon (click to play); current status is play
                stopService(new Intent(getApplicationContext(), MusicService.class));
                storeMusicPref(false);

                //update UI
                PlayIcon.setVisibility(View.VISIBLE);
                MuteIcon.setVisibility(View.GONE);
            }
        });

        PlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), MusicService.class));
                storeMusicPref(true);

                //update UI
                MuteIcon.setVisibility(View.VISIBLE);
                PlayIcon.setVisibility(View.GONE);
            }
        });

        //call camera
        Next = findViewById(R.id.btn_next);
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(HardMain.this, new String[] {Manifest.permission.CAMERA}, 100);
                }

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_DENIED){
                    startActivity(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE));
                }

                //stop background music
                stopService(new Intent(getApplicationContext(), MusicService.class));
            }
        });
    }


    //master control
    public void done(View view) {

//        saveData();

        totalTime();

        nextLevel();

        startActivity(new Intent(getApplicationContext(), HardFinish.class));
    }


    //level control

    private void nextLevel() {

        //int day_count = loadDayCount();
        //if total_time > 2h -> day_count +=1, storeDayCount()
        //if day_count == 14 -> add MotThresh = 1 to database

        int count = loadMirDayCounter();
//
//        Toast.makeText(getApplicationContext(),"new day count:" + count,
//                Toast.LENGTH_SHORT).show();

        if (count == 14){

            //load user root and database
            reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone)
                    .child("Mirror Therapy");

            Map<String, Object> threshUpdates = new HashMap<>();
            threshUpdates.put("MirThresh", 1);

            reff.updateChildren(threshUpdates);
        }
    }

    private void saveMirDayCounter(int day_counter) {
        SharedPreferences sharedPreferences = getSharedPreferences("MirDayCounter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mir_day_counter",day_counter);
        editor.apply();
    }

    private int loadMirDayCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("MirDayCounter", MODE_PRIVATE);
        int count = sharedPreferences.getInt("mir_day_counter", 0);
        return count;
    }

    private String getToday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    private void totalTime() {
        //access database -> reff = child(today)
        //total_time = 0
        //for each child -> get value t, total_time +=t

        final int[] total_time = {0};

        //load database
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone)
                .child("Mirror Therapy");
        reff.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(today).exists()){

                    for (DataSnapshot dataSnapshot : snapshot.child(today).getChildren()){
//                        total_time[0] += (int) dataSnapshot.getValue();
                        int i = Integer.parseInt(dataSnapshot.getValue().toString());
                        total_time[0] += i;
                    }

                    //20 min * 60 * 1000
                    if (total_time[0] >= 20 * 60 * 1000) {
                        pass = true;
                    }
                    else {
                        pass = false;
                    }

                    dayCounter(pass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void dayCounter(boolean pass) {
        int day_count = loadMirDayCounter();

        if (pass) {
            day_count += 1;
            saveMirDayCounter(day_count);

        }
    }

    //database management
    public void saveProgress(View view) {
        saveData();
    }

    private void saveData() {
        String chronoText = chronometer.getText().toString();
        long chrono_val = parseChrono(chronoText);

        //load user root and database
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone)
                .child("Mirror Imagery").child(today);

        //get current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' h:mm a");
        String date = dateFormat.format(calendar.getTime());

        //store val in database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(date, chrono_val);

        reff.updateChildren(userUpdates);

        Toast.makeText(getApplicationContext(), "Your data is saved.",
                Toast.LENGTH_SHORT).show();
    }

    private long parseChrono(String chronoText) {

        //parse chronoText (unit: millisecond)
        long t = 0;
        String array[] = chronoText.split(":");

        if (array.length == 2) {
            t = Integer.parseInt(array[0]) * 60 * 1000
                    + Integer.parseInt(array[1]) * 1000;
        } else if (array.length == 3) {
            t = Integer.parseInt(array[0]) * 60 * 60 * 1000
                    + Integer.parseInt(array[1]) * 60 * 1000
                    + Integer.parseInt(array[2]) * 1000;
        }

        return t;
    }

    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
    }


    //stopwatch control
    public void startStopwatch() {
        if (!running){
            //update UI
            chronometer.setVisibility(View.VISIBLE);
            start.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);

            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    public void pauseStopwatch() {
        if (running){
            //update UI
            chronometer.setVisibility(View.INVISIBLE);
            start.setVisibility(View.VISIBLE);
            start.setText(getString(R.string.resume_stopwatch));
            pause.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.INVISIBLE);

            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void stopStopwatch() {
        chronometer.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
        start.setText(R.string.start_stopwatch_intermed);
        pause.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);

        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        running = false;
    }

    public void toSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    public void toInfo(View view) {
        startActivity(new Intent(getApplicationContext(), HardInfo.class));
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

}
