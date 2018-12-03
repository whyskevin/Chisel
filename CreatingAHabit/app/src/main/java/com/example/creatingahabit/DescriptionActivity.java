package com.example.creatingahabit;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
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
import com.github.mikephil.charting.charts.ScatterChart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;


import org.threeten.bp.DayOfWeek;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;


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
    PieChart graph;
    ScatterChart scatterChart;

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
        scatterChart = findViewById(R.id.scatterchart);

        graph =(PieChart) findViewById(R.id.chart);
        graph.getDescription().setEnabled(false);
        graph.getLegend().setEnabled(false);
        graph.setUsePercentValues(true);

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
                    myDB.updateCompletion(habitName, calendarDay.getDate().toString(), false);
                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));
                }
                else {
                    boolean notCompleteHas = false;
                    if(notCompleted.contains(calendarDay)) {
                        notCompleteHas = true;
                    }
                    notCompleted.remove(calendarDay);
                    completed.add((calendarDay));
                    if(!notCompleteHas) {
                        myDB.insertDataToHR("Table_" + myDB.returnIDFromHT(habitName), calendarDay.getDate().toString(), "1", "Complete");

                    }
                    else {
                        myDB.updateCompletion(habitName, calendarDay.getDate().toString(), true);
                    }
                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
                }
                setGraphData();
            }
        });

//        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
//            @Override
//            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
//                Toast.makeText(DescriptionActivity.this, "" + calendarDay, Toast.LENGTH_SHORT).show();
//                if(completed.contains(calendarDay)) {
//                    completed.remove(calendarDay);
//                    notCompleted.add(calendarDay);
//                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#EF6461"), notCompleted));
//                }
//                else {
//                    completed.add(calendarDay);
//                    notCompleted.remove(calendarDay);
//                    materialCalendarView.addDecorator(new EventDecorator(Color.parseColor("#98EA69"), completed));
//                }
//            }
//        });
          setGraphData();
        chart();

    }

        public void setGraphData(){
        ArrayList<PieEntry> entries1 = new ArrayList<>();
  if(notCompleted.size() == 0 && completed.size() == 0){
            graph.setVisibility(View.GONE);
        }
        else
            graph.setVisibility(View.VISIBLE);

        entries1.add(new PieEntry((float)completed.size(), R.color.colorPrimaryDark));
        entries1.add(new PieEntry((float)notCompleted.size(),  R.color.colorAccent));


        PieDataSet dataSet1 = new PieDataSet(entries1, "result");

        final int[] myColors = {
                Color.rgb(152,234,105),
                Color.rgb(239,100,97),
        };
        ArrayList<Integer> colors = new ArrayList<>();
        for(int c: myColors) colors.add(c);
        dataSet1.setColors(colors);

        PieData data = new PieData(dataSet1);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        graph.setData(data);
        graph.invalidate();
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
        deleteAlarm(habitID);
        if(myDB.deleteFromHT(String.valueOf(habitID))) {
            Intent intent = new Intent(this, MainActivity.class);
            myDB.close();
            startActivity(intent);
            Toast.makeText(DescriptionActivity.this, "Habit deleted", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteAlarm(int habitID) {
        //for testing purposes
        Calendar calendar = Calendar.getInstance();
//                    calendar.add(Calendar.SECOND, 5);

        int requestCode = habitID;
        //set reminder here with alarm service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        for(int i = 0; i < 7; i++) {
            alarmManager.cancel(PendingIntent.getBroadcast(getApplicationContext(), requestCode + i, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    //Plots (10,10) on the Scatter plot in the Description Activity
    public void testMPAndroidChart(){

        ArrayList<Entry> entries = new ArrayList ();
                entries.add(new Entry(0f, 0));
                entries.add(new Entry(1f, 5));
                entries.add(new Entry(2f, 10));
//                entries.add(new Entry(12f, 3));
//                entries.add(new Entry(18f, 4));
//                entries.add(new Entry(9f, 5));

        ScatterDataSet dataset = new ScatterDataSet(entries, "# per month");

        ArrayList<String> months = new ArrayList<>();
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataset);
        ScatterData d = new ScatterData(dataSets);
        scatterChart.setData(d);
        scatterChart.invalidate();


        // the labels that should be drawn on the XAxis
        String[] quarters = new String[] { "2018-11-26", "2018-11-28", "2018-11-27"};

//        IAxisValueFormatter formatter = new IAxisValueFormatter() {
//
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return quarters[(int) value];
//            }
//        };


        IAxisValueFormatter formatter = new MyXAxisValueFormatter(quarters);
        XAxis xAxis = scatterChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);


//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date[] arrayOfDates = new Date[quarters.length];
//        for (int index = 0; index < quarters.length; index++) {
//            try {
//                arrayOfDates[index] = sdf.parse(quarters[index]);
//                Log.d("I", "To Date" +  quarters[index]);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        String [] results = new String[3];
//
//        Arrays.sort(arrayOfDates);
//        for (int index = 0; index < results.length; index++) {
//            results[index] = sdf.format(arrayOfDates[index]);
//            Log.d("I", "From date" + results[index]);
//        }


//        //Testing Scatter Data: hard code point (10,10)
//        ArrayList<Entry> list1 = new ArrayList<Entry>();
//        //Populate the list with new "Entries". These will be data points of the
//        list1.add(new Entry(10,10));
//        ScatterDataSet dataSet = new ScatterDataSet(list1, "Kevin's Data");
//        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//        ArrayList<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
//        dataSets.add(dataSet);
//        ScatterData data = new ScatterData( dataSets);
//        scatterChart.setData(data);
//        scatterChart.invalidate();
    }



    public void chart(){
        int i = 0;
        ArrayList<Entry> completedDays = new ArrayList<Entry>();
        ArrayList<String> dates = new ArrayList<>();
        //Populate the list with new "Entries". These will be data points of the
        //int habitID = myDB.returnIDFromHT(habitName);
        Cursor cursor = myDB.getAllDataHRSortedByDate(habitName);
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }
        else {
            while(cursor.moveToNext()) {
                int complete = Integer.valueOf(cursor.getString(2));
                String date = cursor.getString(1);
                dates.add(customDateFormat(date));
                if(complete > 0) {
                    completedDays.add(new Entry(i ,1));
                }else{
                    completedDays.add(new Entry(i ,0));
                }
                i++;
            }
        }

        ScatterDataSet dataSet = new ScatterDataSet(completedDays, "Kevin's Data");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        int numberOfDates = dates.size();

        String[] values = new String[numberOfDates];

        for(int index = 0; index < numberOfDates; index++){
            values[index] = dates.get(index);
            Log.d("I", " ARRAY[" + index + "] = " + values[index]);
        }

        Log.d("I", "Size of dates arraylist: " + dates.size());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date[] arrayOfDates = new Date[numberOfDates];
//        for (int index = 0; index < numberOfDates; index++) {
//            try {
//                arrayOfDates[index] = sdf.parse(values[index]);
//                Log.d("I", "To Date" +  values[index]);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        //
//        XAxis xAxis = scatterChart.getXAxis();
//        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        ArrayList<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        dataSets.add(dataSet);
        ScatterData data = new ScatterData(dataSets);
        scatterChart.setData(data);
        scatterChart.invalidate();
//
        IAxisValueFormatter formatter = new MyXAxisValueFormatter(values);
//
        XAxis xAxis = scatterChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

    }

    private String customDateFormat(String date){
        String month = date.substring(5,7);
        String day = date.substring(8,10);
        return (month + "/" + day);
    }



}