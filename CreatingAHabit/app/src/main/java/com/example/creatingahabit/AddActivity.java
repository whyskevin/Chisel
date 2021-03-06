package com.example.creatingahabit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    DatabaseHelper myDB;
    EditText habitName, habitDescription, habitFrequency;
    TextView reminderTime;
    MenuItem createHabit;
    Spinner frequencySpinner;
    String spinnerSelected;
    Switch reminder_switch;
    Calendar setTimeReminder;
    Dialog dialog;
    ArrayList<String> selectedDays;

    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        myDB = new DatabaseHelper(this);
        dialog = new Dialog(this);
        selectedDays = new ArrayList<>();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);

        habitName = (EditText) findViewById(R.id.habitName);
        habitDescription = (EditText) findViewById(R.id.description);
        habitFrequency = (EditText) findViewById(R.id.frequency);
        createHabit = (MenuItem) findViewById(R.id.save);
        frequencySpinner = (Spinner)findViewById(R.id.frequency_spinner);
        spinnerCreation(frequencySpinner);

        notification = new NotificationCompat.Builder(this, "MyChannelId_01");
        notification.setAutoCancel(true);

        reminderTime = findViewById(R.id.time);
        reminder_switch = findViewById(R.id.switch1);
        reminder_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    reminderTime.setVisibility(View.VISIBLE);

                }
                else {
                    reminderTime.setVisibility(View.GONE);
                }
            }
        });

        setTimeReminder = Calendar.getInstance();

    }

    public void notifButtonClicked(View view) {

        //send to home screen when clicked on notif
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Builds notification and issues (sending out to device) it
        //object to send notif
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int NOTIFICATION_ID = 234;
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            nm.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.smiley_face)
                .setContentTitle("Here is the title")
                .setContentText("I am the body text")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        nm.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void spinnerCreation(Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.frequency_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.save:
                String habitNameTrimmed = habitName.getText().toString().trim();
                boolean isInserted = myDB.insertDataToHT(habitNameTrimmed,
                        habitDescription.getText().toString(),
                        habitFrequency.getText().toString(), spinnerSelected);
                if (isInserted) {
                    Toast.makeText(AddActivity.this, myDB.returnIDFromHT(habitNameTrimmed) + ": Data Inserted", Toast.LENGTH_LONG).show();

                    //send data to notification receiver
                    makeAlarm();

                    Intent create = new Intent(this, MainActivity.class);
                    startActivity(create);
                } else {
                    Toast.makeText(AddActivity.this, "Data Not Inserted", Toast.LENGTH_LONG).show();
                }
                break;
        }
        Intent add = new Intent(this, MainActivity.class);
        startActivity(add);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        spinnerSelected = adapterView.getItemAtPosition(position).toString();
        //Toast.makeText(adapterView.getContext(), "Selected spinner frequency: "+ spinnerSelected, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void makeAlarm() {
        //for testing purposes
        Calendar calendar = Calendar.getInstance();
//                    calendar.add(Calendar.SECOND, 5);

        int requestCode = myDB.returnIDFromHT(habitName.getText().toString());
        //set reminder here with alarm service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("id", requestCode);
        intent.putExtra("name", habitName.getText().toString());
        intent.putExtra("description", habitDescription.getText().toString());
        intent.putExtra("frequency", habitFrequency.getText().toString());
        intent.putExtra("timePeriod", spinnerSelected);
        for(int i = 0; i < 7; i++) {
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode + i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if(setTimeReminder.before(calendar)) {
                setTimeReminder.add(Calendar.DATE, 7);
            }
            else {
                setTimeReminder.add(Calendar.DATE, i);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setTimeReminder.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
        }
    }

    public void setTime(View view) {
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setTimeReminder.set(Calendar.HOUR_OF_DAY, hourOfDay);
                setTimeReminder.set(Calendar.MINUTE, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                reminderTime.setText(simpleDateFormat.format(setTimeReminder.getTime()));
            }
        }, 12, 00,false);
        timePickerDialog.show();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void clickReminder(View view) {
        dialog.setContentView(R.layout.reminder_popup_window);
        TextView txtclose;
        Button buttonDaysOfWeek;
        buttonDaysOfWeek = dialog.findViewById(R.id.buttonDaysOfWeek);
        txtclose = dialog.findViewById(R.id.close_window);
        //yes = dialog.findViewById(R.id.btn_yes);
        //no = dialog.findViewById(R.id.btn_no);
        //use setOnItemClickListener
        if(selectedDays.size() == 7)
            buttonDaysOfWeek.setText("Every Day");
        else if(selectedDays.size() > 0)
            buttonDaysOfWeek.setText("Custom");
        else
            buttonDaysOfWeek.setText("No days");

        boolean delete = false;
        //close the window using the x button on top right
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //yes button
//        yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        //no button
//        no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }

    public void clickDaysOfWeek(View view) {
        dialog.setContentView(R.layout.days_of_week_list);
        TextView txtclose = dialog.findViewById(R.id.close_window);
        Button saveButton = dialog.findViewById(R.id.btn_save);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.sunday:
                if (checked && !selectedDays.contains("sunday"))
                    selectedDays.add("sunday");
                else
                    selectedDays.remove("sunday");
                // Remove the meat
                break;
            case R.id.monday:
                if (checked && !selectedDays.contains("monday"))
                    selectedDays.add("monday");
                else
                    selectedDays.remove("monday");
                break;
            case R.id.tuesday:
                if (checked && !selectedDays.contains("tuesday"))
                    selectedDays.add("tuesday");
                else
                    selectedDays.remove("tuesday");
                break;
            case R.id.wednesday:
                if (checked && !selectedDays.contains("wednesday"))
                    selectedDays.add("wednesday");
                else
                    selectedDays.remove("wednesday");
                break;
            case R.id.thursday:
                if (checked && !selectedDays.contains("thursday"))
                    selectedDays.add("thursday");
                else
                    selectedDays.remove("thursday");
                break;
            case R.id.friday:
                if (checked && !selectedDays.contains("friday"))
                    selectedDays.add("friday");
                else
                    selectedDays.remove("friday");
                break;
            case R.id.saturday:
                if (checked && !selectedDays.contains("saturday"))
                    selectedDays.add("saturday");
                else
                    selectedDays.remove("saturday");
                break;
        }
    }
}
