package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BeginnerMain extends AppCompatActivity {

    //database control
    DatabaseReference reff;
    private String phone;

    //level control
    private int POF;

    //music control
    private ImageView PlayIcon, MuteIcon;

    //stopwatch control
    private boolean Show;
    private RelativeLayout StopwatchBar;

    private Chronometer chronometer;
    private String chrono_text;
    private boolean running;
    private long pauseOffset;
    ImageView pause, stop, hide, show;
    TextView start, show_time;

    //display images

    ImageView imageView;
    TextView left, right, show_score;

    int score = 0;

    Integer[] finalImageSet = new Integer[16];
    Integer[] initialSet = {
            R.drawable.back1, //L
            R.drawable.back2, //L
            R.drawable.back4, //R
            R.drawable.back5, //R
            R.drawable.back6, //R

            R.drawable.back7, //L
            R.drawable.back8, //R
            R.drawable.back9, //L
            R.drawable.back10, //R
            R.drawable.elbow1, //L

            R.drawable.elbow2, //L
            R.drawable.elbow3, //R
            R.drawable.foot1, //R
            R.drawable.foot2, //R
            R.drawable.foot3, //R

            R.drawable.foot4, //R
            R.drawable.foot5, //L
            R.drawable.hand1, //L
            R.drawable.hand2, //R
            R.drawable.hand3, //L

            R.drawable.hand4, //R
            R.drawable.hand5, //L
            R.drawable.hand6, //L
            R.drawable.hand7, //R
            R.drawable.hand8, //L

            R.drawable.hand9, //L
            R.drawable.hand10, //L
            R.drawable.hand11, //L
            R.drawable.hand12, //L
            R.drawable.hand13, //R

            R.drawable.knee1, //R
            R.drawable.neck1, //R
            R.drawable.neck2, //L
            R.drawable.neck3, //R
            R.drawable.neck4, //R

            R.drawable.neck5, //R
            R.drawable.neck6, //R
            R.drawable.neck7, //L
            R.drawable.shoulder1, //L
            R.drawable.shoulder2, //R

            R.drawable.shoulder3, //L
            R.drawable.shoulder4, //L
            R.drawable.shoulder5, //L
            R.drawable.shoulder6, //R
            R.drawable.shoulder7, //L

            R.drawable.shoulder8, //R
            R.drawable.shoulder9, //L
            R.drawable.image_1, //R
            R.drawable.image__2_, //R
            R.drawable.image__3_, //R

            R.drawable.image__4_, //R
            R.drawable.image__5_, //L
            R.drawable.image__6_, //R
            R.drawable.image__7_, //R
            R.drawable.image__8_, //R

            R.drawable.image__9_, //R
            R.drawable.image__10_, //R
            R.drawable.image__11_, //L
            R.drawable.image__12_, //R
            R.drawable.image__13_, //L

            R.drawable.image__14_, //L
            R.drawable.image__15_, //L
            R.drawable.image__16_, //L
            R.drawable.image__17_, //R
            R.drawable.image__18_, //R

            R.drawable.image__19_, //R
            R.drawable.image__20_, //L
            R.drawable.image__21_, //R
            R.drawable.image__22_, //R
            R.drawable.image__23_, //L

            R.drawable.image__24_, //R
            R.drawable.image__25_, //R
            R.drawable.image_26, //R
            R.drawable.image_27, //L
            R.drawable.image_28, //L

            R.drawable.image_29, //L
            R.drawable.image_30, //L
            R.drawable.image_31, //R
            R.drawable.image_32, //L
    };

    char[] answerSet1 = {
            'L', 'L', 'R', 'R', 'R',
            'L', 'R', 'L', 'R', 'L',
            'L', 'R', 'R', 'R', 'R',
            'R', 'L', 'L', 'R', 'L',
            'R', 'L', 'L', 'R', 'L',
            'L', 'L', 'L', 'L', 'R',
            'R', 'R', 'L', 'R', 'L',
            'R', 'R', 'L', 'L', 'R',
            'L', 'L', 'L', 'R', 'L',
            'R', 'L', 'R', 'R', 'R',
            'R', 'L', 'R', 'R', 'R',
            'R', 'R', 'L', 'R', 'L',
            'L', 'L', 'L', 'R', 'R',
            'R', 'L', 'R', 'R', 'L',
            'R', 'R', 'R', 'L', 'L',
            'L', 'L', 'R', 'L'
    };

//    char[] answerSet1 = {
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R', 'R',
//            'R', 'R', 'R', 'R'
//    };

    Map<Integer, Integer> map;

    int pickedImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginner);

        //color management
        if (getColor() != getResources().getColor(R.color.blue_theme)){
            TextView navbar = findViewById(R.id.navbar);
            navbar.setBackgroundColor(getColor());

            chronometer = findViewById(R.id.chronometer);
            chronometer.setTextColor(getColor());

            start = findViewById(R.id.start);
            start.setTextColor(getColor());

            hide = findViewById(R.id.hide);
            hide.setColorFilter(getColor(), PorterDuff.Mode.SRC_IN);

            show = findViewById(R.id.show);
            show.setColorFilter(getColor(), PorterDuff.Mode.SRC_IN);

            TextView left = findViewById(R.id.btn_left);
            TextView right = findViewById(R.id.btn_right);
            ImageView tabbar_icon = findViewById(R.id.therapy);

            if (getColor() == getResources().getColor(R.color.purple_theme)){
                tabbar_icon.setImageResource(R.drawable.therapy_purple);
                left.setBackgroundColor(getResources().getColor(R.color.purple_theme));
                right.setBackgroundColor(getResources().getColor(R.color.purple_tertiary));
            }
            else if (getColor() == getResources().getColor(R.color.teal_theme)){
                tabbar_icon.setImageResource(R.drawable.therapy_teal);
                left.setBackgroundColor(getResources().getColor(R.color.teal_theme));
                right.setBackgroundColor(getResources().getColor(R.color.teal_tertiary));
            }
            else if (getColor() == getResources().getColor(R.color.green_theme)){
                tabbar_icon.setImageResource(R.drawable.therapy_green);
                left.setBackgroundColor(getResources().getColor(R.color.green_theme));
                right.setBackgroundColor(getResources().getColor(R.color.green_tertiary));
            }
        }

        //database
        phone = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);

        //image randomization and answer key

        Random generator = new Random();

        for (int i=0; i<finalImageSet.length-1; i++){
            int randomIndex = generator.nextInt(initialSet.length);
            finalImageSet[i] = initialSet[randomIndex];

            if (i==finalImageSet.length-2){ // last picture in the array is a placeholder
                i++;
                finalImageSet[i] = initialSet[randomIndex];
            }
        }

        map = new HashMap<>();
        for (int i=0; i<answerSet1.length; i++) {
            map.put(initialSet[i], (int) answerSet1[i]);
        }

        //stopwatch control
        Show = getStopwatchVar();
        StopwatchBar = findViewById(R.id.stopwatch);
