package com.example.creatingahabit;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static android.Manifest.permission_group.CALENDAR;
import static android.provider.AlarmClock.EXTRA_MESSAGE;


//Puts data into activity_description.xml
public class DescriptionActivity extends AppCompatActivity {


    DatabaseHelper myDB;
    TextView freq;
    TextView desc;
    String habitName;
    Dialog dialog;
    ArrayList<CalendarDay> completed;
    ArrayList<CalendarDay> notCompleted;
    MaterialCalendarView materialCalendarView;
    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        //Toolbar creation
        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        myDB = new DatabaseHelper(this);
        freq = findViewById(R.id.text_frequency);
        desc = findViewById(R.id.text_description);
        materialCalendarView = findViewById(R.id.calendarView);
        lineChart = findViewById(R.id.linechart);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            CalendarInfo calendarInfo = getIntent().getParcelableExtra("calendarInfo");
            habitName = calendarInfo.getHabitName();
            setTitle(habitName);
            showInfo(habitName);
            completed = calendarInfo.getCompleted();
            notCompleted = calendarInfo.getNotCompleted();
            populateCalendar();
            materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
            materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));

        }
        dialog = new Dialog(this);

        materialCalendarView.setOnDateLongClickListener(new OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay) {
                Toast.makeText(DescriptionActivity.this, "" + calendarDay, Toast.LENGTH_SHORT).show();
                if(completed.contains(calendarDay)) {
                    completed.remove(calendarDay);
                    notCompleted.add(calendarDay);
                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));
                }
                else {
                    completed.add(calendarDay);
                    notCompleted.remove(calendarDay);
                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
                }
            }
        });
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                Toast.makeText(DescriptionActivity.this, "" + calendarDay, Toast.LENGTH_SHORT).show();
                if(completed.contains(calendarDay)) {
                    completed.remove(calendarDay);
                    notCompleted.add(calendarDay);
                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));
                }
                else {
                    completed.add(calendarDay);
                    notCompleted.remove(calendarDay);
                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent add = new Intent(this, MainActivity.class);
        myDB.close();
        startActivity(add);
        return super.onOptionsItemSelected(item);
    }

    public void showInfo(String name) {
        int ID = myDB.returnIDFromHT(name);
        Log.d("E", "ID is:" + ID);
        Cursor cursor = myDB.getRecordFromHT(String.valueOf(ID));
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }
        else {
            cursor.moveToNext();
            freq.setText(cursor.getString(3) + getString(R.string.divide) + cursor.getString(4));
            desc.setText(cursor.getString(2));
        }
    }


    // Purpose: Method that starts the  EditHabitActivity when the "Edit" button is clicked
    public void clickEdit(View view){
        Intent editIntent = new Intent(this, EditHabitActivity.class);
        Bundle extra = new Bundle();
        //Passes the habit name into the Intent extra
        extra.putString("habit_name",habitName);
        editIntent.putExtras(extra);
        myDB.close();
        //This starts the new edit activity
        startActivity(editIntent);
    }

    public void populateCalendar(){
//        Cursor cs = myDB.getReadableDatabase();

    }

    public void clickDelete(View view) {
        TextView txtclose;
        Button yes, no;
        dialog.setContentView(R.layout.confirmation_popup_window);
        txtclose = (TextView) dialog.findViewById(R.id.close_window);
        yes = dialog.findViewById(R.id.btn_yes);
        no = dialog.findViewById(R.id.btn_no);
        boolean delete = false;
        //close the window using the x button on top right
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //yes button
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                myDB.dropTable("Table_20");
                delete();
            }
        });
        //no button
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void delete() {
        int habitID = myDB.returnIDFromHT(habitName);
//        myDB.dropTable("Table_"+habitID);
//        if(myDB.deleteFromHT(String.valueOf(habitID))) {
//            Log.d("E", "was deleted");
//        }else
//            Log.d("E", "wasn't deleted");
        if(myDB.deleteFromHT(String.valueOf(habitID))) {
            Intent intent = new Intent(this, MainActivity.class);
            myDB.close();
            startActivity(intent);
            Toast.makeText(DescriptionActivity.this, "Habit deleted", Toast.LENGTH_LONG).show();
        }
    }
}