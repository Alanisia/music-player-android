package com.thundersoft.android.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.ListView;
import android.widget.TextView;

import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.player.Track;
import com.thundersoft.android.musicplayer.player.TrackInfoReader;
import com.thundersoft.android.musicplayer.service.PlayerService;
import com.thundersoft.android.musicplayer.service.PlayerServiceConnection;
import com.thundersoft.android.musicplayer.service.SIPlayerServiceConnection;
import com.thundersoft.android.musicplayer.util.Constants;
import com.thundersoft.android.musicplayer.util.Utils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_CODE = 1;
    private final Player player = Player.getInstance();
    private SIPlayerServiceConnection serviceConnection;
    private List<Track> tracks;

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
            viewHolder.trackTitle = convertView.findViewById(R.id.track_title);
            viewHolder.trackArtist = convertView.findViewById(R.id.track_artist);
            viewHolder.trackDuration = convertView.findViewById(R.id.track_duration);
            Track track = tracks.get(position);
            viewHolder.trackTitle.setText(track.getTitle());
            viewHolder.trackArtist.setText(track.getArtist());
            viewHolder.trackDuration.setText(track.getDuration());
            return convertView;
        }

        private static class ViewHolder {
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
        // serviceConnection = new PlayerServiceConnection().setContext(getApplication());
        serviceConnection = SIPlayerServiceConnection.getInstance();
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
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(Constants.TRACK_TITLE, track.getTitle());
        intent.putExtra(Constants.TRACK_ARTIST, track.getArtist());
        intent.putExtra(Constants.TRACK_DURATION, track.getDuration());
        startActivity(intent);
    }

    private void mainLogic() {
        tracks = TrackInfoReader.read(this);
        ListView trackListView = findViewById(R.id.track_list);
        trackListView.setAdapter(new TrackListAdaptor(this, R.layout.layout_track_item, tracks).setTracks(tracks));
        trackListView.setOnItemClickListener(this);
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
}