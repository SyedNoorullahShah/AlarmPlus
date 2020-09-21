package com.alarm.android.alarmplus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.utils.AlarmDBUtils;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, AlarmService.class);
        long alarmId = intent.getLongExtra("alarm-id", 0);
        String alarmType = intent.getStringExtra("alarm-type");

        AlarmDBUtils.setActiveAlarm(alarmId, alarmType);

        alarmIntent.putExtra("alarm-type", alarmType);
        ContextCompat.startForegroundService(context, alarmIntent);
    }
}
