package com.example.sandra.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //This broadcast creates a notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notify = new Intent(context, Result.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, notify, 0);
        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle("Cycle")
                .setDefaults(1)
                .setContentText("Next cycle begins tomorrow")
                .setContentIntent(pIntent)
                .setSmallIcon(com.example.sandra.myapplication.R.drawable.menstrual).build();

        notificationManager.notify(1, notification);

        //Then creates another alarm for the next day
        SharedPreferences pref = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE);
        Intent secondAlarm = new Intent(context, MySecondBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, secondAlarm, 0);

        //The new alarm launches another broadcast
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, pref.getLong("calendar", 0) + 24 * 60 * 60 * 1000, pendingIntent);
    }
}
