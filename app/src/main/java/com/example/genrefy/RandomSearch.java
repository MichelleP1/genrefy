package com.example.genrefy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RandomSearch extends AppCompatActivity {

    private DatabaseReference mbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_search);

        int numberOfGenres = 3230;
        int randomIndex = (int) (Math.floor(Math.random() * numberOfGenres));

        mbase = FirebaseDatabase.getInstance().getReference("genres").child("genre" + randomIndex).child("genre");

        Log.d("Database", "Genre:" + mbase);
        Log.d("PLAYER", "pause");
    }
}