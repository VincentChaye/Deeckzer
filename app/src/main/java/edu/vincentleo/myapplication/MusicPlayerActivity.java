package edu.vincentleo.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Random;

public class MusicPlayerActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private boolean isPlaying = false;
    private ImageButton playPauseButton;
    private ProgressBar fullProgressBar;
    private ProgressBar currentProgressBar;
    private TextView currentTimeView;
    private TextView maxTimeView;

    private MediaPlayer mediaPlayer;

    private Music music;

    private final Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int current = mediaPlayer.getCurrentPosition();
                int total = mediaPlayer.getDuration();

                currentTimeView.setText(formatTime(current));

                float ratio = current / (float) total;
                int maxWidth = fullProgressBar.getWidth();

                ConstraintLayout.LayoutParams params =
                        (ConstraintLayout.LayoutParams) currentProgressBar.getLayoutParams();
                params.width = (int) (ratio * maxWidth);
                currentProgressBar.setLayoutParams(params);
            }
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_player);

        music = getIntent().getParcelableExtra("music");

        if (music == null) {
            Toast.makeText(this, "Erreur : musique introuvable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("DEBUG", "Lecture de : " + music.getTitle());
        Log.d("MUSIC_URL", "MP3 URL = " + music.getMp3());

        // Vues
        playPauseButton = findViewById(R.id.pauseplay);
        fullProgressBar = findViewById(R.id.progressBar);
        currentProgressBar = findViewById(R.id.currentProgress);
        currentTimeView = findViewById(R.id.currentTime);
        maxTimeView = findViewById(R.id.maxTime);

        LinearLayout lyricsLayout = findViewById(R.id.lyricsLayout);
        lyricsLayout.setActivated(false);
        lyricsLayout.setVisibility(View.INVISIBLE);

        ImageView cover = findViewById(R.id.cover);
        cover.setActivated(true);
        cover.setVisibility(View.VISIBLE);

        TextView lyricsText = findViewById(R.id.lyricsText);
        lyricsText.setMovementMethod(new ScrollingMovementMethod());

        // Infos
        displayMusic();

        // Lecture audio
        mediaPlayer = playMusic();

        // Bouton play/pause
        playPauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                togglePlay();
            }
        });

        ImageButton previousButton = findViewById(R.id.previous);
        ImageButton nextButton = findViewById(R.id.next);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((float) (mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) > .01) {
                    mediaPlayer.start();
                    mediaPlayer.start(); // recommence la musique si elle est à moins de 1% de sa durée totale
                }
                else {
                    if (isPlaying) togglePlay();
                    music = Musics.getInstance().getPreviousMusic();
                    displayMusic();
                    playMusic();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying) togglePlay();
                music = Musics.getInstance().getNextMusic();
                displayMusic();
                playMusic();
            }
        });

        ImageButton lyricsButton = findViewById(R.id.lyrics);
        lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLyrics();
            }
        });
    }

    private void displayMusic() {
        TextView titleView = findViewById(R.id.title);
        TextView authorView = findViewById(R.id.author);
        TextView dateView = findViewById(R.id.date);
        ImageView coverView = findViewById(R.id.cover);
        TextView lyricsView = findViewById(R.id.lyricsText);

        titleView.setText(music.getTitle());
        authorView.setText(music.getArtist());
        dateView.setText(String.valueOf(music.getDate()));
        lyricsView.setText(music.getLyrics());

        // Image de couverture
        new Thread(() -> {
            try {
                URL url = new URL(music.getCoverUrl());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                if (bitmap != null) {
                    runOnUiThread(() -> coverView.setImageBitmap(bitmap));
                }
            } catch (Exception e) {
                Log.e("DEBUG", "Erreur image : " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void togglePlay() {
        isPlaying = !isPlaying;
        if (!isPlaying) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.play);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.pause);
        }
    }

    private MediaPlayer playMusic() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(music.getMp3());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                playPauseButton.setImageResource(R.drawable.pause);
                maxTimeView.setText(formatTime(mp.getDuration()));
                handler.post(updateProgress);
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "Erreur de lecture", Toast.LENGTH_LONG).show();
                Log.e("MediaPlayer", "Erreur de lecture : " + what + " / " + extra);
                return true;
            });

        } catch (IOException e) {
            Toast.makeText(this, "Fichier audio introuvable", Toast.LENGTH_LONG).show();
            Log.e("MediaPlayer", "IOException : " + e.getMessage());
            e.printStackTrace();
        }

        return mediaPlayer;
    }

    private void toggleLyrics() {
        LinearLayout layout = findViewById(R.id.lyricsLayout);
        ImageView cover = findViewById(R.id.cover);

        layout.setActivated(!layout.isActivated());
        cover.setActivated(!cover.isActivated());
        if(layout.isActivated())
            layout.setVisibility(View.VISIBLE);
        else
            layout.setVisibility(View.INVISIBLE);
        if(cover.isActivated())
            cover.setVisibility(View.VISIBLE);
        else
            cover.setVisibility(View.INVISIBLE);
    }

    private String formatTime(int millis) {
        int minutes = millis / 1000 / 60;
        int seconds = (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateProgress);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
