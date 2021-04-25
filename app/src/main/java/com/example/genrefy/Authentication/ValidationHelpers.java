package com.example.genrefy.Authentication;

import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

/* Check that email and password fields have input */
public class ValidationHelpers {
    /* Check that email field has input */
    public static boolean validateEmail(TextInputLayout etEmail) {
        String emailInput = etEmail.getEditText().getText().toString().trim();
        if (emailInput.isEmpty()) {
            etEmail.setError("Field can't be empty");
            return false;
        } else {
            etEmail.setError(null);
            return true;
        }
    }

    /* Check that password field has input */
    public static boolean validatePassword(TextInputLayout etPassword) {
        String passwordInput = etPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            etPassword.setError("Field can't be empty");
            return false;
        } else {
            etPassword.setError(null);
            return true;
        }
    }
}
