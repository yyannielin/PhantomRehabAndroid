package com.example.phantomrehab;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phantomrehab.databinding.ActivityMainBinding;
import com.example.phantomrehab.databinding.ActivityReminderBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReminderActivity extends AppCompatActivity {

    //Initialize variable
    TextView tvTimer, tvTimer2, tvTimer3, confirm1, confirm2, confirm3;
    int tHour, tHour2, tHour3, tMinute, tMinute2, tMinute3;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //color management
        TextView navbar = findViewById(R.id.navbar);

        confirm1 = findViewById(R.id.set_alarm);
        confirm2 = findViewById(R.id.motor_set_alarm);
        confirm3 = findViewById(R.id.mirror_set_alarm);

        if (getColor() != getResources().getColor(R.color.blue_theme)){
            navbar.setBackgroundColor(getColor());
            confirm1.setBackgroundColor(getColor());
            confirm2.setBackgroundColor(getColor());
            confirm3.setBackgroundColor(getColor());
        }

        //Notification manager
        tvTimer = findViewById(R.id.select_time);
        tvTimer2 = findViewById(R.id.motor_select_time);
        tvTimer3 = findViewById(R.id.mirror_select_time);

//        tvTimer.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                selectTime();
//            }
//        });

        createNotificationChannel();


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

    public void selectTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                ReminderActivity.this,
                android.R.style.Theme_Holo_Light_Dialog,
//                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Initialize hour and minute
                        tHour = hourOfDay;
                        tMinute = minute;
                        //Store hour and minute in string
                        String time = tHour + ":" + tMinute;
                        //Initializae 24 hours time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat(
                                "HH:mm"
                        );
                        try{
                            Date date = f24Hours.parse(time);
                            //Initialize 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat(
                                    "hh: mm aa"
                            );
                            //Set selected time on text view
                            tvTimer.setText(f12Hours.format(date));
                        } catch (ParseException e){
                            e.printStackTrace();
                        }
                    }
                },12,0,false
        );
        //Set transparent background
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Displayed previous selected time
        timePickerDialog.updateTime(tHour, tMinute);
        //Show dialog
        timePickerDialog.show();
    }

    public void setAlarm(View view) {
        Date date = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_now.setTime(date);
        cal_alarm.setTime(date);

        cal_alarm.set(Calendar.HOUR_OF_DAY, tHour);
        cal_alarm.set(Calendar.MINUTE,tMinute);
        cal_alarm.set(Calendar.SECOND,0);

        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(), pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),  24*60*60*1000 , pendingIntent); // this is inexact
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Reminder set successfully", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(View view) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "PhantomRehabChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("phantomrehab",name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void motSelectTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                ReminderActivity.this,
                android.R.style.Theme_Holo_Light_Dialog,
//                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Initialize hour and minute
                        tHour2 = hourOfDay;
                        tMinute2 = minute;
                        //Store hour and minute in string
                        String time = tHour2 + ":" + tMinute2;
                        //Initializae 24 hours time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat(
                                "HH:mm"
                        );
                        try{
                            Date date = f24Hours.parse(time);
                            //Initialize 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat(
                                    "hh: mm aa"
                            );
                            //Set selected time on text view
                            tvTimer2.setText(f12Hours.format(date));
                        } catch (ParseException e){
                            e.printStackTrace();
                        }
                    }
                },12,0,false
        );
        //Set transparent background
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Displayed previous selected time
        timePickerDialog.updateTime(tHour2, tMinute2);
        //Show dialog
        timePickerDialog.show();
    }

    public void motSetAlarm(View view) {
        Date date = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_now.setTime(date);
        cal_alarm.setTime(date);

        cal_alarm.set(Calendar.HOUR_OF_DAY, tHour);
        cal_alarm.set(Calendar.MINUTE,tMinute);
        cal_alarm.set(Calendar.SECOND,0);

        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(), pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),  24*60*60*1000 , pendingIntent); // this is inexact
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Reminder set successfully", Toast.LENGTH_SHORT).show();
    }

    public void motCancelAlarm(View view) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();
    }

    public void mirSelectTime(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                ReminderActivity.this,
                android.R.style.Theme_Holo_Light_Dialog,
//                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Initialize hour and minute
                        tHour3 = hourOfDay;
                        tMinute3 = minute;
                        //Store hour and minute in string
                        String time = tHour3 + ":" + tMinute3;
                        //Initializae 24 hours time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat(
                                "HH:mm"
                        );
                        try{
                            Date date = f24Hours.parse(time);
                            //Initialize 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat(
                                    "hh: mm aa"
                            );
                            //Set selected time on text view
                            tvTimer3.setText(f12Hours.format(date));
                        } catch (ParseException e){
                            e.printStackTrace();
                        }
                    }
                },12,0,false
        );
        //Set transparent background
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Displayed previous selected time
        timePickerDialog.updateTime(tHour3, tMinute3);
        //Show dialog
        timePickerDialog.show();
    }

    public void mirSetAlarm(View view) {
        Date date = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_now.setTime(date);
        cal_alarm.setTime(date);

        cal_alarm.set(Calendar.HOUR_OF_DAY, tHour);
        cal_alarm.set(Calendar.MINUTE,tMinute);
        cal_alarm.set(Calendar.SECOND,0);

        if(cal_alarm.before(cal_now)){
            cal_alarm.add(Calendar.DATE,1);
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(), pendingIntent);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),  24*60*60*1000 , pendingIntent); // this is inexact
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Reminder set successfully", Toast.LENGTH_SHORT).show();
    }

    public void mirCancelAlarm(View view) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,2,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();
    }


    //color management
    private int getColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("Color", MODE_PRIVATE);
        int selectedColor = sharedPreferences.getInt("color", getResources().getColor(R.color.blue_theme));
        return selectedColor;
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



//        //manage music
//        ImageView PlayIcon, MuteIcon;
//        MuteIcon = findViewById(R.id.mute);
//        PlayIcon = findViewById(R.id.volume);
//
//        MuteIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopService(new Intent(getApplicationContext(), MusicService.class));
//
//                //update UI
//                PlayIcon.setVisibility(View.VISIBLE);
//                MuteIcon.setVisibility(View.GONE);
//            }
//        });
//
//        PlayIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startService(new Intent(getApplicationContext(), MusicService.class));
//
//                //update UI
//                MuteIcon.setVisibility(View.VISIBLE);
//                PlayIcon.setVisibility(View.INVISIBLE);
//            }
//        });


