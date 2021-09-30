package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IntermedVideo extends AppCompatActivity {

    //level control
    Calendar calendar;
    private String today;
    private boolean new_day;
    private boolean pass;

    //database management

    DatabaseReference reff;
    private String phone;

    //video control

    private YouTubePlayerView Video1;
    private YouTubePlayerView Video2;
    private YouTubePlayerView Video3;

    //stopwatch control

    private Chronometer chronometer;
    private long chrono_base;
    private boolean running;
    private long pauseOffset;
    ImageView pause, stop;
    TextView start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermed_video);

        //get videos
        Video1 = findViewById(R.id.youtube_player_view1);
        getLifecycle().addObserver(Video1);

        Video2 = findViewById(R.id.youtube_player_view2);
        getLifecycle().addObserver(Video2);

        Video3 = findViewById(R.id.youtube_player_view3);
        getLifecycle().addObserver(Video3);


        //level control
        calendar = Calendar.getInstance();
        today = getToday();
//        newDay(); //boolean value initialized here


        //database control
        phone = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users")
                .child(phone).child("Motor Imagery");


        //stopwatch control
//        chrono_base = loadMotTime();

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

        //stop background music
        stopService(new Intent(getApplicationContext(), MusicService.class));
    }


    //master control

    public void done(View view) {
        //stop stopwatch
        stopStopwatch();

        //calculate total time and total days of training
        totalTime();

        //level control
        nextLevel();

        startActivity(new Intent(getApplicationContext(), IntermedFinish.class));
    }


    //level control

    private void nextLevel() {

        //int day_count = loadDayCount();
        //if total_time > 2h -> day_count +=1, storeDayCount()
        //if day_count == 14 -> add MotThresh = 1 to database

        int count = loadMotDayCounter();

        Toast.makeText(getApplicationContext(),"new day count:" + count,
                Toast.LENGTH_SHORT).show();

        if (count == 14){

            //load user root and database
            reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone)
                    .child("Motor Imagery");

            Map<String, Object> threshUpdates = new HashMap<>();
            threshUpdates.put("MotThresh", 1);

            reff.updateChildren(threshUpdates);
        }
    }

    private void saveMotDayCounter(int day_counter) {
        SharedPreferences sharedPreferences = getSharedPreferences("MotDayCounter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("mot_day_counter",day_counter);
        editor.apply();
    }

    private int loadMotDayCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("MotDayCounter", MODE_PRIVATE);
        int count = sharedPreferences.getInt("mot_day_counter", 0);
        return count;
    }

    private void totalTime() {
        //access database -> reff = child(today)
        //total_time = 0
        //for each child -> get value t, total_time +=t

        final int[] total_time = {0};

        //load database
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone)
                .child("Motor Imagery");
        reff.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(today).exists()){

                    for (DataSnapshot dataSnapshot : snapshot.child(today).getChildren()){
//                        total_time[0] += (int) dataSnapshot.getValue();
                        int i = Integer.parseInt(dataSnapshot.getValue().toString());
                        total_time[0] += i;
                    }

                    //2 * 60 * 60 * 1000
                    if (total_time[0] >= 2 * 60 * 60 * 1000) {
                        pass = true;
                    }
                    else {
                        pass = false;
                    }

                    dayCounter(pass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void dayCounter(boolean pass) {
        int day_count = loadMotDayCounter();

        if (pass) {
            day_count += 1;
            saveMotDayCounter(day_count);

        }
    }

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

//    private void newDay() {
//        //load database
//        reff = FirebaseDatabase.getInstance().getReference().child("users")
//                .child(phone);
////        reff.addValueEventListener(new ValueEventListener() {
////
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                if (snapshot.child("Motor Imagery").exists()) {
////                    if (snapshot.child("Motor Imagery").child(today).exists()) {
////                        new_day = false;
////                    } else {
////                        new_day = true;
////                    }
////                }
////                new_day = true;
////            }
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {}
////        });
//    }


    //database management

    public void saveProgress(View view) {
        saveData();
    }

    private void saveData() {

        String chronoText = chronometer.getText().toString();
        long chrono_val = parseChrono(chronoText);

        //save data to shared_preference
//        long chrono_val = parseChrono(chronoText);
//        storeMotTime(chrono_val);

        //save data to firebase
        //load user root and database
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone)
                .child("Motor Imagery").child(today);

        //get current time
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' h:mm a");
        String date = dateFormat.format(calendar1.getTime());

        //store val in database
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(date, chrono_val);

        reff.updateChildren(userUpdates);

        Toast.makeText(getApplicationContext(), "Your data is saved.",
                Toast.LENGTH_SHORT).show();
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
//
//            if (new_day) {chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);}
//            else {chronometer.setBase(chrono_base - pauseOffset);}

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

//    private void storeMotTime(long chrono_val) {
//        SharedPreferences sharedPreferences = getSharedPreferences("ChronoBase", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putLong("chrono_base", chrono_val);
//        editor.apply();
//    }
//
//    private long loadMotTime() {
//        SharedPreferences sharedPreferences = getSharedPreferences("ChronoBase", MODE_PRIVATE);
//        long base = sharedPreferences.getInt("chrono_base", 0);
//        return base;
//    }

    //tab bar control
    public void toProfile(View view) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
    }

    public void toProgress(View view) {
        startActivity(new Intent(getApplicationContext(),ProgressActivity.class));
    }

    public void toSettings(View view) {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }

    public void toHome(View view) {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

}
