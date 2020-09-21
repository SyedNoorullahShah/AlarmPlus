package com.alarm.android.alarmplus.ui.alarmmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alarm.android.alarmplus.handler.AlarmHandler;
import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.utils.AlarmDBUtils;

public abstract class AlarmModeActivity extends AppCompatActivity {
    public static boolean isOpened = false;
    private AlarmHandler alarmHandler;
    public static final int MODE_DELAY = 2 * 60 * 1000;
    public static String previousActivity;

    protected final BroadcastReceiver closer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GameActivity.rounds = 1;
            finish();

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpened = true;
        registerReceiver(closer, new IntentFilter("close"));
        previousActivity = this.getClass().getSimpleName();
        alarmHandler = AlarmHandler.getInstance();
        alarmHandler.attachActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("noor", "onResume: ");
        setAlarmDelay(MODE_DELAY);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (AlarmService.isActive) {
            Log.d("noor", "onPause: ");
            setAlarmDelay(AlarmService.SERVICE_DELAY);
        }
        if (isFinishing()) {
            Log.d("noor", "DESTROYING ");
            isOpened = false;
            unregisterReceiver(closer);
        }
    }


    private void setAlarmDelay(int alarmDelay) {
        if (alarmHandler.hasMessages(1)) {
            Log.d("noor", "setAlarmDelay: ");
            alarmHandler.removeMessages(1);
        }
        alarmHandler.sendEmptyMessageDelayed(1, alarmDelay);
    }

    protected void closeAlarm() {
        AlarmObject alarmObject = AlarmDBUtils.getActiveAlarm();

        if (alarmObject != null && alarmObject.isValid()) {
            boolean isAlarmValid = (alarmObject.getTriggerTime() < System.currentTimeMillis());

            if (alarmObject.isActive() && isAlarmValid) {
                AlarmDBUtils.resetState(alarmObject);
            }
        }

        stopService(new Intent(this, AlarmService.class));
        finish();
    }

}
