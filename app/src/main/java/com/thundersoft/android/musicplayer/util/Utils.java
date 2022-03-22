package com.thundersoft.android.musicplayer.util;

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
}
