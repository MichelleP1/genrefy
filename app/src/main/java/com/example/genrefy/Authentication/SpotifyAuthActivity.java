package com.example.genrefy.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.genrefy.Genres.GenreListActivity;
import com.example.genrefy.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

/* Authenticate user through Spotify Android SDK authentication */
public class SpotifyAuthActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private SharedPreferences.Editor editor;
    private static final String REDIRECT_URI = "http://google.com";
    public static final String CLIENT_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_auth);

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-modify-public", "playlist-modify-private", "app-remote-control"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    /* Receive the result of the authentication request */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Successful response, token received
                case TOKEN:
                    // Store access token for later use
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();

                    // Move to genre list activity if authentication was successful
                    Intent genreIntent = new Intent(SpotifyAuthActivity.this, GenreListActivity.class);
                    startActivity(genreIntent);
                    break;

                case ERROR:
                    Toast.makeText(SpotifyAuthActivity.this, "ERROR", Toast.LENGTH_LONG);
                    break;

                default:
                    Toast.makeText(SpotifyAuthActivity.this, "CANCELLED", Toast.LENGTH_LONG);
            }
        }
    }
}