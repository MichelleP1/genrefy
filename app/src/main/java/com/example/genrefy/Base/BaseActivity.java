package com.example.genrefy.Base;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.genrefy.Genres.GenreListActivity;
import com.example.genrefy.Genres.LibraryActivity;
import com.example.genrefy.R;
import com.example.genrefy.Settings.SettingsActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/* Creates the top menu bar used throughout the app */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_list);

        Toolbar myToolbar = findViewById(R.id.tbTop);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /* Create the top menu bar */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* Set on click methods for each menu item */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miSearch:
                showSearch();
                return true;
            case R.id.miLibrary:
                showLibrary();
                return true;
            case R.id.miSettings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Move to full genre list activity */
    public void showSearch() {
        Intent intent = new Intent(this, GenreListActivity.class);
        startActivity(intent);
    }

    /* Move to users genre library list activity */
    public void showLibrary() {
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
    }

    /* Move to settings activity */
    public void showSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
