package com.example.genrefy.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genrefy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/* Allows user to sign up for an account through
* Firebase Authentication */
public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnSignUp;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.etEmailSignUp);
        etPassword = findViewById(R.id.etPasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignup);
        tvSignIn = findViewById(R.id.tvLogin);

        mAuth = FirebaseAuth.getInstance();

        setSignInOption();
        setSignUpOption();
    }

    /* Create an account using Firebase Authentication */
    private void setSignUpOption() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getEditText().getText().toString().trim();
                String password = etPassword.getEditText().getText().toString().trim();

                // Ensure email and password fields have input
                if (!ValidationHelpers.validateEmail(etEmail) | !ValidationHelpers.validatePassword(etPassword)) {
                    return;
                }

                // Create the account
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SaveUserToDatabase.saveUserInDatabase();

                                // Move on to Spotify Authentication
                                Intent intent = new Intent(SignUpActivity.this, SpotifyAuthActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUpActivity.this, "Please enter a valid email and password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });
    }

    /* Move to sign in page */
    private void setSignInOption() {
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}