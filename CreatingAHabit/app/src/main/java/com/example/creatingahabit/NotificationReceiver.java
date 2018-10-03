package com.example.creatingahabit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //get data

        String habitName = "", habitDescription = "", habitFrequency = "", habitTimePeriod = "";

        habitName = intent.getExtras().getString("name");
        habitDescription = intent.getExtras().getString("description");
        habitFrequency = intent.getExtras().getString("frequency");
        habitTimePeriod = intent.getExtras().getString("timePeriod");

        Intent repeatingIntent = new Intent(context, MainActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
        if(habitName.equals("")) {
            habitName = "Title";
            habitDescription = "Description";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.smiley_face)
                .setContentTitle(habitName)
                .setContentText(habitDescription)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationManager.IMPORTANCE_HIGH);

        nm.notify(NOTIFICATION_ID, builder.build());
    }
}
