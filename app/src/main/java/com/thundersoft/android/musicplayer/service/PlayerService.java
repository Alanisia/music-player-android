package com.thundersoft.android.musicplayer.service;

import android.app.Service;
import android.content.Context;
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

import java.io.IOException;

public class PlayerService extends Service {
    private static final String TAG = PlayerService.class.getSimpleName();
    private final Player player = Player.getInstance();
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
        private Context context;

        public void play() {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(new AudioAttributes.
                        Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            } else reset();

            try {
                Track track = player.current();
                mediaPlayer.setDataSource(context, track.getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(p -> sendBroadcast(new Intent(Constants.ACTION_PLAY_COMPLETE)));
            mediaPlayer.setOnErrorListener((p, what, extra) -> {
                Toast.makeText(context, "Error occurred!", Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        public boolean control() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    player.setPlaying(false);
                    return false;
                } else {
                    mediaPlayer.start();
                    player.setPlaying(true);
                    return true;
                }
            } else {
                play();
                player.setPlaying(true);
                return true;
            }
        }

        public void next(boolean over) {
            if (over) player.nextOverPlaying();
            else player.next();
            play();
            player.setPlaying(true);
        }

        public void previous() {
            player.previous();
            play();
            player.setPlaying(true);
        }

        public void reset() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }

        public PlayerBinder setContext(Context context) {
            this.context = context;
            return this;
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }
    }
}
