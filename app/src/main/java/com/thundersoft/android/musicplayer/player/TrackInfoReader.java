package com.thundersoft.android.musicplayer.player;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TrackInfoReader {
    private static final String TAG = "TrackInfoReader";

    public static List<Track> read(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST_KEY, MediaStore.Audio.Media.DURATION},
                null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Track> tracks = new ArrayList<>();
        if (cursor == null) return tracks;
        if (cursor.moveToFirst()) {
            do {
                tracks.add(new Track()
                        .setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)))
                        .setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_KEY))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tracks;
    }

    public static List<Track> read(Context context, String path) {
//        StringBuilder builder = new StringBuilder();
//        File externalStorageDir = context.getExternalFilesDir(null);
//        do {
//            externalStorageDir = externalStorageDir.getParentFile();
//        } while (externalStorageDir.getAbsolutePath().contains("/Android"));
//        String tracksPath = builder.append(externalStorageDir.getAbsolutePath()).append(path).toString();
//        Log.d(TAG, "read: " + tracksPath);
//        List<Track> tracks = new ArrayList<>();
//        File dir = new File(tracksPath);
//        if (dir.isDirectory()) {
//            Log.d(TAG, "read: " + dir.getAbsolutePath());
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            File[] files = dir.listFiles();
//            assert files != null;
//            Arrays.stream(files).skip(1).forEach(e -> {
//                retriever.setDataSource(e.getAbsolutePath());
//                int minutes = getTrackMinutes(Integer.parseInt(
//                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
//                int seconds = getTrackSeconds(Integer.parseInt(
//                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
//                byte[] embeddedPicture = retriever.getEmbeddedPicture();
//                tracks.add(new Track().setPath(e.getPath())
//                        .setTitle(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
//                        .setArtist(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST))
//                        .setDuration(getDuration(minutes, seconds)).setMinutes(minutes).setSeconds(seconds)
//                        .setImage(BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length)));
//            });
//        }
//        return tracks;
        return null;
    }

    private static int getTrackMinutes(int millionSeconds) {
        return millionSeconds / 1000 / 60;
    }

    private static int getTrackSeconds(int millionSeconds) {
        return millionSeconds / 1000 % 60;
    }

    private static String getDuration(int minutes, int seconds) {
        StringBuilder builder = new StringBuilder();
        if (minutes == 0) builder.append("00");
        else if (minutes >= 10) builder.append(minutes);
        else builder.append('0').append(minutes);
        if (seconds == 0) builder.append("00");
        else if (seconds >= 10) builder.append(seconds);
        else builder.append('0').append(seconds);
        return builder.toString();
    }
}
