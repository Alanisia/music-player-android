package com.thundersoft.android.musicplayer.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import static android.content.Context.BIND_AUTO_CREATE;

public final class Utils {
    public static String getProgress(int currentTime) {
        int minutes = currentTime / 60;
        int seconds = currentTime % 60;
        return getDuration(minutes, seconds);
    }

    public static String getDuration(long minutes, long seconds) {
        StringBuilder builder = new StringBuilder();
        if (minutes == 0) builder.append("00");
        else if (minutes >= 10) builder.append(minutes);
        else builder.append('0').append(minutes);
        builder.append(':');
        if (seconds == 0) builder.append("00");
        else if (seconds >= 10) builder.append(seconds);
        else builder.append('0').append(seconds);
        return builder.toString();
    }

    public static void bindService(Context context, Class<? extends Service> serviceClass,
                                   ServiceConnection serviceConnection) {
        Intent serviceIntent = new Intent(context, serviceClass);
        context.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }
}
