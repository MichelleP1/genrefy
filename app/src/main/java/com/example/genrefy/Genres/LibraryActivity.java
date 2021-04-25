package com.example.genrefy.Genres;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.genrefy.Base.BaseActivity;
import com.example.genrefy.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LibraryActivity extends BaseActivity {
    private RecyclerView recyclerView;
    GenreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Toolbar myToolbar = findViewById(R.id.tbTop);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView = findViewById(R.id.recycler2);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
    }

    @SuppressWarnings("deprecation")
    class ListLibraryTask extends AsyncTask<Void, Void, Void> {
        // Get user id of current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get database reference to the current users saved genres
        private DatabaseReference mbase = FirebaseDatabase.getInstance().getReference("userGenres/"+user.getUid());
        FirebaseRecyclerOptions<Genre> options;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Create the recycler using the data queried from the database
                options = new FirebaseRecyclerOptions.Builder<Genre>()
                        .setQuery(mbase, Genre.class)
                        .build();

            } catch (Exception exception) {
                exception.printStackTrace();
            }


            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                // set the adapter to the recyclerview
                adapter = new GenreAdapter(options, LibraryActivity.this);
                adapter.startListening();
                recyclerView.setAdapter(adapter);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /* Begin the adapter once the activity starts */
    @Override protected void onStart()
    {
        super.onStart();

        ListLibraryTask listLibraryTask = new ListLibraryTask();
        listLibraryTask.execute();
    }

    /* Stop getting adapter data once activity is stopped */
    @Override protected void onStop()
    {
        super.onStop();

        adapter.stopListening();
    }
}