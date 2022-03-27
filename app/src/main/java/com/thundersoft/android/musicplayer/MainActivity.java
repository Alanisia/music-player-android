package com.thundersoft.android.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.player.Track;
import com.thundersoft.android.musicplayer.player.TrackInfoReader;
import com.thundersoft.android.musicplayer.service.PlayerService;
import com.thundersoft.android.musicplayer.service.PlayerServiceConnection;
import com.thundersoft.android.musicplayer.util.Constants;
import com.thundersoft.android.musicplayer.util.Utils;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_CODE = 1;
    private final Player player = Player.getInstance();
    private PlayerServiceConnection serviceConnection;
    private List<Track> tracks;
    private final ViewHolder viewHolder = new ViewHolder();

    static class TrackListAdaptor extends ArrayAdapter<Track> {
        private List<Track> tracks;
        private final int resourceId;

        public TrackListAdaptor(@NonNull Context context, int resource, List<Track> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.trackImage = convertView.findViewById(R.id.track_image);
            viewHolder.trackTitle = convertView.findViewById(R.id.track_title);
            viewHolder.trackArtist = convertView.findViewById(R.id.track_artist);
            viewHolder.trackDuration = convertView.findViewById(R.id.track_duration);
            Track track = tracks.get(position);
            if (track.getImage() != null) viewHolder.trackImage.setImageBitmap(track.getImage());
            viewHolder.trackTitle.setText(track.getTitle());
            viewHolder.trackArtist.setText(track.getArtist());
            viewHolder.trackDuration.setText(track.getDuration());
            return convertView;
        }

        private static class ViewHolder {
            ImageView trackImage;
            TextView trackTitle;
            TextView trackArtist;
            TextView trackDuration;
        }

        public TrackListAdaptor setTracks(List<Track> tracks) {
            this.tracks = tracks;
            return this;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind service
        serviceConnection = PlayerServiceConnection.getInstance();
        serviceConnection.setContext(getApplication());
        Utils.bindService(getApplication(), PlayerService.class, serviceConnection);

        int hasReadStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasReadStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_CODE);
        } else mainLogic();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                mainLogic();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        player.setPlayList(tracks);
        player.setCurrent(position);
        Track track = tracks.get(position);
        Log.d(TAG, "onItemClick: " + track);
        startActivityWithCurrentTrack(track);
    }

    private void mainLogic() {
        tracks = TrackInfoReader.read(this);

        viewHolder.lvTrackList = findViewById(R.id.track_list);
        viewHolder.lvTrackList.setAdapter(new TrackListAdaptor(this, R.layout.layout_track_item, tracks).setTracks(tracks));
        viewHolder.lvTrackList.setOnItemClickListener(this);

        viewHolder.etSearchTrack = findViewById(R.id.search_track);
        String keyword = viewHolder.etSearchTrack.getText().toString();

        viewHolder.clPlayingBottomNav = findViewById(R.id.playing_bottom_nav);
        viewHolder.clPlayingBottomNav.setOnClickListener(v -> startActivityWithCurrentTrack(player.current()));

        viewHolder.tvPlayingTitle = findViewById(R.id.playing_title);
        viewHolder.tvPlayingTitle.setText(player.current() == null ? "" : player.current().getTitle());

        viewHolder.ibControl = findViewById(R.id.playing_control);
        setIbControlDrawable();

        viewHolder.ibControl.setOnClickListener(v -> {
            if (player.current() != null) {
                serviceConnection.getBinder().control();
                setIbControlDrawable();
            } else {
                Toast.makeText(this, "No track in play list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setIbControlDrawable() {
        viewHolder.ibControl.setImageResource(player.playing() ?
                R.drawable.ic_baseline_pause_circle_outline_24 :
                R.drawable.ic_baseline_play_circle_outline_24
        );
    }

    private void startActivityWithCurrentTrack(Track track) {
        if (track != null) {
            Intent intent = new Intent(this, PlayActivity.class);
            intent.putExtra(Constants.TRACK_TITLE, track.getTitle());
            intent.putExtra(Constants.TRACK_ARTIST, track.getArtist());
            intent.putExtra(Constants.TRACK_DURATION, track.getDuration());
            startActivityForResult(intent, Constants.MAIN_INTENT_REQUEST);
        } else Toast.makeText(this, "No track in play list", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.MAIN_INTENT_REQUEST) {
            if (resultCode == Constants.PLAYER_RETURN_RESULT) {
                viewHolder.tvPlayingTitle.setText(player.current().getTitle());
                setIbControlDrawable();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.exit(0);
        return false;
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private static class ViewHolder {
        EditText etSearchTrack;
        ListView lvTrackList;
        ConstraintLayout clPlayingBottomNav;
        TextView tvPlayingTitle;
        ImageButton ibControl;
    }
}