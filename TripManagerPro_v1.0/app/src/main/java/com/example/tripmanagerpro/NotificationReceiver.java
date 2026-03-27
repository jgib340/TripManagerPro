package com.example.tripmanagerpro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "trip_manager_alerts";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_TEXT = "text";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(EXTRA_TITLE);
        String text = intent.getStringExtra(EXTRA_TEXT);

        if (title == null) title = "Trip Manager Alert";
        if (text == null) text = "You have an upcoming date.";

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Trip Manager Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        nm.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        nm.notify((int) System.currentTimeMillis(), builder.build());
    }
}