package com.example.creatingahabit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;


public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    DatabaseHelper myDB;
    EditText habitName, habitDescription, habitFrequency;
    MenuItem createHabit;
    Spinner frequencySpinner;
    String spinnerSelected;

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
        frequencySpinner = (Spinner)findViewById(R.id.frequency_spinner);
        spinnerCreation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void spinnerCreation(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.frequency_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.save:
                boolean isInserted = myDB.insertData(habitName.getText().toString(),
                        habitDescription.getText().toString(),
                        habitFrequency.getText().toString(), spinnerSelected);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        spinnerSelected = adapterView.getItemAtPosition(position).toString();
        Toast.makeText(adapterView.getContext(), "Selected spinner frequency: "+ spinnerSelected, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
