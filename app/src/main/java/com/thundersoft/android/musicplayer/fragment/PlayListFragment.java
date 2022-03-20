package com.thundersoft.android.musicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thundersoft.android.musicplayer.R;
import com.thundersoft.android.musicplayer.player.Player;
import com.thundersoft.android.musicplayer.player.Track;

import java.util.List;

public class PlayListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        ListView listView = view.findViewById(R.id.play_list_view);
        listView.setAdapter(new PlayListAdaptor(getContext(), R.layout.layout_play_list_item, Player.getPlayList())
                .setPlayList(Player.getPlayList()));
        listView.setOnItemClickListener(((parent, view1, position, id) -> {

        }));
        return view;
    }

    static class PlayListAdaptor extends ArrayAdapter<Track> {
        private final int resourceId;
        private List<Track> playList;

        public PlayListAdaptor(@NonNull Context context, int resource, @NonNull List<Track> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.itemTitle = convertView.findViewById(R.id.item_title);
            viewHolder.itemArtist = convertView.findViewById(R.id.item_artist);
            viewHolder.remove = convertView.findViewById(R.id.remove);
            Track track = playList.get(position);
            viewHolder.itemTitle.setText(track.getTitle());
            viewHolder.itemArtist.setText(track.getArtist());
            viewHolder.remove.setOnClickListener(v -> {
                Player.remove(track);
                notifyDataSetChanged();
            });
            return convertView;
        }

        public PlayListAdaptor setPlayList(List<Track> playList) {
            this.playList = playList;
            return this;
        }

        static class ViewHolder {
            TextView itemTitle;
            TextView itemArtist;
            ImageButton remove;
        }
    }

}
