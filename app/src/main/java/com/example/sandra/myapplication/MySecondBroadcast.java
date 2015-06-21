package com.example.sandra.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class MySecondBroadcast extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //This new broadcast creates the main notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notify = new Intent(context, Result.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, notify, 0);
        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle("Cycle")
                .setDefaults(1)
                .setContentText("Next cycle begins today")
                .setContentIntent(pIntent)
                .setSmallIcon(com.example.sandra.myapplication.R.drawable.menstrual).build();

        notificationManager.notify(2, notification);

        SharedPreferences pref = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("day", pref.getInt("day", 0) + pref.getInt("regularDayCycle", 0)); //Increments the calendar entry. "day" is incremented by the regular cycle value
        edit.commit();

        //Then creates a new alarm for the next calendar entry(loop)
        Calendar calendar = Calendar.getInstance();
        if (pref.getBoolean("regular", false))      //If the user is regular
        {
            //Holds the first date notification (Day before Main day)
            calendar.set(Calendar.DAY_OF_MONTH, pref.getInt("day", 0) + pref.getInt("regularDayCycle", 0) - 1); //Creates a calendar for the next iteration secondary date
            calendar.set(Calendar.MONTH, pref.getInt("month", 0));
            calendar.set(Calendar.YEAR, pref.getInt("year", 0));
            calendar.set(Calendar.HOUR_OF_DAY, 7);     //Should be 7
            calendar.set(Calendar.MINUTE, 0);          //Should be 0
            calendar.set(Calendar.SECOND, 0);

            Intent loop = new Intent(context, MyBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, loop, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            edit.putLong("calendar", calendar.getTimeInMillis());
            edit.commit();
        }
    }
}
