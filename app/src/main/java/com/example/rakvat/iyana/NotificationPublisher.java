package com.example.rakvat.iyana;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationPublisher extends BroadcastReceiver{

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);

        // led lights
        notification.ledARGB = 0xFFFFFFFF;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notification.ledOnMS = 200;
        notification.ledOffMS = 200;

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }
}
