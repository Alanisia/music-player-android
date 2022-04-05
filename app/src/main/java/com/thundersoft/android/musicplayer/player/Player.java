package com.thundersoft.android.musicplayer.player;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class Player {
    private static final String TAG = Player.class.getSimpleName();
    private static final Player player = new Player();

    private final PlayMode[] modes = {PlayMode.SEQUENCE, PlayMode.SINGLE, PlayMode.SHUFFLE};
    private int currentModeIndex;
    private PlayMode currentMode = modes[currentModeIndex];

    private final List<Track> playList = new ArrayList<>();
    private Track currentTrack;
    private int currentTrackIndex = -1;

    private boolean playing;

    private Player() {
    }

    public static Player getInstance() {
        return player;
    }

    public void previous() {
        --currentTrackIndex;
        currentTrackIndex = currentTrackIndex < 0 ? playList.size() - 1 : currentTrackIndex;
        currentTrack = playList.get(currentTrackIndex);
        Log.d(TAG, String.format("previous: currentTrack = %s, index = %d", currentTrack, currentTrackIndex));
    }

    public void next() {
        ++currentTrackIndex;
        currentTrackIndex = currentTrackIndex >= playList.size() ? 0 : currentTrackIndex;
        currentTrack = playList.get(currentTrackIndex);
        Log.d(TAG, String.format("next: currentTrack = %s, index = %d", currentTrack, currentTrackIndex));
    }

    public void nextOverPlaying() {
        switch (currentMode) {
            case SEQUENCE:
                next();
                break;
            case SHUFFLE:
                Random random = new Random();
                random.setSeed(System.currentTimeMillis());
                int r = random.nextInt(playList.size());
                boolean forward = random.nextBoolean();
                while (r > 0) {
                    if (forward) next();
                    else previous();
                    r--;
                }
                break;
            default:
                break;
        }
    }

    public Track get(int index) {
        if (index == currentTrackIndex)
            return currentTrack;
        currentTrack = playList.get(index);
        currentTrackIndex = index;
        return currentTrack;
    }

    public boolean addNext(Track track) {
        if (!track.equals(currentTrack)) {
            remove(track);
            ListIterator<Track> it = playList.listIterator(currentTrackIndex);
            it.add(track);
            return true;
        }
        return false;
    }

    public void remove(Track track) {
        if (track.equals(currentTrack)) {
            playList.remove(track);
            nextOverPlaying();
        } else playList.remove(track);
    }

    public Track current() {
        return currentTrack;
    }

    public void setCurrent(int index) {
        this.currentTrack = get(index);
    }

    public int getCurrentTrackIndex() {
        return currentTrackIndex;
    }

    public void setPlayList(List<Track> tracks) {
        playList.addAll(tracks);
    }

    public boolean playing() {
        return playing;
    }

    public void changeMode() {
        currentModeIndex = (currentModeIndex + 1) % modes.length;
        currentMode = modes[currentModeIndex];
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public PlayMode getCurrentMode() {
        return currentMode;
    }

    public List<Track> getPlayList() {
        return playList;
    }
}
