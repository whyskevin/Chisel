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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ArrayList;

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

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    Button button;
    ListView userList;
    ArrayList<String> listItem;
    ArrayAdapter adapter;
    TreeMap<Integer, ArrayList> habitsCalendarDates;
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
        listItem = new ArrayList<>();
        habitsCalendarDates = new TreeMap<>();
        userList = findViewById(R.id.user_list);
        viewList();
        adapter = new myListAdapter( this, R.layout.list_item_mcv, listItem);
        userList.setAdapter(adapter);

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
                habitsCalendarDates.put(Integer.valueOf(cursor.getString(0)), new ArrayList()); //TreeMap of (habitIDs, ArrayList)
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
            viewHolder.calendar.setTopbarVisible(false);
            viewHolder.calendar.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay selectedDay, boolean b) {
                    //For the selected calendar, we can get the selected CalendarDay
                    Toast.makeText(MainActivity.this, "" + selectedDay, Toast.LENGTH_SHORT).show();
                    int habit_ID = myDB.returnIDFromHT(viewHolder.title.getText().toString());
                    ArrayList<CalendarDay> clickedCalendarDates = habitsCalendarDates.get(habit_ID);
                    clickedCalendarDates.add(selectedDay);
                    myDB.insertDataToHR("Table_" + habit_ID, selectedDay.getDate().toString(), "1", "");
                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), clickedCalendarDates));
                    //Add selected date to the database

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
}
