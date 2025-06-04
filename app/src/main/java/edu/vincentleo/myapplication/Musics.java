package edu.vincentleo.myapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Musics {
    private static Musics instance;
    private ArrayList<Music> musics;
    private ArrayList<Music> queue;
    private int current;

    private Musics() {
        ArrayList<Music> musics = new ArrayList<>();
        queue = new ArrayList<>();
        current = 0;

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("assets/lyrics.csv");
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

        this.musics = musics;
    }

    public static Musics getInstance() {
        if(instance == null) instance = new Musics();
        return instance;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public ArrayList<Music> getQueue() {
        return queue;
    }

    public void addToQueue(Music music) {
        queue.add(music);
    }

    public int getQueuePosition() {
        return current;
    }

    public Music getPreviousMusic() {
        current--;
        if(current < 0) {
            current = 0;
        } // retour au début de la queue si on l'est déjà

        return queue.get(current); // joue la musique précédente dans la queue
    }

    public Music getLastMusic() {
        return queue.get(queue.size() - 1);
    }

    public Music getNextMusic() {
        current++;
        if(current == queue.size()) {
            Music newMusic = getLastMusic();

            while(newMusic == getQueue().get(current - 1)) {

                newMusic = Musics.getInstance().getMusics().get(new Random().nextInt(Musics.getInstance().getMusics().size()));
                addToQueue(newMusic);
            } // ajout d'une musique aléatoire si on est à la fin de la queue
        }
        return queue.get(current);
    }
}
