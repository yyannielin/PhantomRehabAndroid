package com.example.phantomrehab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BeginnerMain extends AppCompatActivity {

    //database control
    DatabaseReference reff;

    //stopwatch control

    private Chronometer chronometer;
    private boolean running;
    private long pauseOffset;
    ImageView pause, stop, hide, show;
    TextView start, show_time;


    //display images

    //Random r;
    ImageView imageView;
    TextView left, right, show_score;

    int score = 0;

    Integer[] imageSet1 = {
            //R.drawable.back1,
            R.drawable.back2,
            R.drawable.back4,
            R.drawable.back5,
            R.drawable.back6,
            R.drawable.back7,
            R.drawable.back8,
            R.drawable.back9,
            R.drawable.back10,
            R.drawable.elbow1,
            R.drawable.elbow2,
            R.drawable.elbow3,
            R.drawable.foot1,
            R.drawable.foot2,
            R.drawable.foot3,
            R.drawable.foot4,

            //placeholder
            R.drawable.foot4,
    };

    char[] answerSet1 = {
            'R', 'R', 'R', 'R', 'R',
            'R', 'R', 'L', 'R', 'L',
            'L', 'R', 'R', 'R', 'R', 'R'
    };

    Integer[] imageSet2 = {
            R.drawable.foot5,
            R.drawable.hand1,
            R.drawable.hand2,
            R.drawable.hand3,
            R.drawable.hand4,
            R.drawable.hand5,
            R.drawable.hand6,
            R.drawable.hand7,
            R.drawable.hand8,
            R.drawable.hand9,
            R.drawable.hand10,
            R.drawable.hand11,
            R.drawable.hand12,
            R.drawable.hand13,
            R.drawable.knee1,
            R.drawable.neck1,
    };

    Integer[] imageSet3 = {
            R.drawable.neck2,
            R.drawable.neck3,
            R.drawable.neck4,
            R.drawable.neck5,
            R.drawable.neck6,
            R.drawable.neck7,
            R.drawable.shoulder1,
            R.drawable.shoulder2,
            R.drawable.shoulder3,
            R.drawable.shoulder4,
            R.drawable.shoulder5,
            R.drawable.shoulder6,
            R.drawable.shoulder7,
            R.drawable.shoulder8,
            R.drawable.shoulder9
    };

    int pickedImage = 0, lastPicked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beginner);

        //stopwatch control
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
                firstImage();
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


    //master control
    private void firstImage() {
        if (pickedImage == 0) {
            startStopwatch();
        }
    }

    private void done() {

        //show score and time
        String score_formatted = getString(R.string.score, score);
        show_score.setText(score_formatted);

        String chronoText = chronometer.getText().toString();
        String time_formatted = getString(R.string.time, chronoText);
        show_time.setText(time_formatted);
        //parseChrono(chronoText);

        //save score to database
        saveData(chronoText);

        //stop stopwatch
        stopStopwatch();

        //transition to finish page
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent start = new Intent(getApplicationContext(), BeginnerFinish.class);
                startActivity(start);
            }
        }, 3000);
    }


    //database management
    private void saveData(String chronoData) {

        //load user root and database
        String phone = loadRoot();
        reff = FirebaseDatabase.getInstance().getReference().child("users").child(phone);

        //get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' h:mm a");
        String date = dateFormat.format(calendar.getTime());

        //store val in database
        reff = reff.child("Laterailty Training");
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(date, new LateralityHelper(chronoData,score));

        reff.updateChildren(userUpdates);
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
//            pause.setVisibility(View.VISIBLE);
//            stop.setVisibility(View.VISIBLE);

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

    private void parseChrono(String chronoText) {
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
    }


    //display images
    public void nextImageFromLeft() {

        //caculate current score
        countLeft();

        //update image from image-set
        imageView.setImageResource(imageSet1[pickedImage]);

        //image-set finished
        if (pickedImage == imageSet1.length-1) {
            done();
        }

        //advance to the next image
        pickedImage++;
    }

    public void nextImageFromRight() {

        //caculate current score
        countRight();

        //update image from image-set
        imageView.setImageResource(imageSet1[pickedImage]);

        //image-set finished
        if (pickedImage == imageSet1.length-1) {
            done();
        }

        //advance to the next image
        pickedImage++;
    }

    private void countLeft() {
        if (answerSet1[pickedImage] == 'L') score++;
    }

    private void countRight() {
        if (answerSet1[pickedImage] == 'R') score++;
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