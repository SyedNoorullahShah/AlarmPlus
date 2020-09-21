package com.alarm.android.alarmplus.ui.ringtone;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alarm.android.alarmplus.service.AlarmService;
import com.alarm.android.alarmplus.utils.MediaPlayerUtils;
import com.alarm.android.alarmplus.R;

import java.util.ArrayList;

class RingtoneAdapter extends RecyclerView.Adapter {
    private LayoutInflater inflater;
    private ArrayList<Ringtone> ringtones;
    private RingtoneActivity ringtoneActivity;
    private int checkPosition;
    private Ringtone selectedTone;

    public RingtoneAdapter(RingtoneActivity ringtoneActivity, ArrayList<Ringtone> ringtones, Ringtone selectedTone) {
        inflater = ringtoneActivity.getLayoutInflater();
        this.ringtones = ringtones;
        this.ringtoneActivity = ringtoneActivity;
        this.selectedTone = selectedTone;
        checkPosition = this.ringtones.indexOf(selectedTone);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.ringtone_item, parent, false);
        return new RingtoneHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RingtoneHolder ringtoneHolder = (RingtoneHolder) holder;
        ringtoneHolder.setRingtoneLabel(ringtones.get(position).getName());

        Log.d("noor", "onBindViewHolder: " + position + "   " + ringtones.get(position).isSelected());
        ringtoneHolder.setChecker(ringtones.get(position).isSelected());
    }

    @Override
    public int getItemCount() {
        return ringtones.size();
    }

    public Ringtone getSelectedTone() {
        return selectedTone;
    }

    private class RingtoneHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView ringtoneLabel;
        private RadioButton checker;

        public RingtoneHolder(View v) {
            super(v);
            ringtoneLabel = (TextView) v.findViewById(R.id.ring_text);
            checker = (RadioButton) v.findViewById(R.id.ring_button);
            checker.setOnClickListener(this);
        }

        public void setRingtoneLabel(String ringtoneLabel) {
            this.ringtoneLabel.setText(ringtoneLabel.replace("_", " "));
        }

        public void setChecker(boolean checked) {
            this.checker.setChecked(checked);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != checkPosition) {
                ringtones.get(checkPosition).setSelected(false);
                notifyItemChanged(checkPosition);
                checkPosition = getAdapterPosition();
                ringtones.get(checkPosition).setSelected(true);
                selectedTone = ringtones.get(checkPosition);
            }

            if (!AlarmService.isActive)
                MediaPlayerUtils.play(ringtoneActivity, ringtones.get(checkPosition).getResId(), false);
            else {
                Toast.makeText(ringtoneActivity, "Please turn off the alarm first", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
