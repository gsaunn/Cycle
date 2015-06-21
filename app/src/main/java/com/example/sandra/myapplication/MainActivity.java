package com.example.sandra.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    DatePicker menstruationStartDate;
    Button proceedButton;
    RadioGroup rGroup;
    TextView regularText, irregularText, irregularTextSeperator;
    EditText regularEditText, irregularEditText, irregularEditTextRange, name;
    InputFilter[] FilterArray = new InputFilter[1];
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("AccountDetails", Context.MODE_PRIVATE);

        if (pref.contains("username"))
        {
            Intent intent = new Intent(this, Result.class);
            startActivity(intent);
            finish();
        }

        setContentView(com.example.sandra.myapplication.R.layout.activity_main);

        menstruationStartDate = (DatePicker) findViewById(com.example.sandra.myapplication.R.id.menstruationStartDate);
        proceedButton = (Button) findViewById(com.example.sandra.myapplication.R.id.proceedButton);
        rGroup = (RadioGroup) findViewById(com.example.sandra.myapplication.R.id.rGroup);
        name = (EditText) findViewById(com.example.sandra.myapplication.R.id.name);
        regularText = (TextView) findViewById(com.example.sandra.myapplication.R.id.regularText);
        irregularText = (TextView) findViewById(com.example.sandra.myapplication.R.id.irregularText);
        regularEditText = (EditText) findViewById(com.example.sandra.myapplication.R.id.regularEditText);
        irregularEditText = (EditText) findViewById(com.example.sandra.myapplication.R.id.irregularEditText);
        irregularTextSeperator = (TextView) findViewById(com.example.sandra.myapplication.R.id.irregularTextSeperator);
        irregularEditTextRange = (EditText) findViewById(com.example.sandra.myapplication.R.id.irregularEditTextRange);

        FilterArray[0] = new InputFilter.LengthFilter(2);
        regularEditText.setFilters(FilterArray);
        irregularEditText.setFilters(FilterArray);
        irregularEditTextRange.setFilters(FilterArray);

        proceedButton.setOnClickListener(this);
        rGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case com.example.sandra.myapplication.R.id.proceedButton:    //When the proceed button is pressed
                //Make sure there is a name to collect
                if (TextUtils.isEmpty(name.getText().toString()))
                {
                    Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();  //If no name, return
                    return;
                }

                else if (TextUtils.isEmpty(regularEditText.getText().toString()) && rGroup.getCheckedRadioButtonId() ==
                        com.example.sandra.myapplication.R.id.regularRadio)
                {
                    Toast.makeText(this, "Please enter the number of days between cycles",
                            Toast.LENGTH_SHORT).show();  //If no name, return
                    return;
                }

                else if (TextUtils.isEmpty(irregularEditText.getText().toString()) || TextUtils.isEmpty(irregularEditTextRange.getText().toString()))
                {
                    if (rGroup.getCheckedRadioButtonId() == com.example.sandra.myapplication.R.id.irregularRadio)
                    {
                        Toast.makeText(this, "Please enter the range of days between cycles", Toast.LENGTH_SHORT).show();  //If no name, return
                        return;
                    }
                }

                if (rGroup.getCheckedRadioButtonId() == com.example.sandra.myapplication.R.id.regularRadio && Integer.parseInt(regularEditText.getText().toString()) == 0)
                {
                    Toast.makeText(this, "That is not possible", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (rGroup.getCheckedRadioButtonId() == com.example.sandra.myapplication.R.id.irregularRadio)
                {
                    if (Integer.parseInt(irregularEditText.getText().toString()) == 0 || Integer.parseInt(irregularEditTextRange.getText().toString()) == 0)
                    {
                        Toast.makeText(this, "That is not possible", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                SharedPreferences.Editor edit = pref.edit();
                edit.putString("username", name.getText().toString());        //Copy the user's name into a file if the editText has content
                if (rGroup.getCheckedRadioButtonId() == com.example.sandra.myapplication.R.id.regularRadio)
                {
                    edit.putBoolean("regular", true);
                    edit.putInt("regularDayCycle", Integer.parseInt(regularEditText.getText().toString()));      //Copy the menstruation start date into a file
                }

                else if (rGroup.getCheckedRadioButtonId() == com.example.sandra.myapplication.R.id.irregularRadio)
                {
                    edit.putBoolean("regular", false);
                    edit.putInt("irregularDayCycleLeast", Integer.parseInt(irregularEditText.getText().toString()));                    //Copy the range of days into a file
                    edit.putInt("irregularDayCycleMost", Integer.parseInt(irregularEditTextRange.getText().toString()));
                }

                String selectedDate = DateFormat.getDateInstance().format(menstruationStartDate.getCalendarView().getDate());
                //Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_SHORT).show();
                edit.putString("MenstruationStartDate", selectedDate);
                edit.putInt("day", menstruationStartDate.getDayOfMonth());
                edit.putInt("month", menstruationStartDate.getMonth());
                edit.putInt("year", menstruationStartDate.getYear());

                edit.commit();

                //Send broadcast (regularDay - 1) by 7AM
                Intent myIntent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

                Calendar calendar = Calendar.getInstance();
                if (pref.getBoolean("regular", false))      //If the user is regular
                {
                    //Holds the first date notification (Day before Main day)
                    calendar.set(Calendar.DAY_OF_MONTH, menstruationStartDate.getDayOfMonth() + Integer.parseInt(regularEditText.getText().toString()) - 1); //Creates a calendar entry for the day before the Main day
                    calendar.set(Calendar.MONTH, menstruationStartDate.getMonth());
                    calendar.set(Calendar.YEAR, menstruationStartDate.getYear());
                    calendar.set(Calendar.HOUR_OF_DAY, 7);     //Should be 7
                    calendar.set(Calendar.MINUTE, 0);          //Should be 0

                    calendar.set(Calendar.SECOND, 0);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    edit.putLong("calendar", calendar.getTimeInMillis());
                    edit.commit();
                }

                else if(!pref.getBoolean("regular", true))  //If the user is irregular
                {

                }


                Intent intent = new Intent(this, Result.class);        //Go to next activity
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == rGroup)
        {
            switch (checkedId)
            {
                case com.example.sandra.myapplication.R.id.regularRadio:
                    regularText.setVisibility(View.VISIBLE);
                    regularEditText.setVisibility(View.VISIBLE);
                    irregularText.setVisibility(View.GONE);
                    irregularEditText.setVisibility(View.GONE);
                    irregularTextSeperator.setVisibility(View.GONE);
                    irregularEditTextRange.setVisibility(View.GONE);
                    break;
                case com.example.sandra.myapplication.R.id.irregularRadio:
                    regularText.setVisibility(View.GONE);
                    regularEditText.setVisibility(View.GONE);
                    irregularText.setVisibility(View.VISIBLE);
                    irregularEditText.setVisibility(View.VISIBLE);
                    irregularTextSeperator.setVisibility(View.VISIBLE);
                    irregularEditTextRange.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}























































