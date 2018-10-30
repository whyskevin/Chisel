package com.example.creatingahabit;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
//MaterialCalendarView imports
import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;


import java.util.Calendar;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


//Puts data into activity_description.xml
public class DescriptionActivity extends AppCompatActivity {


    DatabaseHelper myDB;
    TextView freq;
    TextView desc;
    String name;
    Dialog dialog;
    MaterialCalendarView calendar;
    private ArrayList<CalendarDay> markedDates;

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
        calendar = (MaterialCalendarView)findViewById(R.id.calendarView);
        markedDates = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            name = extras.getString("name");
            setTitle(name);
            showInfo(name);
        }
        dialog = new Dialog(this);

        markDateTest();
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
        extra.putString("habit_name",name);
        editIntent.putExtras(extra);
        myDB.close();
        //This starts the new edit activity
        startActivity(editIntent);
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
        if(myDB.deleteFromHT(String.valueOf(myDB.returnIDFromHT(name)))) {
            Intent intent = new Intent(this, MainActivity.class);
            myDB.close();
            startActivity(intent);
            Toast.makeText(DescriptionActivity.this, "Habit deleted", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(DescriptionActivity.this, "Habit NOT deleted", Toast.LENGTH_LONG).show();
        }
    }

    public void markDateTest(){

//            calendar.set(2018, 10  , 25);
            CalendarDay day = CalendarDay.from(2018,10,30);
            markedDates.add(day);
            int myColor = 0xff0000ff;

        calendar.addDecorator(new EventDecorator(myColor, markedDates));

    }
    private class EventDecorator implements DayViewDecorator {

        private final int color;
//        private CalendarDay d;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, ArrayList<CalendarDay> date) {
            this.color = color;
            this.dates = new HashSet<CalendarDay>(date);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
//            return true;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }

}
