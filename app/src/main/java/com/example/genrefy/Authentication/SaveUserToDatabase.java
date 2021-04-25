package com.example.genrefy.Authentication;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/* Saves newly created accounts to the database. Also
* saves any Google account sign in on first sign in. */
public class SaveUserToDatabase {

    public static void saveUserInDatabase() {
        try {
            // Get the currently signed in users userID
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Reference the databases user model
            DatabaseReference mbase = FirebaseDatabase.getInstance().getReference("users");
            // Add the userID to the databases users
            mbase.child(user.getUid()).setValue(user.getUid());

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
