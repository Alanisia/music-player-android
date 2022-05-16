package com.thundersoft.android.musicplayer.util;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;

import static android.content.Context.BIND_AUTO_CREATE;

public final class Utils {
    private static final Gson gson = new Gson();
    private static final Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();

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

    public static void bindService(Context context, ServiceConnection serviceConnection,
                                   String action, String packageName) {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(action);
        serviceIntent.setPackage(packageName);
        context.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    public static byte[] drawable2bytes(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return out.toByteArray();
        } else return null;
    }

    public static String json(Object value, boolean pretty) {
        return pretty ? gsonPretty.toJson(value) : gson.toJson(value);
    }
}
