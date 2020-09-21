package com.alarm.android.alarmplus.ui.ringtone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.utils.MediaPlayerUtils;
import com.alarmObject.android.alarmplus.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RingtoneActivity extends AppCompatActivity {
    private RingtoneAdapter ringtoneAdapter;
    private Ringtone selectedTone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        ArrayList<Ringtone> ringtones = new ArrayList<>();
        try {
            setRingtones(ringtones, getIntent().getStringExtra("tone-name"));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        setRecyclerView(ringtones);

    }

    private void setRingtones(ArrayList<Ringtone> ringtones, String toneName) throws IllegalAccessException {
        Field[] fields = R.raw.class.getFields();

        for (Field field : fields) {
            Ringtone ringtone = new Ringtone(field.getName(), field.getInt(field));
            if (ringtone.getName().equals(toneName)) {
                ringtone.setSelected(true);
                selectedTone = ringtone;
            }
            ringtones.add(ringtone);
        }
    }

    private void setRecyclerView(ArrayList<Ringtone> ringtones) {
        RecyclerView ringtoneList = findViewById(R.id.ringtone_list);
        ringtoneAdapter = new RingtoneAdapter(this, ringtones, selectedTone);

        ringtoneList.setLayoutManager(new LinearLayoutManager(this));
        ringtoneList.setAdapter(ringtoneAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!AlarmService.isActive)
            MediaPlayerUtils.stop();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Ringtone tone = ringtoneAdapter.getSelectedTone();

        Log.d("noor", "onBackPressed: ");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("tone-name", tone.getName());
        resultIntent.putExtra("tone-id", tone.getResId());

        setResult(RESULT_OK, resultIntent);
        super.finish();
    }
}
