package com.example.sandra.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
//import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.util.Date;


public class Result extends ActionBarActivity{

    int i = 0;
    SharedPreferences pref;
    TextView names;
    Intent intent;
    SharedPreferences.Editor edit;
    AlertDialog.Builder alertDialog1;
    Date menstruationStartDate;
    int day, month, year;
    //CalendarPickerView calendar;
    DatePicker date, textDate;
    TextView nextCycleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sandra.myapplication.R.layout.activity_result);

        pref = getSharedPreferences("AccountDetails", Context.MODE_PRIVATE);
        edit = pref.edit();
        menstruationStartDate =  new Date(pref.getString("MenstruationStartDate", ""));

        day = pref.getInt("day", 0);
        month = pref.getInt("month", 0);
        year = pref.getInt("year", 0);
        //Toast.makeText(this, String.valueOf(pref.getInt("day",0)), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), pref.getString("MenstruationStartDate", ""), Toast.LENGTH_SHORT).show();

        names = (TextView) findViewById(com.example.sandra.myapplication.R.id.names);
        nextCycleText = (TextView) findViewById(com.example.sandra.myapplication.R.id.nextCycleText);
        date = (DatePicker) findViewById(com.example.sandra.myapplication.R.id.datePicker);

        if (pref.getBoolean("increment", false))
        {
            edit.putBoolean("increment", false);
            edit.commit();

            if (pref.getBoolean("regular", false))
                day = pref.getInt("day", 0) + pref.getInt("regularDayCycle", 0);
            else if (!pref.getBoolean("regular", true))
            {

            }
        }
        if (!pref.getBoolean("regular", true)) {
            textDate = (DatePicker) findViewById(com.example.sandra.myapplication.R.id.textDate);
            textDate.updateDate(year, month, day + pref.getInt("irregularDayCycleLeast", 0));
            nextCycleText.setText("The next cycle will start between " + DateFormat.getDateInstance().format(textDate.getCalendarView().getDate()));
            textDate.updateDate(year, month, day + pref.getInt("irregularDayCycleMost", 0));
            nextCycleText.setText(nextCycleText.getText() + " and " + DateFormat.getDateInstance().format(textDate.getCalendarView().getDate()));
        }

        names.setText("Hello " + pref.getString("username", "Error"));

        alertDialog1 = new AlertDialog.Builder(Result.this);
        alertDialog1.setTitle("Are you sure?");
        alertDialog1.setCancelable(false);
        alertDialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                edit.clear();
                edit.commit();
                Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog1.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        dateHelper.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"Delete Account");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0)
            alertDialog1.show();

        return super.onOptionsItemSelected(item);
    }

    Thread dateHelper = new Thread()
    {
        @Override
        public void run()
        {
            while (true)
            {
                if (pref.getBoolean("regular", false)) {
                    try {
                        sleep(100);
                        dateHandlerRegular.sendMessage(dateHandlerRegular.obtainMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                else if (!pref.getBoolean("regular", true))
                {
                    try
                    {
                        dateHandlerIrregular.sendMessage((dateHandlerIrregular.obtainMessage()));
                        sleep(1000);
                        i++;
                        if (i == 2)
                            i = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    Handler dateHandlerRegular = new Handler()
    {
        public void handleMessage(Message msg)
        {
            date.updateDate(year, month, day + pref.getInt("regularDayCycle", 0));
            nextCycleText.setText("The next cycle starts on " + DateFormat.getDateInstance().format(date.getCalendarView().getDate()));
        }
    };

    Handler dateHandlerIrregular = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (i == 0)
                date.updateDate(year, month, day + pref.getInt("irregularDayCycleLeast", 0));

            else if (i == 1)
                date.updateDate(year, month, day + pref.getInt("irregularDayCycleMost", 0));
        }
    };
}