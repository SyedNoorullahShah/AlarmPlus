package com.alarm.android.alarmplus.ui.settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.ui.ringtone.RingtoneActivity;
import com.alarm.android.alarmplus.utils.AlarmDBUtils;
import com.alarm.android.alarmplus.utils.AlarmUtil;
import com.alarm.android.alarmplus.R;

import java.util.Calendar;

public class AlarmSettingsActivity extends AppCompatActivity implements View.OnClickListener,
        AlarmSettingsModeDialog.SettingsDialogListener, CompoundButton.OnCheckedChangeListener {

    TimePicker alarmPicker;
    private String label;
    private int ringId;
    private String mode, difficulty;
    private int rounds, steps;
    private boolean willSnooze;
    private boolean trackAlarm;
    private long timeId, snoozeId;

    private TextView labelText, modeText, ringText;
    Switch snoozeSwitch, trackSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);
        alarmPicker = findViewById(R.id.alarm_picker);

        if (getIntent().getAction().equals("action-insert")) {
            setDefaultValues();
        } else if (getIntent().getAction().equals("action-update")) {
            setItemValues();
        }
        setComponents();
    }

    private void setItemValues() {
        Intent updateIntent = getIntent();

        setTime(updateIntent);
        label = updateIntent.getStringExtra("label");
        ringId = updateIntent.getIntExtra("ringId", R.raw.alarm_x);
        mode = updateIntent.getStringExtra("mode");
        difficulty = updateIntent.getStringExtra("difficulty");
        rounds = updateIntent.getIntExtra("rounds", 1);
        steps = updateIntent.getIntExtra("steps", 0);
        willSnooze = updateIntent.getBooleanExtra("willSnooze", false);
        trackAlarm = updateIntent.getBooleanExtra("trackAlarm", false);
        timeId = updateIntent.getLongExtra("timeId", 0);
        snoozeId = updateIntent.getLongExtra("snoozeId", 0);
    }

    private void setDefaultValues() {
        label = "Alarm";
        ringId = R.raw.alarm_x;
        mode = "Game";
        difficulty = "Normal";
        rounds = 1;
        steps = 0;
        willSnooze = false;
        trackAlarm = false;
    }

    private void setTime(Intent updateIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmPicker.setHour(updateIntent.getIntExtra("hour", 0));
            alarmPicker.setMinute(updateIntent.getIntExtra("minute", 0));
        } else {
            alarmPicker.setCurrentHour(updateIntent.getIntExtra("hour", 0));
            alarmPicker.setCurrentMinute(updateIntent.getIntExtra("minute", 0));
        }
    }

    private void setComponents() {
        RelativeLayout itemLabel = findViewById(R.id.item_label);
        itemLabel.setOnClickListener(this);
        labelText = findViewById(R.id.label_caption);
        labelText.setText(label);

        RelativeLayout itemMOde = findViewById(R.id.item_mode);
        itemMOde.setOnClickListener(this);
        modeText = findViewById(R.id.mode_caption);
        modeText.setText(mode);

        RelativeLayout itemRing = findViewById(R.id.item_ringtone);
        itemRing.setOnClickListener(this);
        ringText = findViewById(R.id.ringtone_caption);
        ringText.setText(getResources().getResourceEntryName(ringId).replace("_", " "));

        snoozeSwitch = findViewById(R.id.snooze_switch);
        snoozeSwitch.setChecked(willSnooze);
        trackSwitch = findViewById(R.id.track_switch);
        trackSwitch.setChecked(trackAlarm);
        trackSwitch.setOnCheckedChangeListener(this);

        Button btnSet = findViewById(R.id.btn_set);
        btnSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_label:
                openSettingsDialog("label-dialog");
                break;
            case R.id.item_mode:
                openSettingsDialog("mode-dialog");
                break;
            case R.id.item_ringtone:
                openRingtoneActivity();
                break;
            case R.id.btn_set:
                saveAlarm();
                break;
        }
    }

    private void saveAlarm() {
        if (getIntent().getAction().equals("action-update")) {
            disablePreviousAlarm();
        }
        willSnooze = snoozeSwitch.isChecked();
        trackAlarm = trackSwitch.isChecked();

        long triggerTime = getTriggerTime(getHour(), getMin());
        long alarmId;

        if (getIntent().getAction().equals("action-insert")) {
            alarmId = System.currentTimeMillis();
            AlarmDBUtils.insertAlarm(alarmId, triggerTime, label, mode, difficulty, rounds, steps, ringId, willSnooze, trackAlarm);
        } else {
            alarmId = timeId;
            AlarmDBUtils.updateAlarm(alarmId, triggerTime, label, mode, difficulty, rounds, steps, ringId, willSnooze, trackAlarm);
        }
        AlarmUtil.setAlarm(this, triggerTime, alarmId, "main");
        finish();
    }

    private void disablePreviousAlarm() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, getIntent().getIntExtra("hour", 0));
        c.set(Calendar.MINUTE, getIntent().getIntExtra("minute", 0));
        c.set(Calendar.SECOND, 0);

        if (c.getTimeInMillis() > System.currentTimeMillis()) {
            Log.d("noor", "main alarm cancelled");
            AlarmUtil.cancelAlarm(this, timeId);
        } else {
            if (!AlarmService.isActive) {      //alarm is not running
                Log.d("noor", "snooze alarm cancelled");
                AlarmUtil.cancelAlarm(this, snoozeId);
            }
        }
    }

    private int getMin() {
        if (Build.VERSION.SDK_INT < 23) {
            return alarmPicker.getCurrentMinute();
        } else {
            return alarmPicker.getMinute();
        }
    }

    private int getHour() {
        if (Build.VERSION.SDK_INT < 23) {
            return alarmPicker.getCurrentHour();
        } else {
            return alarmPicker.getHour();
        }
    }

    private long getTriggerTime(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        if (c.getTimeInMillis() < System.currentTimeMillis()) {
            c.add(Calendar.DATE, 1);

        }
        return c.getTimeInMillis();
    }

    private void openRingtoneActivity() {
        Intent intent = new Intent(this, RingtoneActivity.class);

        intent.putExtra("tone-name", ringText.getText().toString().replace(" ", "_"));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView ringText = findViewById(R.id.ringtone_caption);

        if (resultCode == RESULT_OK) {
            ringText.setText(data.getStringExtra("tone-name").replace("_", " "));
            ringId = data.getIntExtra("tone-id", R.raw.alarm_x);

            Log.d("noor", "ring id: " + data.getIntExtra("tone-id", R.raw.alarm_x));
        } else if (resultCode == RESULT_CANCELED) {
            ringText.setText("None");
        }

    }

    private void openSettingsDialog(String tag) {
        AlarmSettingsModeDialog dialog = new AlarmSettingsModeDialog();
        dialog.show(getSupportFragmentManager(), tag);
    }

    @Override
    public void setLabel(String label) {
        labelText.setText(label);
        this.label = label;
    }

    @Override
    public void setMode(Bundle modeData) {
        modeText.setText(modeData.getString("mode"));

        mode = modeData.getString("mode");
        difficulty = modeData.getString("level", "NO VALUE");
        steps = modeData.getInt("steps", 0);
        rounds = modeData.getInt("rounds", 0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //check if there is no other track alarm for today
            AlarmObject trackAlarmObject = AlarmDBUtils.getTodayAlarms(getStartOfDay(), getEndOfDay());

            if (trackAlarmObject != null) {
                Toast.makeText(this, "You can only track one alarm per day.", Toast.LENGTH_SHORT).show();
                trackSwitch.setChecked(false);
            }

        }
    }


    private long getStartOfDay() {
        long triggerTime = getTriggerTime(getHour(), getMin());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(triggerTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfDay() {
        long triggerTime = getTriggerTime(getHour(), getMin());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(triggerTime);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();

    }

}