package com.thundersoft.android.musicplayer.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.player.Track;
import com.thundersoft.android.musicplayer.server.IPlayerAidlInterface;
import com.thundersoft.android.musicplayer.util.Constants;

import java.io.IOException;

public class PlayerService extends Service {
    private final Player player = Player.getInstance();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private final Context self = this;

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

    public class PlayerBinder extends IPlayerAidlInterface.Stub {
        private Context context = self;
        private boolean initialized = false;

        @Override
        public void play() {
            mediaPlayer.setAudioAttributes(new AudioAttributes.
                    Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());

            if (initialized) reset();

            initialized = true;

            try {
                Track current = player.current();
                mediaPlayer.setDataSource(context, current.getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(p -> {
                p.start();
                player.setPlaying(true);
            });
            mediaPlayer.setOnCompletionListener(p -> sendBroadcast(new Intent(Constants.ACTION_PLAY_COMPLETE)));
            mediaPlayer.setOnErrorListener((p, what, extra) -> {
                Toast.makeText(context, "Error occurred! Error code: " + what, Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        @Override
        public boolean control() {
            if (initialized) {
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

        @Override
        public void next(boolean over) {
            if (over) player.nextOverPlaying();
            else player.next();
            play();
            player.setPlaying(true);
        }

        @Override
        public void previous() {
            player.previous();
            play();
            player.setPlaying(true);
        }

        @Override
        public void seekTo(int second) {
            mediaPlayer.seekTo(second);
        }

        @Override
        public void reset() {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
        }

        public PlayerBinder setContext(Context context) {
            this.context = context;
            return this;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }
    }
}
