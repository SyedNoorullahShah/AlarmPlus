package com.alarm.android.alarmplus;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WakeMeUpApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if(LeakCanary.isInAnalyzerProcess(this)){
            return;
        }LeakCanary.install(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("wake-me-up.realm").build();
        Realm.setDefaultConfiguration(config);
    }
}
