package com.example.ValPlayer;

import android.provider.MediaStore;

// Is used in the recycleviewer for listing all audio files.
// This class represents an individual item that the RecycleViewer can hold.
// From https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
public class AudioFile {
    private long id;
    private String song;
    private String artist;
    private String album;
    private long duration;
    private String path;

    AudioFile (long id, String song, String artist, String album, long duration, String path) {
        this.id = id;
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.path = path;
    }


    public long getId() {
        return id;
    }

    public String getSong() {
        return song;
    }

    public String getPath() {
        return path;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }

    public String getFormattedDuration() {
        long durInSec = duration / 1000;
        return String.format("%d:%02d:%02d", durInSec / 3600, durInSec % 3600 / 60, durInSec % 60);
    }
}
