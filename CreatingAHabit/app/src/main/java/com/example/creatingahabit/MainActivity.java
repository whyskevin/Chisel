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

//Method for displaying all values of the database in an Alert Dialog
//    public void viewAll() {
//        button.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                Cursor res = myDB.getAllData();
//                if(res.getCount() == 0){ //A table with empty rows will not have any data
//                    showMessage("Error", "Nothing found");
//                }
//                else {
//                    StringBuffer buffer = new StringBuffer();
//                    while (res.moveToNext()){ //Grabs records from the table
//                        buffer.append("NAME : " + res.getString(0) + "\n");
//                        buffer.append("DESCRIPTION : " + res.getString(1) + "\n");
//                        buffer.append("FREQUENCY : " + res.getString(2)+ "\n\n");
//                    }
//
//                    showMessage("Data", buffer.toString());
//                }
//            }
//        });
//    }


    //Purpose: Displays any String message in an Alerg
    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
