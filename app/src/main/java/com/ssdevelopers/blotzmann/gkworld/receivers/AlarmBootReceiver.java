package com.ssdevelopers.blotzmann.gkworld.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ssdevelopers.blotzmann.gkworld.notification.NotificationHelper;

public class AlarmBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationHelper.scheduleRepeatingElapsedNotification(context);
        }
    }
}
