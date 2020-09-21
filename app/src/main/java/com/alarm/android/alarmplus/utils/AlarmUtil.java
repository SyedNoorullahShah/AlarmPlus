package com.alarm.android.alarmplus.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.alarm.android.alarmplus.receivers.AlarmReceiver;
import com.alarm.android.alarmplus.ui.main.MainActivity;
import com.alarm.android.alarmplus.database.AlarmObject;

import java.util.Calendar;

public final class AlarmUtil {
    private static final long TWO_MINUTES = 2 * 60 * 1000;

    public static void setAlarm(Context ctx, long triggerTime, long requestID, String type) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmAction;

        //setting pending intent
        Intent actionIntent = new Intent(ctx, AlarmReceiver.class);
        actionIntent.putExtra("alarm-type", type);
        actionIntent.putExtra("alarm-id", requestID);

        alarmAction = PendingIntent.getBroadcast(ctx, (int) requestID, actionIntent, 0);

        //setting alarm manager for different versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PendingIntent infoIntent = PendingIntent.getActivity(ctx, (int) requestID, new Intent(ctx, MainActivity.class), 0);
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, infoIntent);
            alarmManager.setAlarmClock(info, alarmAction);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, alarmAction);
        }
    }

    public static void cancelAlarm(Context ctx, long cancelId) {
        AlarmManager manager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        PendingIntent cancelAlarm = PendingIntent.getBroadcast(ctx, (int) cancelId, new Intent(ctx, AlarmReceiver.class), 0);
        manager.cancel(cancelAlarm);
    }

    public static void setSnooze(Context ctx, long snoozeId) {
        AlarmObject currentAlarmObject = AlarmDBUtils.getActiveAlarm();
        long snoozeTime = getSnoozeTime(snoozeId);

        if (currentAlarmObject != null && currentAlarmObject.isValid()) {

            boolean isAlarmValid = (currentAlarmObject.getTriggerTime() < System.currentTimeMillis());

            if (currentAlarmObject.willSnooze() && currentAlarmObject.isActive()
                    && currentAlarmObject.getSnooze() <= 2 && isAlarmValid) {
                Log.d("alarmzz", "SET SNOOZE !!!");

                AlarmDBUtils.updateSnooze(currentAlarmObject, snoozeTime, snoozeId);
                setAlarm(ctx, snoozeTime, snoozeId, "snooze");
                Toast.makeText(ctx, "Snooze is set", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("alarmzz", "NO SNOOZE !!!");
                AlarmDBUtils.resetState(currentAlarmObject);
            }
        }
    }

    private static long getSnoozeTime(long currentTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(currentTime);
        c.set(Calendar.SECOND, 0);

        return (c.getTimeInMillis() + TWO_MINUTES);
    }
}
