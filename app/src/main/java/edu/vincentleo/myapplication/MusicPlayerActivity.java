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
    private boolean isPlaying = true;

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

        // Récupération de la musique passée par l'intent
        Music music = getIntent().getParcelableExtra("music");

        if (music == null) {
            Log.e("DEBUG", ">>> music == null !");
            Toast.makeText(this, "Erreur : musique introuvable", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("DEBUG", ">>> MusicPlayerActivity lancée");
        Log.d("MUSIC_URL", "URL MP3: " + music.getMp3());

        // Bind des vues
        TextView titleView = findViewById(R.id.title);
        TextView authorView = findViewById(R.id.author);
        TextView dateView = findViewById(R.id.date);
        ImageView coverView = findViewById(R.id.cover);

        playPauseButton = findViewById(R.id.pauseplay);
        fullProgressBar = findViewById(R.id.progressBar);
        currentProgressBar = findViewById(R.id.currentProgress);
        currentTimeView = findViewById(R.id.currentTime);
        maxTimeView = findViewById(R.id.maxTime);

        // Affichage des infos
        titleView.setText(music.getTitle());
        authorView.setText(music.getArtist());
        dateView.setText(String.valueOf(music.getDate()));

        // Chargement de l’image (en thread secondaire)
        new Thread(() -> {
            try {
                URL url = new URL(music.getCoverUrl());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                if (bitmap != null) {
                    runOnUiThread(() -> coverView.setImageBitmap(bitmap));
                }
            } catch (Exception e) {
                Log.e("DEBUG", "Erreur chargement image : " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        // Initialisation MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(music.getMp3());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                maxTimeView.setText(formatTime(mp.getDuration()));
                handler.post(updateProgress);
            });
        } catch (IOException e) {
            Toast.makeText(this, "Erreur de lecture audio", Toast.LENGTH_SHORT).show();
            Log.e("MediaPlayer", "IOException: " + e.getMessage());
            e.printStackTrace();
        }

        // En cas d'erreur de lecture
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e("MediaPlayer", "Erreur pendant la lecture : what=" + what + ", extra=" + extra);
            return true;
        });

        // Bouton Play/Pause
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
