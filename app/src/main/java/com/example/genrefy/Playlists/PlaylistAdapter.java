package com.example.genrefy.Playlists;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.genrefy.Player.PlayerActivity;
import com.example.genrefy.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

/* The adapter used to display the playlists retrieved for a given genre */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewholder> {

    private Context mContext;
    private ArrayList<ArrayList<String>> mArrayPlaylist;
    private String genre;
    SharedPreferences sharedPreferences;

    public PlaylistAdapter(Context context, ArrayList<ArrayList<String>> arrayPlaylist, String genre)
    {
        mContext = context;
        mArrayPlaylist = arrayPlaylist;
        this.genre = genre;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /* Binds the playlists to the layout view */
    @Override
    public void onBindViewHolder(@NonNull PlaylistViewholder holder, int position)
    {
        // Get the playlist name
        String playlistName = mArrayPlaylist.get(position).get(0);
        // Get the users preferred text size and apply it
        String textSize = sharedPreferences.getString("textSize", "14");
        holder.playlist.setTextSize(Float.parseFloat(textSize));
        holder.playlist.setText(playlistName);

        // Add on click method to open each individual playlist
        holder.playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String playlistName = mArrayPlaylist.get(position).get(0);
                // Gets the playlist ownerID needed for follow request
                String userId = mArrayPlaylist.get(position).get(1);
                // Gets the playlist playlistID needed for follow request
                String playlistId = mArrayPlaylist.get(position).get(2);

                Intent intent = new Intent(context, PlayerActivity.class);
                // Pass the playlist name, userID, playlistID, and genre name to the player activity
                intent.putExtra("playlistName", playlistName);
                intent.putExtra("userId", userId);
                intent.putExtra("playlistId", playlistId);
                intent.putExtra("genre", genre);
                // Start the player activity
                context.startActivity(intent);
            }
        });
    }

    /* Set the playlist view to the adapter */
    @NonNull
    @Override
    public PlaylistViewholder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist, parent, false);
        return new PlaylistViewholder(view);
    }

    /* Create references to the views */
    class PlaylistViewholder extends RecyclerView.ViewHolder {
        TextView playlist;
        public PlaylistViewholder(@NonNull View itemView)
        {
            super(itemView);
            playlist = itemView.findViewById(R.id.playlist);
        }
    }

    /* Gets the number of playlists */
    @Override
    public int getItemCount() {
        return mArrayPlaylist.size();
    }
}