//        stopwatchUI();

        chronometer = findViewById(R.id.chronometer);
        start = findViewById(R.id.start);

        hide = findViewById(R.id.hide);
        show = findViewById(R.id.show);

        show_time = findViewById(R.id.show_time);
        show_time.setText("");

        //display images
        //        r = new Random();
        imageView = findViewById(R.id.image);
        left = findViewById(R.id.btn_left);
        right = findViewById(R.id.btn_right);

        show_score = findViewById(R.id.show_score);
        show_score.setText("");

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstImage(); //to start stopwatch
                nextImageFromLeft();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstImage();
                nextImageFromRight();
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
    }


    //master control
    private void firstImage() {
        if (pickedImage == 0) {
            startStopwatch();
            if(!Show){
                chronometer.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
                start.setText(R.string.hide_stopwatch);

                hide.setVisibility(View.INVISIBLE);
                show.setVisibility(View.VISIBLE);
            }
        }
    }

    private void done() {

        //display chronometer
        chronometer.setVisibility(View.VISIBLE);
        start.setVisibility(View.INVISIBLE);
        hide.setVisibility(View.VISIBLE);
        show.setVisibility(View.INVISIBLE);

        //show score and time
        String score_formatted = getString(R.string.score, score);
        show_score.setText(score_formatted);

        chrono_text = chronometer.getText().toString();
        String time_formatted = getString(R.string.time, chrono_text);
        show_time.setText(time_formatted);

        //decide if the user passes the training; save score, time, and P/F info to database;
        POF = passOrFail(); //return 1 if pass and 0 if fail
        saveData();

        //stop stopwatch
        stopStopwatch();

        //level control
        dayCounter();
        nextLevel();

        //transition to finish page
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent start = new Intent(getApplicationContext(), BeginnerFinish.class);
                startActivity(start);

//                if (POF == 1){
//                    //to BeginnerPass
//                    Intent start = new Intent(getApplicationContext(), BeginnerPass.class);
//                    startActivity(start);
//                }
//                else {
//                    //to BeginnerFail
//                    Intent start = new Intent(getApplicationContext(), BeginnerFail.class);
//                    startActivity(start);
//                }
            }
        }, 3000);
    }


    //level control

    private void nextLevel() {
        //if day_counter >= 7 -> add child "LatThresh" = 1 to database

        int count = loadDayCounter();

//        Toast.makeText(getApplicationContext(),"day count:" + count,
//                Toast.LENGTH_SHORT).show();

        if (count >= 7){

            //load user root and database
            reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone)
                    .child("Laterality Training");

            Map<String, Object> threshUpdates = new HashMap<>();
            threshUpdates.put("LatThresh", 1);

            reff.updateChildren(threshUpdates);
        }
    }

    private void dayCounter() {
        //implement in done()
        //count how many days in a row has the user passed the training
        //store the value in shared_preference

        final int[] day_counter = new int[1];

        //if fail -> day_counter = 0
        //if pass and training_counter==5  -> day_counter += 1

        if (POF==0) {
            day_counter[0] = 0;
            saveDayCounter(day_counter[0]);

//            Toast.makeText(getApplicationContext(),"day count:" + day_counter[0],
//                    Toast.LENGTH_SHORT).show();

            return;
        }

        //POF == 1
        //initialize day_counter
        day_counter[0] = loadDayCounter();

        //initialize other variables
        final int[] training_counter = new int[1];
        final boolean[] new_day = new boolean[1];
        String today;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = dateFormat.format(calendar.getTime());

        //load database
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);
        reff.child("Laterality Training").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //check if the user has done training today
                //training_counter = how many times has the user done training today
                if (snapshot.child(today).exists()){
                    new_day[0] = false;
                    training_counter[0] = (int) snapshot.child(today).getChildrenCount();

//                    Toast.makeText(getApplicationContext(),"count: " + training_counter[0] + "; POF: " + POF,
//                            Toast.LENGTH_SHORT).show();
                }
                else {
                    new_day[0] = true;
                    training_counter[0] = 1;
                }

                //if pass and training_counter==5  -> day_counter += 1
                if (POF == 1 && training_counter[0] == 5) {
                    day_counter[0] += 1;
                    saveDayCounter(day_counter[0]);

//                    Toast.makeText(getApplicationContext(),"day count:" + day_counter[0],
//                            Toast.LENGTH_SHORT).show();
                }
                else {
//                    Toast.makeText(getApplicationContext(), "incorrect training count",
//                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveDayCounter(int day_counter) {
        SharedPreferences sharedPreferences = getSharedPreferences("DayCounter", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("day_counter",day_counter);
        editor.apply();
    }

    private int loadDayCounter() {
        SharedPreferences sharedPreferences = getSharedPreferences("DayCounter", MODE_PRIVATE);
        int count = sharedPreferences.getInt("day_counter", 0);
        return count;
    }

    private int passOrFail() {
        //returns 1 if pass, 0 if fail

        int count = finalImageSet.length;
        double time_thresh = count*1.8*1000; //unit: millisecond
        double score_thresh = count*0.9;

        boolean pass_time;
        boolean pass_score;

        long time_spent = parseChrono(chrono_text);
        if (time_spent <= time_thresh){
            pass_time = true;
        }
        else{
            pass_time = false;
        }

        if (score >= score_thresh){
            pass_score = true;
        }
        else{
            pass_score = false;
        }

        if (pass_time && pass_score) return 1;
        return 0;
    }


    //database management
    private void saveData() {

        //load user root and database
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);

        //get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' h:mm a");
        String date = dateFormat.format(calendar.getTime());

        SimpleDateFormat dateFormat_wt_time = new SimpleDateFormat("yyyy-MM-dd");
        String date_wt_time = dateFormat_wt_time.format(calendar.getTime());

        //store val in database
//        reff = reff.child("Laterailty Training");
        reff = reff.child("Laterality Training").child(date_wt_time);
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(date, new LateralityHelper(chrono_text,score,POF));

        reff.updateChildren(userUpdates);
    }

    private String loadRoot(){
        SharedPreferences sharedPreferences = getSharedPreferences("Root", MODE_PRIVATE);
        String root = sharedPreferences.getString("root", "");
        return root;
    }


    //stopwatch control
