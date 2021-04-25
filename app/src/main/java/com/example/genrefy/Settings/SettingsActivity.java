package com.example.genrefy.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.genrefy.Authentication.LoginActivity;
import com.example.genrefy.Base.BaseActivity;
import com.example.genrefy.Genres.GenreListActivity;
import com.example.genrefy.Genres.LibraryActivity;
import com.example.genrefy.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/* Sets up the Androidx preference screen, and registers
* user changes in preferences. */
public class SettingsActivity extends BaseActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myToolbar = findViewById(R.id.tbTop);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Listens for any changes of preferences
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("dropdown")) {

                    // Get text size from drop down selection
                    String textSize = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getString("dropdown", "6");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("textSize", textSize);
                    editor.commit();

                } if (key.equals("autoplay")) {

                    // Get the auto play selection
                    boolean autoplay = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getBoolean("autoplay", false);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("autoplay", autoplay);
                    editor.commit();

                } if (key.equals("backgroundColor")) {
                    // Get the background color preference
                    boolean backgroundColor = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getBoolean("backgroundColor", false);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("backgroundColor", backgroundColor);
                    editor.commit();

                } if (key.equals("showGenre")) {
                    // Get the show genre preference
                    boolean showGenre = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getBoolean("showGenre", false);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("showGenre", showGenre);
                    editor.commit();
                }
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }
}
