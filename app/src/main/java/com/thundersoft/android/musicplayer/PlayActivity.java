package com.thundersoft.android.musicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thundersoft.android.musicplayer.player.PlayMode;
import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.player.Track;
import com.thundersoft.android.musicplayer.service.PlayerService;
import com.thundersoft.android.musicplayer.util.Constants;
import com.thundersoft.android.musicplayer.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PlayActivity.class.getSimpleName();
    private final ViewHolder viewHolder = new ViewHolder();
    private final Player player = Player.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private PlayerBroadcastReceiver receiver;

    private ServiceConnection serviceConnection;
    private PlayerService.PlayerBinder binder;
    private int currentTime = 0;

    // false when is not playing and touching seek bar
    // this is for controlling timer, default false
    private boolean timing = false;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // bind service
        serviceConnection = new PlayerServiceConnection();
        Intent serviceIntent = new Intent(this, PlayerService.class);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

        // get views
        viewHolder.albumImage = findViewById(R.id.album_image);
        viewHolder.title = findViewById(R.id.play_title);
        viewHolder.artist = findViewById(R.id.play_artist);
        viewHolder.currentTime = findViewById(R.id.current_time);
        viewHolder.duration = findViewById(R.id.duration);
        viewHolder.timeBar = findViewById(R.id.time_bar);
        viewHolder.playMode = findViewById(R.id.play_mode);
        viewHolder.playOrPause = findViewById(R.id.play_or_pause);

        setModeImageResource();

        Intent intent = getIntent();
        viewHolder.title.setText(intent.getStringExtra(Constants.TRACK_TITLE));
        viewHolder.artist.setText(intent.getStringExtra(Constants.TRACK_ARTIST));
        viewHolder.duration.setText(intent.getStringExtra(Constants.TRACK_DURATION));

        viewHolder.timeBar.setMax(player.current().getMinutes() * 60 + player.current().getSeconds());
        viewHolder.timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewHolder.currentTime.setText(Utils.getProgress(currentTime));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                timing = false; // stop timing when touching seek bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player.playing()) {
                    currentTime = seekBar.getProgress();
                    binder.getMediaPlayer().seekTo(currentTime * 1000);
                    viewHolder.currentTime.setText(Utils.getProgress(currentTime));

                    // start timing
                    timing = true;
                    setTimer();
                }
            }
        });

        setTimer();
        if (player.playing()) {
            timing = true;
            viewHolder.playOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register broadcast
        receiver = new PlayerBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_PLAY_COMPLETE);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.album_image:
                Intent intent = new Intent(this, AlbumPictureActivity.class);
                break;
            case R.id.play_mode:
                changeMode();
                break;
            case R.id.previous_track:
                previous();
                break;
            case R.id.next_track:
                next(false);
                break;
            case R.id.play_list:
                // create fragment
                // show play list
                // wait for clicking
                // modify player
                // modify data in this activity
                // TODO
                break;
            case R.id.play_or_pause:
                control();
                break;
            default: break;
        }
    }

    private void previous() {
        binder.previous();
        currentTime = 0;
        viewHolder.timeBar.setProgress(currentTime);
        timing = true;
        setTimer();
    }

    private void next(boolean over) {
        binder.next(over);
        currentTime = 0;
        viewHolder.timeBar.setProgress(currentTime);
        timing = true;
        setTimer();
    }

    private void changeMode() {
        player.changeMode();
        setModeImageResource();
    }

    private void control() {
        if (binder.control()) {
            viewHolder.playOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
            timing = true;
        } else {
            viewHolder.playOrPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
            timing = false;
        }
    }

    private void setTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timing) {
                    currentTime += 1;
                    handler.post(() -> {
                        viewHolder.timeBar.setProgress(currentTime);
                        viewHolder.currentTime.setText(Utils.getProgress(currentTime));
                    });
                }
            }
        }, 0, 1000);
    }

    private void setModeImageResource() {
        switch (player.getCurrentMode()) {
            case SINGLE:
                viewHolder.playMode.setImageResource(PlayMode.SINGLE.getUri());
                break;
            case SEQUENCE:
                viewHolder.playMode.setImageResource(PlayMode.SEQUENCE.getUri());
                break;
            case SHUFFLE:
                viewHolder.playMode.setImageResource(PlayMode.SHUFFLE.getUri());
                break;
            default: break;
        }
    }

    private static class ViewHolder {
        ImageView albumImage;
        TextView title, artist;
        TextView currentTime, duration;
        SeekBar timeBar;
        ImageButton playMode, playOrPause;
    }

    private class PlayerServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayerService.PlayerBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_PLAY_COMPLETE)) {
                next(true);
            }
        }
    }
}