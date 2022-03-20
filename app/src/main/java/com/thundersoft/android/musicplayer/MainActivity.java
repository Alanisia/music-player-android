package com.thundersoft.android.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thundersoft.android.musicplayer.player.Track;
import com.thundersoft.android.musicplayer.player.TrackInfoReader;

import java.util.List;

public class MainActivity extends AppCompatActivity {
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
        tracks = TrackInfoReader.read(this);
//        tracks = TrackInfoReader.read(this, "/Download");
        ListView trackListView = findViewById(R.id.track_list);
        trackListView.setAdapter(new TrackListAdaptor(this, R.layout.layout_track_item, this.tracks).setTracks(tracks));
        trackListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, PlayActivity.class);

            startActivity(intent);
        });
    }
}