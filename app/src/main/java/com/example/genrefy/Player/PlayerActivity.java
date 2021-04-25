package com.example.genrefy.Player;

import androidx.annotation.WorkerThread;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.Callback;
import retrofit.RetrofitError;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kaaes.spotify.webapi.android.models.Result;
import retrofit.client.Response;

import com.example.genrefy.Base.BaseActivity;
import com.example.genrefy.Helpers.OnSwipeTouchListener;
import com.example.genrefy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

/* Connects to the Spotify remote player, which allows users
 * to play tracks directly from Spotify. Also allows users to
 * add songs and playlists to their Spotify library, and save
 * liked genres to their Genrefy account. */
public class PlayerActivity extends BaseActivity {

    private static final String REDIRECT_URI = "http://google.com";
    public static final String CLIENT_ID = "";
    private SharedPreferences msharedPreferences;
    private SpotifyAppRemote mSpotifyAppRemote;
    private PlayerState state;

    private String playlistName;
    private String userId;
    private String playlistId;
    private String genre;
    private TextView trackName;
    private TextView trackArtist;
    private TextView playlist;
    private Button btnFollowPlaylist;
    private Button btnAddGenre;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnLikeSong;
    private ImageView imgArtwork;

    int color;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Retrieve the playlist name, userID, playlistID, and genre name from the previous activity
        playlistName    = getIntent().getExtras().getString("playlistName","unavailable");
        userId          = getIntent().getExtras().getString("userId","unavailable");
        playlistId      = getIntent().getExtras().getString("playlistId","unavailable");
        genre           = getIntent().getExtras().getString("genre", "unavailable");

        msharedPreferences = PreferenceManager.getDefaultSharedPreferences(PlayerActivity.this);
        boolean showGenre = msharedPreferences.getBoolean("showGenre", false);

        Toolbar myToolbar = findViewById(R.id.tbTop);
        setSupportActionBar(myToolbar);

        if (showGenre) {
            getSupportActionBar().setTitle(playlistName);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        trackName           = findViewById(R.id.trackName);
        trackArtist         = findViewById(R.id.trackArtist);
        playlist            = findViewById(R.id.playlist);
        btnFollowPlaylist   = findViewById(R.id.btnFollow);
        btnNext             = findViewById(R.id.btnNext);
        btnPrevious         = findViewById(R.id.btnPrevious);
        btnPlayPause        = (ImageButton) findViewById(R.id.btnPlayPause);
        btnAddGenre         = findViewById(R.id.btnAddGenre);
        btnLikeSong         = findViewById(R.id.btnLikeSong);
        imgArtwork          = findViewById(R.id.imgArtwork);

        trackName.setSelected(true);
        trackArtist.setSelected(true);

        setAlbumSwipeListener();
    }

    /* Resumes or pauses player on user selection */
    public void onPlayPauseButtonClicked(View view) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(
                playerState -> {
                    state = playerState;
                    if (playerState.isPaused) { play(); }
                    else { pause(); }
                });

