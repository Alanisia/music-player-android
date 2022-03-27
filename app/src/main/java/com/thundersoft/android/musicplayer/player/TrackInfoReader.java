package com.thundersoft.android.musicplayer.player;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.core.app.ActivityCompat;

import com.thundersoft.android.musicplayer.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class TrackInfoReader {
    private static final String TAG = TrackInfoReader.class.getSimpleName();

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

//            int albumId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
//            Bitmap albumPicture = getAlbumArt(context, albumId);

            Track track = new Track().setId(id) // .setImage(albumPicture)
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

    @Deprecated
    private static Bitmap getAlbumArt(Context context, int albumId) {
        Uri albumUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId);
        try {
            return context.getContentResolver().loadThumbnail(albumUri, new Size(50, 50), null);
        } catch (IOException e) {
            return null;
        }
    }


    private static long getTrackMinutes(long millionSeconds) {
        return millionSeconds / 1000 / 60;
    }

    private static long getTrackSeconds(long millionSeconds) {
        return millionSeconds / 1000 % 60;
    }
}
