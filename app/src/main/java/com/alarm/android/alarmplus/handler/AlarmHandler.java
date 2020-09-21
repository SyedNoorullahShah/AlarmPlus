package com.alarm.android.alarmplus.handler;

import android.os.Handler;
import android.os.Message;

import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.ui.alarmmode.AlarmModeActivity;
import com.alarm.android.alarmplus.ui.alarmmode.GameActivity;
import com.alarm.android.alarmplus.utils.AlarmUtil;

import java.lang.ref.WeakReference;

public class AlarmHandler extends Handler {
    private static AlarmHandler alarmHandler;
    private WeakReference<AlarmService> alarmService;
    private WeakReference<AlarmModeActivity> modeActivity;

    private AlarmHandler() {
    }

    public void attachService(AlarmService alarmService) {
        this.alarmService = new WeakReference<>(alarmService);
    }

    public void attachActivity(AlarmModeActivity modeActivity) {
        this.modeActivity = new WeakReference<>(modeActivity);
    }

    public static AlarmHandler getInstance() {
        if (alarmHandler == null) {
            alarmHandler = new AlarmHandler();
        }
        return alarmHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 1) {
            AlarmService.isActive = false;
            GameActivity.rounds = 1; //resetting rounds
            if (AlarmModeActivity.isOpened) {
                modeActivity.get().finish();
            }

            AlarmUtil.setSnooze(alarmService.get(), System.currentTimeMillis());
            alarmService.get().stopSelf();
        }
    }


}
