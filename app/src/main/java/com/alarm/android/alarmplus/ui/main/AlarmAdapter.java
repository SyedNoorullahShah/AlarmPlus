package com.alarm.android.alarmplus.ui.main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.ui.settings.AlarmSettingsActivity;
import com.alarm.android.alarmplus.utils.AlarmDBUtils;
import com.alarm.android.alarmplus.utils.AlarmUtil;

import java.text.DateFormat;
import java.util.Calendar;

public class AlarmAdapter extends RealmRecyclerViewAdapter<AlarmObject, AlarmAdapter.AlarmViewHolder> {
    private LayoutInflater inflater;
    private MainActivity mainActivity;

    public AlarmAdapter(RealmResults<AlarmObject> alarmObjects, MainActivity mainActivity) {
        super(alarmObjects, true, true);
        inflater = mainActivity.getLayoutInflater();
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("alarmzz", "onCreateViewHolder: ");
        View v = inflater.inflate(R.layout.alarm_item, viewGroup, false);
        return new AlarmViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        holder.setTime(getItem(position).getTriggerTime());
        holder.setDate(getItem(position).getTriggerTime());
        holder.setLabel(getItem(position).getLabel());
        holder.setSwitch(getItem(position).isActive());

    }

    public AlarmObject getAlarmAt(int position) {
        return getItem(position);
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        private TextView displayTime;
        private TextView displayLabel;
        private Switch alarmSwitch;
        private TextView displayDate;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            displayTime = itemView.findViewById(R.id.displayTime);
            displayLabel = itemView.findViewById(R.id.displayLabel);
            alarmSwitch = itemView.findViewById(R.id.alarm_switch);
            alarmSwitch.setOnCheckedChangeListener(this);
            displayDate = itemView.findViewById(R.id.date);
        }

        public void setTime(long timeInMillis) {
            displayTime.setText(getTime(timeInMillis));
        }

        public void setLabel(String label) {
            displayLabel.setText(label);
        }

        public void setDate(long triggerTime) {
            displayDate.setText(getDisplayDate(triggerTime));
        }

        private String getDisplayDate(long triggerTime) {
            return DateUtils.getRelativeTimeSpanString(triggerTime, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();

        }

        private String getTime(long timeInMillis) {
            return DateFormat.getTimeInstance(DateFormat.SHORT).format(timeInMillis);
        }

        public void setSwitch(boolean active) {
            alarmSwitch.setChecked(active);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.isPressed()) {

                Log.d("alarmzz", "onCheckedChanged: " + DateFormat.getTimeInstance(DateFormat.SHORT).format(getItem(getAdapterPosition()).getTriggerTime()));

                AlarmObject selectedAlarmObject = getItem(getAdapterPosition());

                if (isChecked) {
                    if (selectedAlarmObject.getTriggerTime() > System.currentTimeMillis()) {

                        Realm.getDefaultInstance().beginTransaction();
                        selectedAlarmObject.setActive(true);
                        Realm.getDefaultInstance().commitTransaction();
                    } else {
                        //adding 24 hours
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(selectedAlarmObject.getTriggerTime());
                        c.add(Calendar.DATE, 1);

                        //updating trigger time
                        Realm.getDefaultInstance().beginTransaction();
                        selectedAlarmObject.setTriggerTime(c.getTimeInMillis());
                        selectedAlarmObject.setActive(true);
                        Realm.getDefaultInstance().commitTransaction();
                    }

                    AlarmUtil.setAlarm(mainActivity, selectedAlarmObject.getTriggerTime(), selectedAlarmObject.getCurrentTime(), "main");
                } else {
                    if (selectedAlarmObject.getTriggerTime() > System.currentTimeMillis()) {
                        AlarmUtil.cancelAlarm(mainActivity, selectedAlarmObject.getCurrentTime());
                    } else {
                        if (!AlarmService.isActive) {      //alarm is not running
                            AlarmUtil.cancelAlarm(mainActivity, selectedAlarmObject.getSnoozeId());
                        }
                    }
                    AlarmDBUtils.resetState(selectedAlarmObject);

                }
            }

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mainActivity, AlarmSettingsActivity.class);
            intent.setAction("action-update");
            putExtras(intent);
            mainActivity.startActivity(intent);
        }

        private void putExtras(Intent intent) {
            //putting time data
            intent.putExtra("hour", getHourMin("hour", getItem(getAdapterPosition()).getTriggerTime()));
            intent.putExtra("minute", getHourMin("minute", getItem(getAdapterPosition()).getTriggerTime()));
            intent.putExtra("timeId",getItem(getAdapterPosition()).getCurrentTime());
            //label
            intent.putExtra("label", getItem(getAdapterPosition()).getLabel());
            //ringtone
            intent.putExtra("ringId", getItem(getAdapterPosition()).getRingId());
            //alarm mode
            intent.putExtra("mode", getItem(getAdapterPosition()).getMode().getMode());
            intent.putExtra("difficulty", getItem(getAdapterPosition()).getMode().getDifficulty());
            intent.putExtra("rounds", getItem(getAdapterPosition()).getMode().getRounds());
            intent.putExtra("steps", getItem(getAdapterPosition()).getMode().getSteps());
            //snooze data
            intent.putExtra("willSnooze", getItem(getAdapterPosition()).willSnooze());
            intent.putExtra("snoozeId", getItem(getAdapterPosition()).getSnoozeId());
            //track
            intent.putExtra("trackAlarm", getItem(getAdapterPosition()).isTrackAlarm());
        }

        private int getHourMin(String what, long triggerTime) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(triggerTime);
            if (what.equals("hour")) {
                return c.get(Calendar.HOUR_OF_DAY);
            } else {
                return c.get(Calendar.MINUTE);
            }
        }
    }
}
