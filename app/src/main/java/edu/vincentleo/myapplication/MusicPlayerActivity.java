package edu.vincentleo.myapplication;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MusicPlayerActivity extends AppCompatActivity {
    boolean lyricsShown = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Music music = getIntent().getParcelableExtra("music");


        ImageView cover = findViewById(R.id.cover);
        LinearLayout linearLayout = findViewById(R.id.lyricsLayout);
        TextView lyricsText = findViewById(R.id.lyricsText);
        lyricsText.setText(music.getLyrics());
        linearLayout.setActivated(false);

        ImageButton lyricsButton = findViewById(R.id.lyrics);
        lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLyrics();
                cover.setActivated(!isLyricsShown());
                linearLayout.setActivated(isLyricsShown());

            }
        });
    }

    private boolean isLyricsShown() { return lyricsShown; }

    private void toggleLyrics() {
        lyricsShown = !lyricsShown;
    }

    private void displayMusic(Music music) {

    }

    private void playMusic(Music music) throws IOException {
        MediaPlayer mediaPlayer = new MediaPlayer();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mediaPlayer.setDataSource(music.getMp3());
        mediaPlayer.setAudioAttributes(audioAttributes);



        mediaPlayer.setDataSource(music.getMp3());
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
}
