package com.example.genrefy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.genrefy.Authentication.LoginActivity;
import com.example.genrefy.Authentication.SpotifyAuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/* Begins sign in process */
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // If user is not yet signed in, bring them to the sign in page
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        // If user is currently signed in, skip to Spotify authentication
        else {
            Intent intent = new Intent(MainActivity.this, SpotifyAuthActivity.class);
            startActivity(intent);
        }
    }
}


