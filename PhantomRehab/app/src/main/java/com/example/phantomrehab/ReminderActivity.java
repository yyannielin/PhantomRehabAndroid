package com.example.phantomrehab;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
    TextView tvTimer;
    int tHour, tMinute;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //Notification manager
        tvTimer = findViewById(R.id.select_time);
        tvTimer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        createNotificationChannel();

    }

    public void selectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                ReminderActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,0,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Reminder set successfully", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(View view) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this,0,intent,0);

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



//                //Initialize TimePickerDialog
//                TimePickerDialog picker = new TimePickerDialog(
//                        NewReminderActivity.this,
//                        new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                //Initialize hour and minute
//                                t1Hour = hourOfDay;
//                                t1Minute = minute;
//                                //Initialize calendar
//                                Calendar calendar = Calendar.getInstance();
//                                //Set hour and minute
//                                calendar.set(0,0,0,t1Hour,t1Minute);
//                                //Set selected time on textview
//                                tvTimer.setText(
//                                        DateFormat.SHORT);
////                                DateFormat.format("hh:mm aa",calendar));
//                            }
//                        },12,0,false
//                );
//                //Displayed previous selected time
//                picker.updateTime(t1Hour,t1Minute);
//                //show dialog
//                picker.show();