package com.example.creatingahabit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    Button button;
    ListView userList;
    ArrayList<String> listItem;
    ArrayAdapter adapter;
    ArrayList<ArrayList<CalendarDay>> completedLists = new ArrayList<>(),
                                      notCompletedLists = new ArrayList<>();

    DescriptionActivity sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);

        myDB = new DatabaseHelper(this);
        listItem = new ArrayList<>();
        userList = findViewById(R.id.user_list);
        viewList();

//        Calendar calendar = Calendar.getInstance();
//        int day = calendar.get(Calendar.DAY_OF_WEEK);
//        MaterialCalendarView materialCalendarView = findViewById(R.id.calendarView);
//        materialCalendarView.setTopbarVisible(false);
//        materialCalendarView.state().edit()
//                .setFirstDayOfWeek(DayOfWeek.of(day))
//                .commit();

        //button = (Button) findViewById(R.id.displayHabits);

        //viewAll();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent add = new Intent(this, AddActivity.class);
        startActivity(add);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void viewList() {
        Cursor cursor = myDB.readAllDataHT();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }
        else {
            while(cursor.moveToNext()) {
                listItem.add(cursor.getString(1));  //col 1 is name, col 0 is id
            }
            adapter = new myListAdapter(this, R.layout.list_item_mcv, listItem);
            userList.setAdapter(adapter);
            //making view list buttons to take to different page
            userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CalendarInfo calendarInfo = new CalendarInfo((String) adapter.getItem(position), completedLists.get(position), notCompletedLists.get(position));
                    Intent appInfo = new Intent(MainActivity.this, DescriptionActivity.class);
                    appInfo.putExtra("calendarInfo", calendarInfo);
                    startActivity(appInfo);
                }
            });
        }
    }

    private class myListAdapter extends ArrayAdapter<String> {
        private int layout;

        public myListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if (convertView == null) {

                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_habit_name);
                viewHolder.calendar = convertView.findViewById(R.id.calendarView);
                viewHolder.title.setText(getItem(position));
                Calendar calendar = Calendar.getInstance();
                int today = calendar.get(Calendar.DAY_OF_WEEK);
                viewHolder.calendar.state().edit()
                        .setFirstDayOfWeek(DayOfWeek.of(today))
                        .commit();
                viewHolder.calendar.setTopbarVisible(false);

                final ArrayList<CalendarDay> completed = new ArrayList<>(), notCompleted = new ArrayList<>();
                completedLists.add(completed);
                notCompletedLists.add(notCompleted);
                try {
                    populateCalendar(getItem(position), viewHolder.calendar, completed, notCompleted);
                }
                catch (ParseException e) {

                }
                viewHolder.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                        try {
                            populateCalendar(getItem(position), viewHolder.calendar, completed, notCompleted, calendarDay);
                        }
                        catch (ParseException e) {

                        }
                    }
                });
                //
                convertView.setTag(viewHolder);
            } else {

                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.title.setText(getItem(position));
            }
            return convertView;
        }


    }
    public class ViewHolder {
        TextView title;
        MaterialCalendarView calendar;
    }

    public void populateCalendar(String habitName, MaterialCalendarView materialCalendarView,
                                 ArrayList<CalendarDay> completed, ArrayList<CalendarDay> notCompleted, CalendarDay calendarDay) throws ParseException {
        if(completed.contains(calendarDay)) {
            completed.remove((calendarDay));
            notCompleted.add(calendarDay);
            myDB.updateCompletion(habitName, calendarDay.getDate().toString(), false);
        }
        else {
            boolean notCompleteHas = false;
            if(notCompleted.contains(calendarDay)) {
                notCompleteHas = true;
            }
            notCompleted.remove(calendarDay);
            completed.add((calendarDay));
            System.out.println("ASDASDASD " + calendarDay.getDate().toString());
            if(!notCompleteHas) {
                myDB.insertDataToHR("Table_" + myDB.returnIDFromHT(habitName), calendarDay.getDate().toString(), "1", "Complete");

            }
            else {
                myDB.updateCompletion(habitName, calendarDay.getDate().toString(), true);
            }
        }
        materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));
        materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    public void populateCalendar(String habitName, MaterialCalendarView materialCalendarView,
                                 ArrayList<CalendarDay> completed, ArrayList<CalendarDay> notCompleted) throws ParseException {
        Cursor cursor = myDB.getAllDataHR(habitName);
        int habitID = myDB.returnIDFromHT(habitName);
        if(cursor.getCount() == 0) {
            //Toast.makeText(this, "No data in Table_" + habitID, Toast.LENGTH_SHORT).show();
        }
        else {
            while(cursor.moveToNext()) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(1));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                CalendarDay selectedDay = CalendarDay.from(year,month,day);
                System.out.println(habitName + ": + " + month + "/" + day + "/" + year);

                //Incomplete habit
                if(Integer.valueOf(cursor.getString(2)) == 0){
                    Log.d("I", "Extracted habit " + habitName + " for " + selectedDay.toString() + " is complete") ;
                    if(!notCompleted.contains(selectedDay) && !completed.contains(selectedDay)) {
                        notCompleted.add(selectedDay);
                    }
                }else if (Integer.valueOf(cursor.getString(2)) > 0){ //The habit has been complete that day
                    Log.d("I", "Extracted habit " + habitName + " for " + selectedDay.toString() + " is complete") ;
                    if(!completed.contains(selectedDay) && !notCompleted.contains(selectedDay)) {
                        completed.add(selectedDay);
                    }
                }
            }
            materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));
            materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
            cursor.close();
        }
    }

    public void viewAll() {
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Cursor res = myDB.readAllDataHT();
                if(res.getCount() == 0){ //A table with empty rows will not have any data
                    showMessage("Error", "Nothing found");
                }
                else {
                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()){ //Grabs records from the table
                        buffer.append("NAME : " + res.getString(0) + "\n");
                        buffer.append("DESCRIPTION : " + res.getString(1) + "\n");
                        buffer.append("FREQUENCY : " + res.getString(2)+ "\n\n");
                    }

                    showMessage("Data", buffer.toString());
                }
            }
        });
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
