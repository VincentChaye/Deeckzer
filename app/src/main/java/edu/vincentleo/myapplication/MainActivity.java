package edu.vincentleo.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Music> musics;

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

        musics = getMusics();

        
    }

    private ArrayList<Music> getMusics() {
        ArrayList<Music> musics = new ArrayList<>();

        Context context = this.getApplicationContext();

        try {
            InputStream is = context.getAssets().open("../../../sampledata/lyrics.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String ligne;
            boolean premiereLigne = true;

            while ((ligne = reader.readLine()) != null) {
                if (premiereLigne) {
                    premiereLigne = false;
                    continue;
                }

                String[] tokens = ligne.split(",");
                if (tokens.length == 6) {

                    Music music = new Music()
                            .setTitle(tokens[0])
                            .setAlbum(tokens[1])
                            .setArtist(tokens[2])
                            .setDate(Integer.parseInt(tokens[3]))
                            .setCover(Picasso.get().load("URL").get())
                            .setLyrics(tokens[5].replace(';',  '\n'));

                    musics.add(music);
                }
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return musics;
    }
}