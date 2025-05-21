package edu.vincentleo.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {
        filteredList.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Music music : fullList) {
                if (music.getTitle().toLowerCase().startsWith(lowerQuery)) {
                    filteredList.add(music);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class MusicViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, author, time;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.time);
        }
    }
}
