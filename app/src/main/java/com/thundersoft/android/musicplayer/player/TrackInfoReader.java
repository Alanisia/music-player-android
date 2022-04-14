package com.thundersoft.android.musicplayer.player;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.thundersoft.android.musicplayer.util.Utils;

import java.util.ArrayList;
import java.util.List;

public final class TrackInfoReader {
    private static final String TAG = TrackInfoReader.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static List<Track> read(Context context) {
        Uri contentURI = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ?
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) :
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "read: " + contentURI.getPath());
        Cursor cursor = context.getContentResolver().query(contentURI,
                new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM_ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ALBUM_ID
                }, null, null, null);
        List<Track> tracks = new ArrayList<>();
        Log.d(TAG, "read: " + cursor.getCount());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            long minutes = getTrackMinutes(duration), seconds = getTrackSeconds(duration);

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

            Track track = new Track().setId(id)
                    .setPath(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id))
                    .setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)))
                    .setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)))
                    .setMinutes((int) minutes).setSeconds((int) seconds).setDuration(Utils.getDuration(minutes, seconds));
            if (minutes >= 1)
                tracks.add(track);
        }
        cursor.close();
        return tracks;
    }

    private static long getTrackMinutes(long millionSeconds) {
        return millionSeconds / 1000 / 60;
    }

    private static long getTrackSeconds(long millionSeconds) {
        return millionSeconds / 1000 % 60;
    }
}
