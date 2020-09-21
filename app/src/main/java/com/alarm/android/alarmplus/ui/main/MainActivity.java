package com.alarm.android.alarmplus.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.ui.settings.AlarmSettingsActivity;
import com.alarm.android.alarmplus.utils.AlarmDBUtils;
import com.alarm.android.alarmplus.utils.AlarmUtil;
import com.alarm.android.alarmplus.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private AlarmAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.add_btn);
        fab.setOnClickListener(this);
        setRecyclerView();
    }


    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.alarm_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlarmAdapter(AlarmDBUtils.getAlarms(), this);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlarmObject alarmObject = adapter.getAlarmAt(viewHolder.getAdapterPosition());

                if (alarmObject.getTriggerTime() > System.currentTimeMillis()) {
                    AlarmUtil.cancelAlarm(MainActivity.this, alarmObject.getCurrentTime());
                } else {
                    if (!AlarmService.isActive) {      //alarm is not running
                        AlarmUtil.cancelAlarm(MainActivity.this, alarmObject.getSnoozeId());
                    }
                }
                AlarmDBUtils.deleteAlarm(alarmObject);
            }
        }).attachToRecyclerView(recyclerView);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, AlarmSettingsActivity.class);
        intent.setAction("action-insert");
        startActivity(intent);
    }


}
