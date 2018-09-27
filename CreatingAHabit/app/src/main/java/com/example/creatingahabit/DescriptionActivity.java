package com.example.creatingahabit;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DescriptionActivity extends AppCompatActivity {


    DatabaseHelper myDB;
    TextView freq;
    TextView desc;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        myDB = new DatabaseHelper(this);
        freq = findViewById(R.id.text_frequency);
        desc = findViewById(R.id.text_description);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            name = extras.getString("name");
            setTitle(name);
            showInfo(name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent add = new Intent(this, MainActivity.class);
        startActivity(add);
        return super.onOptionsItemSelected(item);
    }

    public void showInfo(String name) {
        Cursor cursor = myDB.getItem(name);
        if(cursor.getCount() == 0) {
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }
        else {
            cursor.moveToNext();
            freq.setText(cursor.getString(2) + getString(R.string.divide) + cursor.getString(3));
            desc.setText(cursor.getString(1));
        }
    }

    public void clickDelete(View view) {
        if(myDB.deleteData(name)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(DescriptionActivity.this, "Habit deleted", Toast.LENGTH_LONG).show();
        }
    }
}
