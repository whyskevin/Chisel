package com.example.creatingahabit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity{

    DatabaseHelper myDB;
    EditText habitName, habitDescription, habitFrequency;
    MenuItem createHabit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        myDB = new DatabaseHelper(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        habitName = (EditText) findViewById(R.id.habitName);
        habitDescription = (EditText) findViewById(R.id.description);
        habitFrequency = (EditText) findViewById(R.id.frequency);
        createHabit = (MenuItem) findViewById(R.id.save);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.save:
                boolean isInserted = myDB.insertData(habitName.getText().toString(),
                        habitDescription.getText().toString(),
                        habitFrequency.getText().toString());
                if (isInserted) {
                    Toast.makeText(AddActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
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

}
