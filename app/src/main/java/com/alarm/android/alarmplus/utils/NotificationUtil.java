package com.alarm.android.alarmplus.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.alarm.android.alarmplus.ui.alarmmode.GameActivity;
import com.alarm.android.alarmplus.ui.lockscreen.LockActivity;
import com.alarm.android.alarmplus.ui.main.MainActivity;
import com.alarm.android.alarmplus.ui.alarmmode.StepCounterActivity;
import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.database.AlarmModeObject;
import com.alarm.android.alarmplus.R;

public class NotificationUtil {

    private static final String CHANNEL_ID = "alarm-channel";
    private static final String CHANNEL_NAME = "Alarms";
    private static final int NOTIF_INTENT_CODE = 101;

    private NotificationUtil() {

    }

    public static Notification buildNotification(Context ctx) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setContentTitle(getTitle())
                .setSmallIcon(R.drawable.pugnotification_ic_launcher)
                .setContentText(getContent())
                .setContentIntent(contentIntent(ctx))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(getFullScreenIntent(ctx), true);

        return builder.build();
    }

    private static PendingIntent getFullScreenIntent(Context ctx) {
        Intent intent = new Intent(ctx, LockActivity.class);
        return PendingIntent.getActivity(ctx,20,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static Notification tempNotification(Context ctx) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        PendingIntent p = PendingIntent.getActivity(ctx, 555, new Intent(ctx, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setContentTitle("kuch bhii")
                .setSmallIcon(R.drawable.pugnotification_ic_launcher)
                .setContentText("just passing by...")
                .setContentIntent(p)
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        return builder.build();
    }


    private static String getContent() {
        AlarmModeObject currentAlarmMode = AlarmDBUtils.getActiveAlarm().getMode();
        return currentAlarmMode.getMode().equals("Game") ? "Time to play the game !" : "Get ready to walk !";
    }

    private static String getTitle() {
        AlarmObject currentAlarmObjectMode = AlarmDBUtils.getActiveAlarm();
        return currentAlarmObjectMode.getLabel();
    }

    private static PendingIntent contentIntent(Context ctx) {
        AlarmModeObject currentAlarmMode = AlarmDBUtils.getActiveAlarm().getMode();
        Class<?> modeCls;

        if (currentAlarmMode.getMode().equals("Game")) {
            modeCls = GameActivity.class;
            GameActivity.setGameValues(currentAlarmMode.getRounds(), currentAlarmMode.getDifficulty());
        } else {
            modeCls = StepCounterActivity.class;
            StepCounterActivity.setSteps(currentAlarmMode.getSteps());
        }

        Intent intent = new Intent(ctx, modeCls);
        return PendingIntent.getActivity(ctx, NOTIF_INTENT_CODE, intent, 0);
    }
}
