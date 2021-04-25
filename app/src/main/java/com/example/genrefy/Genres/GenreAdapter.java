package com.example.genrefy.Genres;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genrefy.Playlists.PlaylistActivity;
import com.example.genrefy.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

/* The adapter used to display the genres from the Firebase database */
public class GenreAdapter extends FirebaseRecyclerAdapter<Genre, GenreAdapter.genresViewholder> {
    SharedPreferences sharedPreferences;

    public GenreAdapter(@NonNull FirebaseRecyclerOptions<Genre> options, Context context)
    {
        super(options);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /* Binds the genre model to the layout view */
    @Override
    protected void
    onBindViewHolder(@NonNull genresViewholder holder, int position, @NonNull Genre model)
    {
        // Get the users chosen text size from shared preferences
        String textSize = sharedPreferences.getString("textSize", "14");
        holder.genre.setTextSize(Float.parseFloat(textSize));
        // Use the genre model to get genre data and set to the view
        holder.genre.setText(model.getGenre());
    }

    /* Set the genre view to the adapter */
    @NonNull
    @Override
    public genresViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre, parent, false);

        return new GenreAdapter.genresViewholder(view);
    }

    /* Create references to the views */
    class genresViewholder extends RecyclerView.ViewHolder {
        TextView genre;
        public genresViewholder(@NonNull View itemView)
        {
            super(itemView);

            genre = itemView.findViewById(R.id.genre);

            // Add on click method to each genre that is listed
            genre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlaylistActivity.class);
                    String genreName = genre.getText().toString();
                    // Send genre name to next activity
                    intent.putExtra("genre", genreName);
                    // Move to the playlist activity
                    context.startActivity(intent);
                }
            });
        }
    }
}
