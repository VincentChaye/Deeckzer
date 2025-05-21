package edu.vincentleo.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Music> musics;
    private RecyclerView recyclerView;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        musics = getMusics();

        adapter = new MusicAdapter(musics);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Music> getMusics() {
        ArrayList<Music> musics = new ArrayList<>();
        Context context = this.getApplicationContext();

        try {
            InputStream is = context.getAssets().open("lyrics.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String ligne;
            reader.readLine(); // Ignorer l'en-tête

            while ((ligne = reader.readLine()) != null) {
                String[] tokens = ligne.split("#");
                if (tokens.length == 8) {
                    String[] time = tokens[7].split("\\.");
                    int minutes = Integer.parseInt(time[0]);
                    int seconds = Integer.parseInt(time[1]);

                    Music music = new Music()
                            .setTitle(tokens[0])
                            .setAlbum(tokens[1])
                            .setArtist(tokens[2])
                            .setDate(Integer.parseInt(tokens[3]))
                            .setCoverUrl( tokens[4]) // 🔁 Correction ici
                            .setLyrics(tokens[5].replace(';', '\n'))
                            .setMp3(tokens[6])
                            .setDuration(minutes, seconds);

                    musics.add(music);
                }
            }

            reader.close();

        } catch (Exception e) {
            Log.e("getMusics", e.getMessage(), e.getCause());
        }

        return musics;
    }

}
