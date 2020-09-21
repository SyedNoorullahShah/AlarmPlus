package com.alarm.android.alarmplus.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AlarmModeObject extends RealmObject {
    @PrimaryKey
    private String id;
    private String mode;
    private String difficulty;
    private int rounds;
    private int steps;


    public String getId() {
        return id;
    }

    public String getMode() {
        return mode;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getRounds() {
        return rounds;
    }

    public int getSteps() {
        return steps;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
