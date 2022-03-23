package com.thundersoft.android.musicplayer.player;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.stream.IntStream;

public class Player {
    private static final String TAG = Player.class.getSimpleName();
    private static final Player player = new Player();

    private final PlayMode[] modes = {PlayMode.SEQUENCE, PlayMode.SINGLE, PlayMode.SHUFFLE};
    private int currentModeIndex;
    private PlayMode currentMode = modes[currentModeIndex];

    private final List<Track> playList = new LinkedList<>();
    private Track currentTrack;
    private int currentTrackIndex = -1;

    private boolean playing;

    private Player() {
    }

    public static Player getInstance() {
        return player;
    }

    public void previous() {
        ListIterator<Track> it = playList.listIterator(currentTrackIndex);
        it.previous();
        currentTrack = it.previous();
        Log.d(TAG, "previous: " + currentTrack);
        currentTrackIndex = (currentTrackIndex - 1) % playList.size();
    }

    public void next() {
        ListIterator<Track> it = playList.listIterator(currentTrackIndex);
        it.next();
        currentTrack = it.next();
        Log.d(TAG, "next: " + currentTrack);
        currentTrackIndex = (currentTrackIndex + 1) % playList.size();
    }

    public Track get(int index) {
        if (index == currentTrackIndex)
            return currentTrack;
        currentTrack = playList.get(index);
        currentTrackIndex = index;
        return currentTrack;
    }

    // TODO
    public void addNext(Track track) {
        if (!track.equals(currentTrack)) {
            if (!playList.remove(track)) {
                ListIterator<Track> it = playList.listIterator(currentTrackIndex);
                it.add(track);
            } else {

            }

        }
    }

    /**
     * TODO
     * The track, which will be removed but now playing, should be stopped before deletion.
     * If it is the playing track, the system should select next track under current mode,
     * otherwise there is no influence and we can continue playing the current track.
     * Maybe index of current track will be changed after deletion...
     * @param track the track will be removed
     */
    public void remove(Track track) {
        if (track.equals(currentTrack)) {
            // playListIterator.remove();
            nextOverPlaying();
        }
        playList.remove(track);
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

    public Track current() {
        return currentTrack;
    }

    public void setCurrent(int index) {
        this.currentTrack = get(index);
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

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public PlayMode getCurrentMode() {
        return currentMode;
    }

    public List<Track> getPlayList() {
        return playList;
    }
}