//    private void stopwatchUI() {
//        if (Show) StopwatchBar.setVisibility(View.VISIBLE);
//        else {
//            StopwatchBar.setVisibility(View.VISIBLE);
////            chronometer.setVisibility(View.INVISIBLE);
////            start.setVisibility(View.VISIBLE);
////            start.setText(R.string.hide_stopwatch);
////
////            hide.setVisibility(View.INVISIBLE);
////            show.setVisibility(View.VISIBLE);
//            }
//    }

    private boolean getStopwatchVar(){
        SharedPreferences sharedPreferences = getSharedPreferences("Stopwatch", MODE_PRIVATE);
        boolean show = sharedPreferences.getBoolean("stopwatch", Show);
        return show;
    }

    public void startStopwatch() {
        if (!running){
            //update UI
            chronometer.setVisibility(View.VISIBLE);
            start.setVisibility(View.INVISIBLE);
//            pause.setVisibility(View.VISIBLE);
//            stop.setVisibility(View.VISIBLE);

            Toast.makeText(getApplicationContext(), "stopwatch started", Toast.LENGTH_SHORT).show();

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
//            pause.setVisibility(View.INVISIBLE);
//            stop.setVisibility(View.INVISIBLE);

            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }

    public void stopStopwatch() {
        chronometer.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
        start.setText(R.string.start_stopwatch);
//        pause.setVisibility(View.INVISIBLE);
//        stop.setVisibility(View.INVISIBLE);

        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
        running = false;
    }

    public void hideStopwatch(View view) {
        chronometer.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
        start.setText(R.string.hide_stopwatch);

        hide.setVisibility(View.INVISIBLE);
        show.setVisibility(View.VISIBLE);
    }

    public void showStopwatch(View view) {
        chronometer.setVisibility(View.VISIBLE);
        start.setVisibility(View.INVISIBLE);
//        start.setText(R.string.hide_stopwatch);

        hide.setVisibility(View.VISIBLE);
        show.setVisibility(View.INVISIBLE);
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


    //display images
    public void nextImageFromLeft() {

        //caculate current score
        countLeft();

        //update image from image-set
        imageView.setImageResource(finalImageSet[pickedImage]);

        //image-set finished
        if (pickedImage == finalImageSet.length-1) {
            done();
        }

        //advance to the next image
        pickedImage++;
    }

    public void nextImageFromRight() {

        //caculate current score
        countRight();

        //update image from image-set
        imageView.setImageResource(finalImageSet[pickedImage]);

        //image-set finished
        if (pickedImage == finalImageSet.length-1) {
            done();
        }

        //advance to the next image
        pickedImage++;
    }

    private void countLeft() {
//        if (answerSet1[pickedImage] == 'L') score++;
        if(pickedImage>0){
            if (map.get(finalImageSet[pickedImage-1]) == 'L')
            score++;
        }
    }

    private void countRight() {
//        if (answerSet1[pickedImage] == 'R') score++;
        if (pickedImage == 0) score++;
        if(pickedImage>0){
            if (map.get(finalImageSet[pickedImage-1]) == 'R')
                score++;
        }    }


    //tab bar control
    public void toProfile(View view) {
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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


    //music control
    private void mute() {
        stopService(new Intent(getApplicationContext(), MusicService.class));

        //update UI
        PlayIcon.setVisibility(View.VISIBLE);
        MuteIcon.setVisibility(View.GONE);
    }

    private void play() {
        startService(new Intent(getApplicationContext(), MusicService.class));

        //update UI
        MuteIcon.setVisibility(View.VISIBLE);
        PlayIcon.setVisibility(View.INVISIBLE);
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


    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
    }
}