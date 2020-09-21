package com.alarm.android.alarmplus.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerUtils {
    private static MediaPlayer player;
    private MediaPlayerUtils() {
    }

    public static void play(Context ctx, int resId, boolean setLooping) {
        stop();     //if already playing
        player = MediaPlayer.create(ctx, resId);
        player.setLooping(setLooping);
        player.start();
    }

    public static void stop() {
        if(player!=null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
