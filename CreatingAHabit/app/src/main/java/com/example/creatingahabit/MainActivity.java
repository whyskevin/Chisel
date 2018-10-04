package com.example.creatingahabit;

import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    Button button;
    ListView userList;
    ArrayList<String> listItem;
    ArrayAdapter adapter;

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
        userList.setAdapter(new myListAdapter( this, R.layout.list_item, listItem));

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
        Cursor cursor = myDB.viewData();
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }
        else {
            while(cursor.moveToNext()) {
                listItem.add(cursor.getString(0));  //col 1 is name, col 0 is id
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

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            ViewHolder mainViewHolder = null;
            if (convertView == null){

                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.textView);

                viewHolder.title.setText(getItem(position).toString());
                viewHolder.buttonMon = (Button) convertView.findViewById(R.id.button);
                viewHolder.buttonMon.setOnClickListener(new View.OnClickListener() {
                    int i=1;
                    @Override
                    public void onClick(View v) {
                        if (i == 0 ) {
                            viewHolder.buttonMon.setBackgroundResource(R.drawable.circle_shape);
                            i++;
                        }
                        else if (i == 1){
                            viewHolder.buttonMon.setBackgroundResource(R.drawable.circle_shape_green);
                            i++;
                        }
                        else if(i ==2){
                            viewHolder.buttonMon.setBackgroundResource(R.drawable.circle_shape_red);
                            i++;
                        }
                        else if( i ==3){
                            viewHolder.buttonMon.setBackgroundResource(R.drawable.circle_shape_white);
                            i = 0;
                        }
                    }
                });
                viewHolder.buttonTues = (Button) convertView.findViewById(R.id.button1);
                viewHolder.buttonTues.setOnClickListener(new View.OnClickListener() {
                    int i=1;
                    @Override
                    public void onClick(View v) {
                        if (i == 0 ) {
                            viewHolder.buttonTues.setBackgroundResource(R.drawable.circle_shape);
                            i++;
                        }
                        else if (i == 1){
                            viewHolder.buttonTues.setBackgroundResource(R.drawable.circle_shape_green);
                            i++;
                        }
                        else if(i ==2){
                            viewHolder.buttonTues.setBackgroundResource(R.drawable.circle_shape_red);
                            i++;
                        }
                        else if( i ==3){
                            viewHolder.buttonTues.setBackgroundResource(R.drawable.circle_shape_white);
                            i = 0;
                        }
                    }
                });

                viewHolder.buttonWed = (Button) convertView.findViewById(R.id.button2);
                viewHolder.buttonWed.setOnClickListener(new View.OnClickListener() {
                    int i=1;
                    @Override
                    public void onClick(View v) {
                        if (i == 0 ) {
                            viewHolder.buttonWed.setBackgroundResource(R.drawable.circle_shape);
                            i++;
                        }
                        else if (i == 1){
                            viewHolder.buttonWed.setBackgroundResource(R.drawable.circle_shape_green);
                            i++;
                        }
                        else if(i ==2){
                            viewHolder.buttonWed.setBackgroundResource(R.drawable.circle_shape_red);
                            i++;
                        }
                        else if( i ==3){
                            viewHolder.buttonWed.setBackgroundResource(R.drawable.circle_shape_white);
                            i = 0;
                        }
                    }
                });
                viewHolder.buttonThur = (Button) convertView.findViewById(R.id.button3);
                viewHolder.buttonThur.setOnClickListener(new View.OnClickListener() {
                    int i=1;
                    @Override
                    public void onClick(View v) {
                        if (i == 0 ) {
                            viewHolder.buttonThur.setBackgroundResource(R.drawable.circle_shape);
                            i++;
                        }
                        else if (i == 1){
                            viewHolder.buttonThur.setBackgroundResource(R.drawable.circle_shape_green);
                            i++;
                        }
                        else if(i ==2){
                            viewHolder.buttonThur.setBackgroundResource(R.drawable.circle_shape_red);
                            i++;
                        }
                        else if( i ==3){
                            viewHolder.buttonThur.setBackgroundResource(R.drawable.circle_shape_white);
                            i = 0;
                        }
                    }
                });
                viewHolder.buttonFri = (Button) convertView.findViewById(R.id.button4);
                viewHolder.buttonFri.setOnClickListener(new View.OnClickListener() {
                    int i=1;
                    @Override
                    public void onClick(View v) {
                        if (i == 0 ) {
                            viewHolder.buttonFri.setBackgroundResource(R.drawable.circle_shape);
                            i++;
                        }
                        else if (i == 1){
                            viewHolder.buttonFri.setBackgroundResource(R.drawable.circle_shape_green);
                            i++;
                        }
                        else if(i ==2){
                            viewHolder.buttonFri.setBackgroundResource(R.drawable.circle_shape_red);
                            i++;
                        }
                        else if( i ==3){
                            viewHolder.buttonFri.setBackgroundResource(R.drawable.circle_shape_white);
                            i = 0;
                        }
                    }
                });
                viewHolder.buttonSat = (Button) convertView.findViewById(R.id.button5);
                viewHolder.buttonSat.setOnClickListener(new View.OnClickListener() {
                    int i=1;
                    @Override
                    public void onClick(View v) {
                        if (i == 0 ) {
                            viewHolder.buttonSat.setBackgroundResource(R.drawable.circle_shape);
                            i++;
                        }
                        else if (i == 1){
                            viewHolder.buttonSat.setBackgroundResource(R.drawable.circle_shape_green);
                            i++;
                        }
                        else if(i ==2){
                            viewHolder.buttonSat.setBackgroundResource(R.drawable.circle_shape_red);
                            i++;
                        }
                        else if( i ==3){
                            viewHolder.buttonSat.setBackgroundResource(R.drawable.circle_shape_white);
                            i = 0;
                        }
                    }
                });
                viewHolder.buttonSun = (Button) convertView.findViewById(R.id.button6);
                viewHolder.buttonSun.setOnClickListener(new View.OnClickListener() {
                    int i=1;
                    @Override
                    public void onClick(View v) {
                        if (i == 0 ) {
                            viewHolder.buttonSun.setBackgroundResource(R.drawable.circle_shape);
                            i++;
                        }
                        else if (i == 1){
                            viewHolder.buttonSun.setBackgroundResource(R.drawable.circle_shape_green);
                            i++;
                        }
                        else if(i ==2){
                            viewHolder.buttonSun.setBackgroundResource(R.drawable.circle_shape_red);
                            i++;
                        }
                        else if( i ==3){
                            viewHolder.buttonSun.setBackgroundResource(R.drawable.circle_shape_white);
                            i = 0;
                        }
                    }
                });


                convertView.setTag(viewHolder);
            }
            else{

                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.title.setText(getItem(position).toString());
            }
            return convertView;
        }


    }
    public class ViewHolder {
        TextView title;
        Button buttonMon;
        Button buttonTues;
        Button buttonWed;
        Button buttonThur;
        Button buttonFri;
        Button buttonSat;
        Button buttonSun;
    }

    public void viewAll() {
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Cursor res = myDB.getAllData();
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
