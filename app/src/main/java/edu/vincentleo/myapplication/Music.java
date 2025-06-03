package edu.vincentleo.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Date;

public class Music implements Parcelable {
    private String title;
    private String album;
    private String artist;
    private String lyrics;
    private String coverUrl;
    private String mp3;
    private int date;
    private int minutes;
    private int seconds;

    public Music() {
        lyrics = "";
        coverUrl = ""; //TODO trouver image de couverture par defaut :)
        date = Date.from(Instant.now()).getYear();
    }

    public Music(Parcel in) {
        title = in.readString();
        album = in.readString();
        artist = in.readString();
        lyrics = in.readString();
        coverUrl = in.readString();
        mp3 = in.readString();
        date = in.readInt();
        minutes = in.readInt();
        seconds = in.readInt();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

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

    public Music setCoverUrl(String coverUrl) {
        this.coverUrl = "http://edu.info06.net/lyrics/images/" + coverUrl;
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

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getMp3() {
        return mp3;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int[] getTime() {
        return new int[]{minutes, seconds};
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(lyrics);
        dest.writeString(coverUrl);
        dest.writeString(mp3);
        dest.writeInt(date);
        dest.writeInt(minutes);
        dest.writeInt(seconds);
    }
}
