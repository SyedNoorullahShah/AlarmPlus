package com.alarm.android.alarmplus.ui.alarmmode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alarm.android.alarmplus.ui.alarmmode.AlarmModeActivity;
import com.alarmObject.android.alarmplus.R;

public class StepCounterActivity extends AlarmModeActivity implements SensorEventListener {
    private TextView stepText;
    private boolean isWalking = false;
    private SensorManager sensorManager;
    private static int totalSteps;
    private int steps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        stepText = findViewById(R.id.steps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    public static void setSteps(int steps) {
        totalSteps = steps;
    }

    @Override
    protected void onResume() {
        super.onResume();

        isWalking = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isWalking = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
      if(isWalking) {
          steps++;
          stepText.setText(String.valueOf(steps));
          if (steps == totalSteps)
              closeAlarm();
      }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
