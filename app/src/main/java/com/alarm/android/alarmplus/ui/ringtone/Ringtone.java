package com.alarm.android.alarmplus.ui.ringtone;

public class Ringtone {
    private String name;
    private int resId;
    private boolean isSelected;

    public Ringtone(String name, int resId){
        this.name = name;
        this.resId = resId;
        isSelected = false;
    }

    public String getName() {
        return name;
    }

    public int getResId() {
        return resId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
