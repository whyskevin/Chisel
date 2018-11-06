package com.example.creatingahabit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Parcel;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Collection;
import java.util.ArrayList;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.MaterialCalendarViewInitProvider;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    Button button;
    ListView userList;
    ArrayList<String> listItem;
    ArrayAdapter adapter;
    TreeMap<Integer, ArrayList> completeCalendarDates;
    TreeMap<Integer,ArrayList> incompleteCalendarDates;
    Hashtable<Integer, MaterialCalendarView> allCalendars;
    //    Collection<CalendarDay> calendarDates;

    DescriptionActivity sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);

        myDB = new DatabaseHelper(this);
//        calendarDates = new HashSet<>();
        if(listItem == null)
            listItem = new ArrayList<>();
        if(completeCalendarDates == null)
            completeCalendarDates = new TreeMap<>();
        if(incompleteCalendarDates == null)
            incompleteCalendarDates = new TreeMap<>();
        if(allCalendars == null)
            allCalendars = new Hashtable<>();
        userList = findViewById(R.id.user_list);

        viewList();

        adapter = new myListAdapter( this, R.layout.list_item, listItem);
        userList.setAdapter(adapter);


        Cursor c = myDB.getAllDataHT();
        if(c.getCount() > 0) {
            while (c.moveToNext()) {
                String habitName = c.getString(1);
                Log.d("I", "populating " + habitName);
                try {
                    populateCalendar(habitName);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        c.close();


//        Log.d("E", completeCalendarDates.get(0).toString());
//        Log.d("E", incompleteCalendarDates.get(0).toString());



//        try {
//            populateCalendar("Michael");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
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
                int ID = Integer.valueOf(cursor.getString(0));
                String habitName = cursor.getString(1);

                listItem.add(habitName);  //col 1 is name, col 0 is id
                completeCalendarDates.put(ID, new ArrayList()); //TreeMap of (habitIDs, ArrayList)
                incompleteCalendarDates.put(ID, new ArrayList()); //TreeMap of (habitIDs, ArrayList)
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItem);
            userList.setAdapter(adapter);
            //making view list buttons to take to different page
            userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent appInfo = new Intent(MainActivity.this, DescriptionActivity.class);
                    appInfo.putExtra("name", (String) adapter.getItem(position));
                    startActivity(appInfo);
                }
            });



//            for(int i = 0; i < listItem.size(); i++){
//                String name = listItem.get(i);
//                try {
//                    populateCalendar(name);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        //Retrieve each habit's marked dates

        if(!listItem.isEmpty()){
            for(int i = 0; i < listItem.size(); i++){

            }
        }
    }

    private class myListAdapter extends ArrayAdapter<String> {
        private int layout;
        public myListAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.list_item_mcv,parent, false);
            //Holds list_item views
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_habit_name);
            viewHolder.calendar = (MaterialCalendarView) convertView.findViewById(R.id.calendarView);

            int ID = myDB.returnIDFromHT(viewHolder.title.toString());

            // Key-Value (ID, Material Calendar View) stored in Hashtable
            allCalendars.put( ID, viewHolder.calendar);

            viewHolder.calendar.setTopbarVisible(false);

            viewHolder.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay selectedDay, boolean b) {

                    Cursor cursor = null;
                    //For the selected calendar, we can get the selected CalendarDay
                    Toast.makeText(MainActivity.this, "" + selectedDay, Toast.LENGTH_SHORT).show();

                    String habitName = viewHolder.title.getText().toString();
                    int habit_ID = myDB.returnIDFromHT(viewHolder.title.getText().toString());

                    //Retrieves the arraylist assigned to that habit
                    ArrayList<CalendarDay> completed = completeCalendarDates.get(habit_ID);
                    ArrayList<CalendarDay> notCompleted = incompleteCalendarDates.get(habit_ID);

                    cursor = myDB.getRecordFromHR(habitName, selectedDay.getDate().toString());

                    //Test output
                    if(cursor != null) {
                        StringBuffer buffer = new StringBuffer();
                        while (cursor.moveToNext()) { //Grabs records from the table
                            buffer.append(cursor.getString(0));
                            buffer.append(cursor.getString(1));
                            buffer.append(cursor.getString(2));
                        }
                        Log.d("E", "Cursor holds" + buffer.toString());
                    }else{
                        Log.d("I", "Date does not exist in " + habitName);
                    }
                    //Test output


                    //If the day has been complete, it now becomes incomplete
                    if(completed.contains(selectedDay)) {
                        completed.remove(selectedDay);
                        notCompleted.add(selectedDay);
                        materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));

                        //Add selected date to the database. If exists, UPDATE else, INSERT FOR THE FIRST TIME
                        if(cursor.getCount() == 0){
                            myDB.insertDataToHR("Table_" + habit_ID, selectedDay.getDate().toString(), "0", "Incomplete");
                        }else {
                            myDB.updateCompletion(habitName, selectedDay.getDate().toString(), false);
                        }
                        Log.d("I", habitName + " has been set to incomplete");

                    }else{
                        completed.add(selectedDay);
                        notCompleted.remove(selectedDay);
                        materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));

                        //Add selected date to the database. If exists, UPDATE else, INSERT FOR THE FIRST TIME
                        if(cursor.getCount() == 0){
                            myDB.insertDataToHR("Table_" + habit_ID, selectedDay.getDate().toString(), "1", "Complete");
                        }else{
                            myDB.updateCompletion(habitName, selectedDay.getDate().toString(), true);
                        }

                        Log.d("I", habitName + " has been set to complete");
                    }
