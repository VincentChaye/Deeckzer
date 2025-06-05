package edu.vincentleo.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.net.URL;
import java.util.Locale;

public class MusicPlayerActivity extends AppCompatActivity {

    private ImageButton playPauseButton;
    private ProgressBar fullProgressBar;
    private ProgressBar currentProgressBar;
    private TextView currentTimeView;
    private TextView maxTimeView;

    private Music music;

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(
                progressReceiver,
                new IntentFilter("UPDATE_PROGRESS"),
                Context.RECEIVER_NOT_EXPORTED
        );
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(progressReceiver);
    }

    private final BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getIntExtra("current", 0);
            int duration = intent.getIntExtra("duration", 1);

            currentTimeView.setText(formatTime(current));

            float ratio = current / (float) duration;
            int maxWidth = fullProgressBar.getWidth();

            ConstraintLayout.LayoutParams params =
                    (ConstraintLayout.LayoutParams) currentProgressBar.getLayoutParams();
            params.width = (int) (ratio * maxWidth);
            currentProgressBar.setLayoutParams(params);
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

        // Initialisation des vues
        playPauseButton = findViewById(R.id.pauseplay);
        fullProgressBar = findViewById(R.id.progressBar);
        currentProgressBar = findViewById(R.id.currentProgress);
        currentTimeView = findViewById(R.id.currentTime);
        maxTimeView = findViewById(R.id.maxTime);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        LinearLayout lyricsLayout = findViewById(R.id.lyricsLayout);
        lyricsLayout.setVisibility(View.INVISIBLE);

        ImageView cover = findViewById(R.id.cover);

        TextView lyricsText = findViewById(R.id.lyricsText);
        lyricsText.setMovementMethod(new ScrollingMovementMethod());

        displayMusic();

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("musicUrl", music.getMp3());
        startService(intent);

        playPauseButton.setOnClickListener(v -> {
            Intent i = new Intent(this, MusicService.class);
            i.setAction(MusicService.ACTION_TOGGLE);
            startService(i);
        });

        findViewById(R.id.previous).setOnClickListener(v -> {
            Intent i = new Intent(this, MusicService.class);
            i.setAction(MusicService.ACTION_PREV);
            startService(i);
        });

        findViewById(R.id.next).setOnClickListener(v -> {
            Intent i = new Intent(this, MusicService.class);
            i.setAction(MusicService.ACTION_NEXT);
            startService(i);
        });

        findViewById(R.id.lyrics).setOnClickListener(v -> toggleLyrics());
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

        new Thread(() -> {
            try {
                URL url = new URL(music.getCoverUrl());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                if (bitmap != null) {
                    runOnUiThread(() -> coverView.setImageBitmap(bitmap));
                }
            } catch (Exception e) {
                Log.e("DEBUG", "Erreur image : " + e.getMessage());
            }
        }).start();
    }

    private void toggleLyrics() {
        LinearLayout layout = findViewById(R.id.lyricsLayout);
        ImageView cover = findViewById(R.id.cover);

        boolean showLyrics = layout.getVisibility() != View.VISIBLE;
        layout.setVisibility(showLyrics ? View.VISIBLE : View.INVISIBLE);
        cover.setVisibility(showLyrics ? View.INVISIBLE : View.VISIBLE);
    }

    private String formatTime(int millis) {
        int minutes = millis / 1000 / 60;
        int seconds = (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }
}