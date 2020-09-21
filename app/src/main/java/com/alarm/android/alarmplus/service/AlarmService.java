package com.alarm.android.alarmplus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alarm.android.alarmplus.handler.AlarmHandler;
import com.alarm.android.alarmplus.ui.alarmmode.AlarmModeActivity;
import com.alarm.android.alarmplus.ui.alarmmode.GameActivity;
import com.alarm.android.alarmplus.ui.alarmmode.StepCounterActivity;
import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.utils.AlarmDBUtils;
import com.alarm.android.alarmplus.utils.AlarmUtil;
import com.alarm.android.alarmplus.utils.MediaPlayerUtils;
import com.alarm.android.alarmplus.utils.NotificationUtil;

public class AlarmService extends Service {
    private AlarmHandler alarmHandler;
    public static final int SERVICE_DELAY = 2 * 60 * 1000;
    public static boolean isActive = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmHandler = AlarmHandler.getInstance();
        alarmHandler.attachService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("alarmzz", "onStartCommand: " + intent.getStringExtra("alarm-type"));

        isActive = true;
        clearPreviousAlarms(intent.getStringExtra("alarm-type"), AlarmDBUtils.getActiveAlarm().getTriggerTime());          //if any
        closeModeActivity();        //if it is opened
        startForeground(1, NotificationUtil.buildNotification(this));
        MediaPlayerUtils.play(this, AlarmDBUtils.getActiveAlarm().getRingId(), true);

        if (AlarmModeActivity.isOpened)
            alarmHandler.sendEmptyMessageDelayed(1, GameActivity.MODE_DELAY);
        else
            alarmHandler.sendEmptyMessageDelayed(1, SERVICE_DELAY);

        return START_NOT_STICKY;
    }

    private void closeModeActivity() {
        if (AlarmModeActivity.isOpened) {
            String currentMode = AlarmDBUtils.getActiveAlarm().getMode().getMode();

            if (currentMode.equals("Game") && !AlarmModeActivity.previousActivity.equals("GameActivity")) {
                sendBroadcast(new Intent("close"));
                Intent intent = new Intent(this, GameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (currentMode.equals("Step Counter") && !AlarmModeActivity.previousActivity.equals("StepCounterActivity")) {       //if it is step counter mode
                sendBroadcast(new Intent("close"));
                Intent intent = new Intent(this, StepCounterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void clearPreviousAlarms(String alarmType, long currentAlarm) {
        if (alarmHandler.hasMessages(1)) {
            alarmHandler.removeMessages(1);
        }

        if (alarmType.equals("main")) {

            AlarmObject previousAlarmObject = AlarmDBUtils.getPreviousAlarm(currentAlarm);

            if (previousAlarmObject != null && previousAlarmObject.isActive()) {
                Log.d("alarmzz", "PREVIOUS ALARMS EXIST ");

                if (previousAlarmObject.getSnoozeTime() >= currentAlarm) {
                    AlarmUtil.cancelAlarm(this, previousAlarmObject.getSnoozeId());
                }
                AlarmDBUtils.resetState(previousAlarmObject);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActive = false;
        MediaPlayerUtils.stop();
        if (alarmHandler.hasMessages(1)) {
            alarmHandler.removeMessages(1);
        }

    }

}