//                    viewAllHR(habitName);
                    cursor.close();
                }
            });
            viewHolder.title.setText(getItem(position).toString());
            return convertView;
        }
    }

    public class ViewHolder {
        TextView title;
        MaterialCalendarView calendar;
    }

    public void populateCalendar(String habitName) throws ParseException {
    Cursor cursor = myDB.getAllDataHR(habitName);
    int habitID = myDB.returnIDFromHT(habitName);
    ArrayList<CalendarDay> completed = completeCalendarDates.get(habitID);
    ArrayList<CalendarDay> notCompleted = incompleteCalendarDates.get(habitID);
    MaterialCalendarView materialCalendarView = allCalendars.get(habitID);
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data in Table_" + habitID, Toast.LENGTH_SHORT).show();
        }
        else {
            while(cursor.moveToNext()) {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(1));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                CalendarDay selectedDay = CalendarDay.from(year,month,day);

                //Incomplete habit
                if(Integer.valueOf(cursor.getString(2)) == 0){
                    Log.d("I", "Extracted habit " + habitName + " for " + selectedDay.toString() + " is incomplete") ;
                    if(!notCompleted.contains(selectedDay)) {
                        notCompleted.add(selectedDay);
                        completed.remove(selectedDay);
                    }
                }else if (Integer.valueOf(cursor.getString(2)) > 0){ //The habit has been complete that day
                    Log.d("I", "Extracted habit " + habitName + " for " + selectedDay.toString() + " is complete") ;
                    if(!completed.contains(selectedDay)) {
                        completed.add(selectedDay);
                        notCompleted.remove(selectedDay);
                    }
                }
            }
//            materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));
//            materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
            cursor.close();
            }
    }



    public void viewAll() {
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Cursor res = myDB.getAllDataHT();
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

    public void viewAllHR(String habit) {
        Cursor res = myDB.getAllDataHR(habit);
        if (res.getCount() == 0) { //A table with empty rows will not have any data
            showMessage("Error", "Nothing found");
        } else {
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) { //Grabs records from the table
                buffer.append("DATE: " + res.getString(1) + "\n");
                buffer.append("COMPLETE : " + res.getString(2) + "\n");
                buffer.append("NOTE : " + res.getString(3) + "\n\n");
            }
            showMessage( "Table_"+myDB.returnIDFromHT(habit), buffer.toString());
        }
    }
}
