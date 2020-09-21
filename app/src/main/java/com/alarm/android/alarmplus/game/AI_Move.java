package com.alarm.android.alarmplus.game;

public class AI_Move {
    private int x;
    private int y;
    private int score;

    public AI_Move() {
    }

    public AI_Move(int score) {
        this.score = score;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getScore() {
        return score;
    }
}
