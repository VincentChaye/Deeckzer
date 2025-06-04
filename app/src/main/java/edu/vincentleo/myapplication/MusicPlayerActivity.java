package edu.vincentleo.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class MusicPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    private ImageButton playPauseButton;
    private ProgressBar fullProgressBar;
    private ProgressBar currentProgressBar;
    private TextView currentTimeView;
    private TextView maxTimeView;

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

        Music music = getIntent().getParcelableExtra("music");

        if (music == null) {
            Toast.makeText(this, "Erreur : musique introuvable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("DEBUG", "Lecture de : " + music.getTitle());
        Log.d("MUSIC_URL", "MP3 URL = " + music.getMp3());

        // Vues
        TextView titleView = findViewById(R.id.title);
        TextView authorView = findViewById(R.id.author);
        TextView dateView = findViewById(R.id.date);
        ImageView coverView = findViewById(R.id.cover);
        playPauseButton = findViewById(R.id.pauseplay);
        fullProgressBar = findViewById(R.id.progressBar);
        currentProgressBar = findViewById(R.id.currentProgress);
        currentTimeView = findViewById(R.id.currentTime);
        maxTimeView = findViewById(R.id.maxTime);

        // Infos
        titleView.setText(music.getTitle());
        authorView.setText(music.getArtist());
        dateView.setText(String.valueOf(music.getDate()));

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

        // Lecture audio
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

        // Bouton play/pause
        playPauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    playPauseButton.setImageResource(R.drawable.play);
                } else {
                    mediaPlayer.start();
                    playPauseButton.setImageResource(R.drawable.pause);
                }
                isPlaying = !isPlaying;
            }
        });
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
