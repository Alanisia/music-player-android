package com.thundersoft.android.musicplayer.player;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Player {
    private static final PlayMode[] modes = {PlayMode.SEQUENCE, PlayMode.SINGLE, PlayMode.SHUFFLE};
    private static PlayMode currentMode;
    private static int currentModeIndex;
    private static final List<Track> playList = new LinkedList<>();
    private static final ListIterator<Track> playListIterator = playList.listIterator();
    private static Track currentTrack;
    private static int currentTrackIndex;

    public static void previous() {
        currentTrack = playListIterator.previous();
    }

    public static void next() {
        currentTrack = playListIterator.next();
    }

    public static Track get(int index) {
        if (index == currentTrackIndex)
            return currentTrack;
        while (currentTrackIndex < index) {
            currentTrack = playListIterator.next();
            currentTrackIndex++;
        }
        while (currentTrackIndex > index) {
            currentTrack = playListIterator.previous();
            currentTrackIndex--;
        }
        return currentTrack;
    }

    public static void addNext(Track track) {
        if (!track.equals(currentTrack)) {
            playList.remove(track);
            playListIterator.add(track);
        }
    }

    // TODO
    public static void remove(Track track) {
        if (track.equals(currentTrack)) {
            playListIterator.remove();
//            currentTrack =
        }
        playList.remove(track);
    }

    public static void changeMode() {
        currentModeIndex = (currentModeIndex + 1) % modes.length;
        currentMode = modes[currentModeIndex];
    }

    public static PlayMode getCurrentMode() {
        return currentMode;
    }

    public static List<Track> getPlayList() {
        return playList;
    }
}
