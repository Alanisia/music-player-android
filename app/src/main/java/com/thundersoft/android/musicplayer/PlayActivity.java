package com.thundersoft.android.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.thundersoft.android.musicplayer.player.PlayMode;
import com.thundersoft.android.musicplayer.player.Player;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private final ViewHolder viewHolder = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        viewHolder.albumImage = findViewById(R.id.album_image);
        viewHolder.title = findViewById(R.id.play_title);
        viewHolder.artist = findViewById(R.id.play_artist);
        viewHolder.currentTime = findViewById(R.id.current_time);
        viewHolder.duration = findViewById(R.id.duration);
        viewHolder.timeBar = findViewById(R.id.time_bar);
        viewHolder.playMode = findViewById(R.id.play_mode);
        viewHolder.previousTrack = findViewById(R.id.previous_track);
        viewHolder.playOrPause = findViewById(R.id.play_or_pause);
        viewHolder.nextTrack = findViewById(R.id.next_track);
        viewHolder.playList = findViewById(R.id.play_list);

        Intent intent = getIntent();

        viewHolder.timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.album_image:break;
            case R.id.play_mode:
                Player.changeMode();
                switch (Player.getCurrentMode()) {
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
                break;
            case R.id.previous_track:
                Player.previous();
                break;
            case R.id.next_track:
                Player.next();
                break;
            case R.id.play_list:
                // create fragment
                // show play list
                // wait for clicking
                // modify player
                // modify data in this activity

                break;
            case R.id.play_or_pause:
                break;
            default: break;
        }
    }

    static class ViewHolder {
        ImageView albumImage;
        TextView title, artist;
        TextView currentTime, duration;
        SeekBar timeBar;
        ImageButton playMode, previousTrack, playOrPause, nextTrack, playList;
    }
}