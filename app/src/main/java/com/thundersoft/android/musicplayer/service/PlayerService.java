package com.thundersoft.android.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.player.Track;
import com.thundersoft.android.musicplayer.util.Constants;

public class PlayerService extends Service {
    private static final String TAG = PlayerService.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private Player player;

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

            Track track = player.current();
            // TODO: set data source

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(p -> sendBroadcast(new Intent(Constants.ACTION_PLAY_COMPLETE)));
            mediaPlayer.setOnErrorListener((p, what, extra) -> {
                Toast.makeText(PlayerService.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        public boolean control() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    return false;
                } else {
                    mediaPlayer.start();
                    return true;
                }
            } else {
                play();
                return true;
            }
        }

        public void next(boolean over) {
            if (over) player.nextOverPlaying();
            else player.next();
            play();
        }

        public void previous() {
            player.previous();
            play();
        }

        public void reset() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }
    }
}
