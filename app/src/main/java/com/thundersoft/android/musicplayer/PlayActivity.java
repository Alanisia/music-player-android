package com.thundersoft.android.musicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thundersoft.android.musicplayer.player.PlayMode;
import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.service.PlayerService;
import com.thundersoft.android.musicplayer.service.PlayerServiceConnection;
import com.thundersoft.android.musicplayer.util.Constants;
import com.thundersoft.android.musicplayer.util.Utils;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PlayActivity.class.getSimpleName();
    private final ViewHolder viewHolder = new ViewHolder();
    private final PlayerServiceConnection serviceConnection = PlayerServiceConnection.getInstance();
    private final Player player = Player.getInstance();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private PlayerBroadcastReceiver receiver;
    private int currentTime = 0;

    // false when is not playing and touching seek bar
    // this is for controlling timer, default false
    private boolean timing = false;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // get views
        viewHolder.albumImage = findViewById(R.id.album_image);
        viewHolder.title = findViewById(R.id.play_title);
        viewHolder.artist = findViewById(R.id.play_artist);
        viewHolder.currentTime = findViewById(R.id.current_time);
        viewHolder.duration = findViewById(R.id.duration);
        viewHolder.timeBar = findViewById(R.id.time_bar);
        viewHolder.playMode = findViewById(R.id.play_mode);
        viewHolder.playOrPause = findViewById(R.id.play_or_pause);
        viewHolder.previousTrack = findViewById(R.id.previous_track);
        viewHolder.nextTrack = findViewById(R.id.next_track);
        viewHolder.playList = findViewById(R.id.play_list);

        viewHolder.albumImage.setOnClickListener(this);
        viewHolder.playMode.setOnClickListener(this);
        viewHolder.playOrPause.setOnClickListener(this);
        viewHolder.previousTrack.setOnClickListener(this);
        viewHolder.nextTrack.setOnClickListener(this);
        viewHolder.playList.setOnClickListener(this);

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
                PlayerService.PlayerBinder binder = serviceConnection.getBinder();
                currentTime = seekBar.getProgress();
                binder.getMediaPlayer().seekTo(currentTime * 1000);
                viewHolder.currentTime.setText(Utils.getProgress(currentTime));
                if (player.playing()) {
                    // start timing
                    timing = true;
                    setTimer();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set timer
        MediaPlayer mediaPlayer = serviceConnection.getBinder().getMediaPlayer();
        currentTime = mediaPlayer != null ? mediaPlayer.getCurrentPosition() / 1000 : 0;
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
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) setResult(Constants.PLAYER_RETURN_RESULT);
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.album_image:
                Intent intent = new Intent(this, AlbumPictureActivity.class);
                intent.putExtra(Constants.ALBUM_ART, new byte[1024]); // TODO
                startActivity(intent);
                break;
            case R.id.play_mode:
                Log.d(TAG, "onClick: change mode");
                changeMode();
                break;
            case R.id.previous_track:
                Log.d(TAG, "onClick: previous track");
                previous();
                break;
            case R.id.next_track:
                Log.d(TAG, "onClick: next track");
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
                Log.d(TAG, "onClick: play or pause");
                control();
                break;
            default: break;
        }
    }

    private void reInitPlayer() {
        currentTime = 0;
        viewHolder.title.setText(player.current().getTitle());
        viewHolder.artist.setText(player.current().getArtist());
        viewHolder.duration.setText(player.current().getDuration());
        viewHolder.timeBar.setProgress(currentTime);
        timing = true;
        setTimer();
        viewHolder.playOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
    }

    private void previous() {
        PlayerService.PlayerBinder binder = serviceConnection.getBinder();
        binder.previous();
        reInitPlayer();
    }

    private void next(boolean over) {
        PlayerService.PlayerBinder binder = serviceConnection.getBinder();
        binder.next(over);
        reInitPlayer();
    }

    private void changeMode() {
        player.changeMode();
        setModeImageResource();
    }

    private void control() {
        PlayerService.PlayerBinder binder = serviceConnection.getBinder();
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
        ImageButton playMode, playOrPause, previousTrack, nextTrack, playList;
    }

    private class PlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.ACTION_PLAY_COMPLETE))
                next(true);
        }
    }
}