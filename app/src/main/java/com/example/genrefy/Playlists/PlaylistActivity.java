package com.example.genrefy.Playlists;

import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.genrefy.Base.BaseActivity;
import com.example.genrefy.Genres.Genre;
import com.example.genrefy.Genres.GenreAdapter;
import com.example.genrefy.Genres.LibraryActivity;
import com.example.genrefy.Player.PlayerActivity;
import com.example.genrefy.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/* Lists the most relevant playlists of the
* users selected genre */
public class PlaylistActivity extends BaseActivity {

    private ArrayList<ArrayList<String>> aList = new ArrayList<ArrayList<String> >(20);
    private SharedPreferences msharedPreferences;
    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    String genreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        genreName = getIntent().getExtras().getString("genre","unavailable");

        msharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlaylistActivity.this);
        boolean showGenre = msharedPreferences.getBoolean("showGenre", false);

        Toolbar myToolbar = findViewById(R.id.tbTop);
        setSupportActionBar(myToolbar);

        if (showGenre) {
            getSupportActionBar().setTitle(genreName);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Create the recyclerview for listing the playlists
        recyclerView = findViewById(R.id.recycler2);
        recyclerView.setLayoutManager( new LinearLayoutManager(PlaylistActivity.this));

        ListPlaylistTask listPlaylistTask = new ListPlaylistTask();
        listPlaylistTask.execute();
    }

    @SuppressWarnings("deprecation")
    class ListPlaylistTask extends AsyncTask<Void, Void, Void> {
        // Get user id of current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Retrieve the users selected genre
        String genreName = getIntent().getExtras().getString("genre","unavailable");

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Connect to the Spotify API, needed to search for playlists
                SpotifyApi api = new SpotifyApi();
                // Retrieve the users token needed for API permission
                msharedPreferences = PlaylistActivity.this.getSharedPreferences("SPOTIFY", 0);
                api.setAccessToken(msharedPreferences.getString("token", ""));
                // Begin a service request to the Spotify API
                SpotifyService spotify = api.getService();

                // Use the service request to search for relevant playlists
                spotify.searchPlaylists(genreName, new Callback<PlaylistsPager>() {
                    @Override
                    public void success(PlaylistsPager playlistsPager, Response response) {
                        // Retrieve the userID, playlistID, and genre name - needed for
                        // any playlist follow requests
                        List<PlaylistSimple> items = playlistsPager.playlists.items;
                        int i = 0;
                        for( PlaylistSimple pt : items){
                            ArrayList<String> a1 = new ArrayList<String>();
                            a1.add(items.get(i).name);
                            a1.add(items.get(i).owner.id);
                            a1.add(items.get(i).id);
                            aList.add(a1);
                            i++;
                        }

                        // Create the recyclerview for listing the playlists
                        recyclerView = findViewById(R.id.recycler2);
                        recyclerView.setLayoutManager( new LinearLayoutManager(PlaylistActivity.this));

                        // Create the playlist adapter using the playlist data
                        PlaylistAdapter adapter = new PlaylistAdapter(PlaylistActivity.this, aList, genreName);

                        // Set the playlist adapter to the recyclerview
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Playlist error", error.toString());
                    }
                });

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                // Create the playlist adapter using the playlist data
                adapter = new PlaylistAdapter(PlaylistActivity.this, aList, genreName);
                // Set the playlist adapter to the recyclerview
                recyclerView.setAdapter(adapter);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
