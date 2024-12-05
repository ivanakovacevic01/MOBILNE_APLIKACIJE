package com.example.eventapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.eventapp.activities.HomeActivity;
import com.example.eventapp.activities.MainActivity;

public class NotificationHelper {


    public static void createNotificationChannel(Context context,String CHANNEL_ID,String CHANNEL_NAME,String CHANNEL_DESCRIPTION) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

public static void showNotification(Context context, String title, String text, String CHANNEL_ID, String notificationId) {
    int requestCode = generateUniqueRequestCode();
    Intent intent = new Intent(context, HomeActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Add FLAG_IMMUTABLE flag here

    // Create an intent to launch when the notification is clicked
    Intent notificationIntent = new Intent(context, NotificationClickReceiver.class);
    notificationIntent.setAction("com.example.eventapp.NOTIFICATION_CLICKED");
    notificationIntent.putExtra("notification_id", notificationId); // Pass the notification ID as extra data
    PendingIntent clickIntent = PendingIntent.getBroadcast(context, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE); // Add FLAG_IMMUTABLE flag here

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_suggested_background)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_delete, "Mark as Seen", clickIntent); // Add an action button for marking as seen

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(notificationId.hashCode(), builder.build()); // Use hashCode of notificationId as the notification ID
}
    private static int generateUniqueRequestCode() {
        return (int) System.currentTimeMillis(); // Using current timestamp as request code
    }
}
