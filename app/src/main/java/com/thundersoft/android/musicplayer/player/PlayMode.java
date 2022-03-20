package com.thundersoft.android.musicplayer.player;

import com.thundersoft.android.musicplayer.R;

public enum PlayMode {
    SEQUENCE(R.drawable.ic_baseline_replay_24),
    SHUFFLE(R.drawable.ic_baseline_shuffle_24),
    SINGLE(R.drawable.ic_baseline_repeat_one_24);

    private final int uri;

    PlayMode(int uri) {
        this.uri = uri;
    }

    public int getUri() {
        return uri;
    }
}
