package edu.vincentleo.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MusicService extends Service {

    public static final String ACTION_TOGGLE = "TOGGLE";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREV";

    private MediaPlayer mediaPlayer;
    private String currentUrl;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action == null) {
            String musicUrl = intent.getStringExtra("musicUrl");
            startMusic(musicUrl);
        } else {
            switch (action) {
                case ACTION_TOGGLE:
                    toggle();
                    break;
                case ACTION_NEXT:
                    // gestion future
                    break;
                case ACTION_PREV:
                    // gestion future
                    break;
            }
        }

        return START_NOT_STICKY;
    }

    private void startMusic(String url) {
        currentUrl = url;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                new Thread(() -> {
                    while (mp != null && mp.isPlaying()) {
                        Intent progressIntent = new Intent("UPDATE_PROGRESS");
                        progressIntent.putExtra("current", mp.getCurrentPosition());
                        progressIntent.putExtra("duration", mp.getDuration());
                        sendBroadcast(progressIntent);

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }).start();
            });

        } catch (IOException e) {
            Log.e("MusicService", "Erreur MediaPlayer", e);
        }
    }

    private void toggle() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}