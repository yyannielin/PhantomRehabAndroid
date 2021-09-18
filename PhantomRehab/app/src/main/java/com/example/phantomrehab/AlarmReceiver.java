package com.example.phantomrehab;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, DestinationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

        //Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "phantomrehab")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Phantom Rehab")
                .setContentText("Reminder to start your training :D")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        Notification notification = new Notification.Builder(context)
                .setContentTitle("Phantom Rehab")
                .setContentText("Reminder to start your training :D")
                .setSmallIcon(R.drawable.ic_launcher_background).build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notification.flags|= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(0,notification);
//
//        Uri noti = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//
//        Ringtone r = RingtoneManager.getRingtone(context,noti);
//        r.play();
    }
}
