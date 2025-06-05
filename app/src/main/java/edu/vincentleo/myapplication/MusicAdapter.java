package edu.vincentleo.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private final List<Music> fullList;
    private final List<Music> filteredList;

    public MusicAdapter(List<Music> musicList) {
        this.fullList = new ArrayList<>(musicList);
        this.filteredList = new ArrayList<>(musicList);
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = filteredList.get(position);

        holder.title.setText(music.getTitle());
        holder.author.setText(music.getArtist());
        holder.time.setText(String.format("%d:%02d", music.getMinutes(), music.getSeconds()));

        // Chargement natif de l'image en thread secondaire
        new Thread(() -> {
            try {
                String imageUrl = music.getCoverUrl();
                URL url = new URL(imageUrl);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                if (bitmap != null) {
                    ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                        holder.cover.setImageBitmap(bitmap);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        String source = music.getMatchSource();

        if (source != null && !source.isEmpty()) {
            holder.matchSource.setVisibility(View.VISIBLE);
            holder.matchSource.setText(source);

            // 🎨 Couleurs personnalisées selon la provenance
            switch (source) {
                case "Titre":
                    holder.matchSource.setTextColor(0xFF4CAF50); // vert
                    break;
                case "Auteur":
                    holder.matchSource.setTextColor(0xFF2196F3); // bleu
                    break;
                case "Paroles":
                    holder.matchSource.setTextColor(0xFFF44336); // rouge
                    break;
                default:
                    holder.matchSource.setTextColor(0xFF888888); // gris par défaut
            }

        } else {
            holder.matchSource.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(holder.itemView.getContext(), MusicPlayerActivity.class);
            intent.putExtra("music", music);
            Musics.getInstance().addToQueue(music);
            holder.itemView.getContext().startActivity(intent);
        });



    }


    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            for (Music music : fullList) {
                music.setMatchSource("");
            }
            filteredList.addAll(fullList);
        } else {
            String lowerQuery = query.toLowerCase();
            List<Music> matchedTitles = new ArrayList<>();
            List<Music> matchedArtists = new ArrayList<>();
            List<Music> matchedLyrics = new ArrayList<>();

            for (Music music : fullList) {
                if (music.getTitle().toLowerCase().contains(lowerQuery)) {
                    music.setMatchSource("Titre");
                    matchedTitles.add(music);
                } else if (music.getArtist().toLowerCase().contains(lowerQuery)) {
                    music.setMatchSource("Auteur");
                    matchedArtists.add(music);
                } else if (music.getLyrics().toLowerCase().contains(lowerQuery)) {
                    music.setMatchSource("Paroles");
                    matchedLyrics.add(music);
                } else {
                    music.setMatchSource("");
                }
            }

            filteredList.addAll(matchedTitles);
            filteredList.addAll(matchedArtists);
            filteredList.addAll(matchedLyrics);
        }
        notifyDataSetChanged();
    }




    static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, author, time, matchSource;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.time);
            matchSource = itemView.findViewById(R.id.matchSource);

        }
    }
}
