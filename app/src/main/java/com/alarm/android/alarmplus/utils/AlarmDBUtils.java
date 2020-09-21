package com.alarm.android.alarmplus.utils;

import com.alarm.android.alarmplus.database.AlarmObject;
import com.alarm.android.alarmplus.database.AlarmModeObject;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AlarmDBUtils {
    private static AlarmObject activeAlarmObject;

    private AlarmDBUtils() {

    }

    public static RealmResults<AlarmObject> getAlarms() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(AlarmObject.class)
                .sort("triggerTime", Sort.ASCENDING)
                .findAllAsync();
    }


    public static AlarmObject getTodayAlarms(long startOfDay, long endOfDay) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(AlarmObject.class)
                .greaterThanOrEqualTo("triggerTime", startOfDay)
                .and()
                .lessThanOrEqualTo("triggerTime", endOfDay)
                .equalTo("trackAlarm", true)
                .findFirst();
    }

    public static void insertAlarm(long currentTime, long triggerTime, String label, String mode, String difficulty, int rounds, int steps,
                                   int ringId, boolean willSnooze, boolean trackAlarm) {
        Realm realm = Realm.getDefaultInstance();
        AlarmModeObject gameMode = getAlarmMode(mode, difficulty, rounds, steps);
        AlarmObject alarmObject = new AlarmObject(currentTime, triggerTime, true, label, gameMode, ringId, willSnooze, trackAlarm);
        realm.beginTransaction();
        realm.copyToRealm(alarmObject);
        realm.commitTransaction();
        realm.close();
    }

    public static void updateAlarm(long timeId, long triggerTime, String label, String mode, String difficulty, int rounds, int steps,
                                   int ringId, boolean willSnooze, boolean trackAlarm) {
        Realm realm = Realm.getDefaultInstance();
        AlarmObject updateAlarmObject = realm.where(AlarmObject.class)
                .equalTo("currentTime", timeId)
                .findFirstAsync();

        realm.beginTransaction();
        updateAlarmObject.setTriggerTime(triggerTime);
        updateAlarmObject.setActive(true);
        updateAlarmObject.resetSnooze();
        updateAlarmObject.setSnoozeTime(0);
        updateAlarmObject.setSnoozeId(0);
        updateAlarmObject.setLabel(label);
        updateAlarmObject.getMode().setMode(mode);
        updateAlarmObject.getMode().setDifficulty(difficulty);
        updateAlarmObject.getMode().setRounds(rounds);
        updateAlarmObject.getMode().setSteps(steps);
        updateAlarmObject.setRingId(ringId);
        updateAlarmObject.isSnooze(willSnooze);
        updateAlarmObject.setTrackAlarm(trackAlarm);
        realm.commitTransaction();
    }

    private static AlarmModeObject getAlarmMode(String mode, String difficulty, int rounds, int steps) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        AlarmModeObject alarmMode = realm.createObject(AlarmModeObject.class, UUID.randomUUID().toString());
        alarmMode.setMode(mode);
        alarmMode.setDifficulty(difficulty);
        alarmMode.setRounds(rounds);
        alarmMode.setSteps(steps);
        realm.commitTransaction();
        return alarmMode;
    }

    public static void deleteAlarm(AlarmObject alarmObject) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        alarmObject.getMode().deleteFromRealm();
        alarmObject.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }


    public static void updateSnooze(AlarmObject currentAlarmObject, long snoozeTime, long snoozeId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        currentAlarmObject.incrementSnooze();
        currentAlarmObject.setSnoozeTime(snoozeTime);
        currentAlarmObject.setSnoozeId(snoozeId);
        realm.commitTransaction();

    }

    public static AlarmObject getPreviousAlarm(long currentTime) {
        Realm realm = Realm.getDefaultInstance();
        AlarmObject alarmObject = realm.where(AlarmObject.class)
                .lessThan("triggerTime", currentTime)
                .sort("triggerTime", Sort.DESCENDING)
                .findFirst();

        return alarmObject;
    }

    public static void resetState(AlarmObject currentAlarmObject) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        currentAlarmObject.setActive(false);
        currentAlarmObject.resetSnooze();
        currentAlarmObject.setSnoozeTime(0);
        currentAlarmObject.setSnoozeId(0);
        realm.commitTransaction();

    }

    public static void setActiveAlarm(long alarmId, String alarmType) {
        Realm realm = Realm.getDefaultInstance();
        if (alarmType.equals("main")) {
            activeAlarmObject = realm.where(AlarmObject.class)
                    .equalTo("currentTime", alarmId)
                    .findFirst();
        } else if (alarmType.equals("snooze")) {
            activeAlarmObject = realm.where(AlarmObject.class)
                    .equalTo("snoozeId", alarmId)
                    .findFirst();
        }
    }

    public static AlarmObject getActiveAlarm() {
        return activeAlarmObject;
    }


}