        getImage();
    }

    /* Set the skip next button onclick listener*/
    public void onSkipNextButtonClicked(View view) {
        skipNext();
    }

    /* Set the skip previous button onclick listener*/
    public void onSkipPreviousButtonClicked(View view) {
        skipPrevious();
    }

    /* Add the current track to the user's Spotify liked songs */
    public void onLikeSongButtonClicked(View view) {
        // Indicate the users button selection
        highlightImageButtonSelection(500, btnLikeSong,
                R.drawable.ic_baseline_favorite_24_green, R.drawable.ic_baseline_favorite_24);

        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(
                playerState -> {
                    state = playerState;
                    // Add the track to the users Spotify library
                    mSpotifyAppRemote.getUserApi().addToLibrary(state.track.uri).setResultCallback(
                            empty -> Log.d("MP", "TRACK ADDED"));
                });
    }

    /* Follow the current playlist on the user's Spotify account */
    public void onFollowPlaylistButtonClicked(View view) {
        // Indicate the users button selection
        highlightButtonSelection(500, btnFollowPlaylist, "#18D860", "#FFFFFF");

        try {
            // Connect to the Spotify API in order to make the follow playlist requests
            SpotifyApi api = new SpotifyApi();
            // Retrieve the users token and create a service request using the token
            msharedPreferences = this.getSharedPreferences("SPOTIFY", 0);
            api.setAccessToken(msharedPreferences.getString("token", ""));
            SpotifyService spotify = api.getService();

            // Connect to the Spotify API and follow the current playlist
            spotify.followPlaylist(userId, playlistId, new Callback<Result>() {
                @Override
                public void success(Result result, Response response) {
                    Log.d("Follow success", "success");
                }
                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(PlayerActivity.this, "Error occured", Toast.LENGTH_LONG).show();
                    Log.d("Follow failure", error.toString());
                }
            });

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    /* Add the current genre to the user's Genrefy library */
    public void onAddGenreButtonClicked(View view) {
        // Indicate the users button selection
        highlightButtonSelection(500, btnAddGenre, "#18D860", "#FFFFFF");

        try {
            // Connect to the firebase database and add the genre to the user's liked genres
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference mbase = FirebaseDatabase.getInstance().getReference("userGenres/"+user.getUid());
            mbase.child(user.getUid()+genre).child("genre").setValue(genre);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /* Set up swipe abilities on the album artwork to skip through tracks */
    @SuppressLint("ClickableViewAccessibility")
    private void setAlbumSwipeListener() {
        imgArtwork.setOnTouchListener(new OnSwipeTouchListener(PlayerActivity.this) {
            public void onSwipeRight() {
                skipPrevious();
            }
            public void onSwipeLeft() {
                skipNext();
            }
        });
    }

    /* Disconnect from the Spotify App Remote */
    private void disconnectPlayer() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    /* Connect to the Spotify App Remote and get the current playlist */
    private void connectPlayer() {
        workerThread();
    }

    @WorkerThread
    void workerThread() {
        PlayerActivity.this.runOnUiThread(() -> {
            boolean autoplay = msharedPreferences.getBoolean("autoplay", false);

            try {
                disconnectPlayer();

                // Set up the connection parameters
                ConnectionParams connectionParams =
                        new ConnectionParams.Builder(CLIENT_ID)
                                .setRedirectUri(REDIRECT_URI)
                                .showAuthView(true)
                                .build();

                // Connect to the Spotify App Remote using the connection parameters
                SpotifyAppRemote.connect(this, connectionParams,
                        new Connector.ConnectionListener() {
                            @Override
                            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                mSpotifyAppRemote = spotifyAppRemote;
                                mSpotifyAppRemote.getPlayerApi()
                                        .play("spotify:playlist:" + playlistId)
                                        .setResultCallback(empty ->{
                                            if (!autoplay) {
                                                mSpotifyAppRemote
                                                        .getPlayerApi()
                                                        .pause();
                                            } else {
                                                btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                                            }
                                        });

                                getImage();
                                playlist.setText(playlistName);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e("Connection", throwable.getMessage(), throwable);
                            }
                        });

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    /* Resume Spotify App Remote player */
    private void play() {
        mSpotifyAppRemote.getPlayerApi().resume().setResultCallback(
                empty -> Log.d("PLAYER", "play"));

        btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
    }

    /* Pause Spotify App Remote player */
    private void pause() {
        mSpotifyAppRemote.getPlayerApi().pause().setResultCallback(
                empty -> Log.d("PLAYER", "pause"));

        btnPlayPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
    }

    /* Skip to the next track using the Spotify App Remote player */
    private void skipNext() {
        mSpotifyAppRemote.getPlayerApi().skipNext().setResultCallback(playerState -> {
            if (state.isPaused) { play(); }
            else { pause(); }
        });
    }

    /* Skip to the previous track using the Spotify App Remote player */
    private void skipPrevious() {
        mSpotifyAppRemote.getPlayerApi().skipPrevious().setResultCallback(playerState -> {
            if (state.isPaused) { play(); }
            else { pause(); }
        });
    }

    /* Get the album image associated with the current track */
    private void getImage() {
        boolean backgroundColor = msharedPreferences.getBoolean("backgroundColor", false);

        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().
                setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            // Set the track name and artist
                            trackName.setText(track.name);
                            trackArtist.setText(track.artist.name);

                            // Retrieve the album image and set it to the image view
                            mSpotifyAppRemote.getImagesApi().getImage(track.imageUri, Image.Dimension.LARGE).setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                @Override public void onResult(Bitmap bitmap) {
                                    Log.d("IMAGE", bitmap.toString());
                                    imgArtwork.setImageBitmap(bitmap);

                                    if (backgroundColor) {
                                        changeBackgroundColor(bitmap);
                                    }
                                } });
                        }
                    }
                });
    }



    // Change the background to match the album color
    private void changeBackgroundColor(Bitmap bitmap) {
        color = bitmap.getPixel(0, 0);
        if (color < 14000000) {
            color = bitmap.getPixel(150, 150);
            if (color < 14000000) {
                color = bitmap.getPixel(10, 10);
            }
        }
        ConstraintLayout layout = findViewById(R.id.constraintLayout);
        layout.setBackgroundColor(ColorUtils.blendARGB(color, Color.BLACK, 0.8f));
        btnPlayPause.setBackgroundColor(ColorUtils.blendARGB(color, Color.BLACK, 0.8f));
        btnNext.setBackgroundColor(ColorUtils.blendARGB(color, Color.BLACK, 0.8f));
        btnPrevious.setBackgroundColor(ColorUtils.blendARGB(color, Color.BLACK, 0.8f));
        btnLikeSong.setBackgroundColor(ColorUtils.blendARGB(color, Color.BLACK, 0.8f));
    }

    /* Indicated that the user's button click has registered */
    private void highlightImageButtonSelection(int time, ImageButton button, int iconSelected, int iconDefault) {
        // Change the icon's color
        button.setImageResource(iconSelected);

        // Return to the default icon color after given delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                button.setImageResource(iconDefault);
            }
        }, time);
    }

    /* Indicated that the user's image button click has registered */
    private void highlightButtonSelection(int time, Button button, String colorSelected, String colorDefault) {
        // Change the button's color
        button.setBackgroundColor(Color.parseColor(colorSelected));

        // Return to the default button color after given delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                button.setBackgroundColor(Color.parseColor(colorDefault));
            }
        }, time);
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pause();
        disconnectPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
        disconnectPlayer();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        pause();
        disconnectPlayer();
    }
}