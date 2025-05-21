package edu.vincentleo.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

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

import android.graphics.Color;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Music> musics;
    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private SearchView searchView; // 🔍 Barre de recherche

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

        // Initialisation des vues
        searchView = findViewById(R.id.search_bar);

        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        if (textView != null) {
            textView.setTextColor(Color.WHITE);            // Texte tapé
            textView.setHintTextColor(Color.GRAY);         // Texte de l'indice (queryHint)
        }

        // Récupère la SearchView
        recyclerView = findViewById(R.id.recycler_view);  // Récupère le RecyclerView

        // Chargement des musiques depuis le CSV
        musics = getMusics();

        // Configuration de l'adapter et du layout
        adapter = new MusicAdapter(musics);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Ajout du comportement de recherche
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // On ne fait rien ici
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText); // Filtrage dynamique
                return true;
            }
        });
    }

    // Lecture des données depuis le fichier lyrics.csv dans assets/
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
                            .setCoverUrl(tokens[4])
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
