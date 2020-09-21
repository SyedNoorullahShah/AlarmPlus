package com.alarm.android.alarmplus.ui.lockscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.alarm.android.alarmplus.ui.alarmmode.StepCounterActivity;
import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.database.AlarmModeObject;
import com.alarm.android.alarmplus.ui.alarmmode.GameActivity;
import com.alarm.android.alarmplus.utils.AlarmDBUtils;
import com.alarmObject.android.alarmplus.R;
import com.ncorti.slidetoact.SlideToActView;

import org.jetbrains.annotations.NotNull;

public class LockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        SlideToActView slider = (SlideToActView) findViewById(R.id.slider);
        slider.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NotNull SlideToActView slideToActView) {
                Intent intent = new Intent(LockActivity.this, contentIntent());
                startActivity(intent);
                finish();

            }
        });

        TextView label = (TextView) findViewById(R.id.label);
        label.setText(getLabel());
        TextView desc = (TextView) findViewById(R.id.desc);
        desc.setText(getDesc());


    }

    private String getDesc() {
        AlarmModeObject currentAlarmMode = AlarmDBUtils.getActiveAlarm().getMode();
        return currentAlarmMode.getMode().equals("Game") ? "Time to play the game !" : "Get ready to walk !";
    }

    private String getLabel() {
        AlarmObject currentAlarmObjectMode = AlarmDBUtils.getActiveAlarm();
        return currentAlarmObjectMode.getLabel();
    }

    private static Class contentIntent() {
        AlarmModeObject currentAlarmMode = AlarmDBUtils.getActiveAlarm().getMode();
        Class<?> modeCls;

        if (currentAlarmMode.getMode().equals("Game")) {
            modeCls = GameActivity.class;
            GameActivity.setGameValues(currentAlarmMode.getRounds(), currentAlarmMode.getDifficulty());
        } else {
            modeCls = StepCounterActivity.class;
            StepCounterActivity.setSteps(currentAlarmMode.getSteps());
        }

        return modeCls;
    }
}
