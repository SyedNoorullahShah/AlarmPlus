package com.alarm.android.alarmplus.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AlarmObject extends RealmObject {
    @PrimaryKey
    private long currentTime;
    private long triggerTime;
    private boolean isActive;
    private String label;
    private AlarmModeObject mode;
    private int ringId;
    private boolean willSnooze;
    private boolean trackAlarm;

    private int snooze = 1;
    private long snoozeTime;
    private long snoozeId;

    public AlarmObject() {
    }

    public AlarmObject(long currentTime, long triggerTime, boolean isActive, String label, AlarmModeObject mode, int ringId, boolean willSnooze, boolean trackAlarm) {
        this.currentTime = currentTime;
        this.triggerTime = triggerTime;
        this.isActive = isActive;
        this.label = label;
        this.mode = mode;
        this.ringId = ringId;
        this.willSnooze = willSnooze;
        this.trackAlarm = trackAlarm;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public int getSnooze() {
        return snooze;
    }

    public void incrementSnooze() {
        snooze++;
    }

    public void resetSnooze() {
        snooze = 1;
    }

    public long getSnoozeTime() {
        return snoozeTime;
    }

    public void setSnoozeTime(long snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    public long getSnoozeId() {
        return snoozeId;
    }

    public void setSnoozeId(long snoozeId) {
        this.snoozeId = snoozeId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setTriggerTime(long timeInMillis) {
        triggerTime = timeInMillis;
    }

    public boolean getActive() {
        return isActive;
    }

    public String getLabel() {
        return label;
    }

    public AlarmModeObject getMode() {
        return mode;
    }

    public int getRingId() {
        return ringId;
    }

    public boolean willSnooze() {
        return willSnooze;
    }

    public boolean isTrackAlarm() {
        return trackAlarm;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setRingId(int ringId) {
        this.ringId = ringId;
    }

    public void isSnooze(boolean willSnooze) {
        this.willSnooze = willSnooze;
    }

    public void setTrackAlarm(boolean trackAlarm) {
        this.trackAlarm = trackAlarm;
    }
}
