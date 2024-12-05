package com.example.eventapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.eventapp.repositories.NotificationRepo;

public class NotificationClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationId = intent.getStringExtra("notification_id");
        NotificationRepo.updateNotificationStatus(notificationId);

    }
}
