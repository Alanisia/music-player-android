package com.thundersoft.android.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.thundersoft.android.musicplayer.player.PlayMode;
import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.player.Track;

import java.io.IOException;

public class PlayerService extends Service {
    private static final String TAG = "PlayerService";
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public class PlayerBinder extends Binder {
        public void play() {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.
                        Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            } else reset();

            // todo
            Track track = Player.getCurrentTrack();

            try {
                mediaPlayer.setDataSource(track.getPath());
            } catch (IOException e) {
//                Log.e(TAG, "play: " + );
                e.printStackTrace();
            }

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(p -> {
                p.start();
                Player.setPlaying(true);
            });
            mediaPlayer.setOnErrorListener((p, what, extra) -> {
                Toast.makeText(PlayerService.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                return true;
            });
            mediaPlayer.setOnCompletionListener(p -> {
                if (!mediaPlayer.isLooping()) Player.setPlaying(false);

            });

        }

        public void control() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Player.setPlaying(false);
                } else {
                    mediaPlayer.start();
                    Player.setPlaying(true);
                }
            }
        }

        public void next() {
            Player.next();
        }

        public void previous() {
            Player.previous();
        }

        public void reset() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
    }
}
