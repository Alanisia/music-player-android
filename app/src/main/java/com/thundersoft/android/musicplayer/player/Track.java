package com.thundersoft.android.musicplayer.player;

import android.graphics.Bitmap;

import java.util.Objects;

public class Track {
    private String title;
    private String artist;
    private String duration;
    private String path;
    private Bitmap image;
    private int minutes;
    private int seconds;

    public String getTitle() {
        return title;
    }

    public Track setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public Track setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getDuration() {
        return duration;
    }

    public Track setDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Track setPath(String path) {
        this.path = path;
        return this;
    }

    public int getMinutes() {
        return minutes;
    }

    public Track setMinutes(int minutes) {
        this.minutes = minutes;
        return this;
    }

    public int getSeconds() {
        return seconds;
    }

    public Track setSeconds(int seconds) {
        this.seconds = seconds;
        return this;
    }

    public Bitmap getImage() {
        return image;
    }

    public Track setImage(Bitmap image) {
        this.image = image;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (minutes != track.minutes) return false;
        if (seconds != track.seconds) return false;
        if (!Objects.equals(title, track.title)) return false;
        if (!Objects.equals(artist, track.artist)) return false;
        if (!Objects.equals(duration, track.duration)) return false;
        if (!Objects.equals(path, track.path)) return false;
        return Objects.equals(image, track.image);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + minutes;
        result = 31 * result + seconds;
        return result;
    }

    @Override
    public String toString() {
        return "Track{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration='" + duration + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
