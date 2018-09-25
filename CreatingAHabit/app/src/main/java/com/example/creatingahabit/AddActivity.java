package com.example.creatingahabit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity{

    DatabaseHelper myDB;
    EditText habitName, habitDescription, habitFrequency;
    MenuItem createHabit;

    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;

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

        notification = new NotificationCompat.Builder(this, "MyChannelId_01");
        notification.setAutoCancel(true);

    }

    public void notifButtonClicked(View view) {

        //send to home screen when clicked on notif
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Builds notification and issues (sending out to device) it
        //object to send notif
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int NOTIFICATION_ID = 234;
        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "This is my channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            nm.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.smiley_face)
                .setContentTitle("Here is the title")
                .setContentText("I am the body text")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        nm.notify(NOTIFICATION_ID, builder.build());
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
