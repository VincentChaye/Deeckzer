package edu.vincentleo.myapplication;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Music {
    private String title, album, artist, lyrics;
    private int date;
    private Bitmap cover;
    private String mp3;
    private int minutes;
    private int seconds;

    public Music() {}

    public Music setTitle(String title) {
        this.title = title;
        return this;
    }

    public Music setAlbum(String album) {
        this.album = album;
        return this;
    }

    public Music setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public Music setLyrics(String lyrics) {
        this.lyrics = lyrics;
        return this;
    }

    public Music setDate(int date) {
        this.date = date;
        return this;
    }

    public Music setCover(Bitmap cover) {
        this.cover = cover;
        return this;
    }

    public Music setMp3(String mp3) {
        this.mp3 = mp3;
        return this;
    }

    public Music setDuration(int minutes, int seconds) {
        this.minutes = minutes;
        this.seconds = seconds;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getLyrics() {
        return lyrics;
    }

    public int getDate() {
        return date;
    }

    public Bitmap getCover() {
        return cover;
    }

    public String getMp3() {
        return mp3;
    }

    public int[] getTime() {

    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }
}
