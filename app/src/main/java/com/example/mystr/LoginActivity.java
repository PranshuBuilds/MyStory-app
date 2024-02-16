package com.example.mystr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText emailEditText;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailedit);
        passwordEditText = findViewById(R.id.passworkedit);

        Button signInButton = findViewById(R.id.signin);
        Button signUpButton = findViewById(R.id.signup);

        signInButton.setOnClickListener(v -> signIn(emailEditText.getText().toString(), passwordEditText.getText().toString()));

        signUpButton.setOnClickListener(v -> signUp(emailEditText.getText().toString(), passwordEditText.getText().toString()));
    }
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Successful sign in, update UI or navigate to next screen
                        updateUI(user);
                    } else {
                        // Sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Successful sign up, update UI or navigate to next screen
                        updateUI(user);
                    } else {
                        // Sign up fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in, navigate to main activity or dashboard
            // For example:
             startActivity(new Intent(LoginActivity.this, MainActivity.class));
             finish();
        } else {
            // User is signed out, stay on the login screen
            // You can also display a message to the user
        }
    }

    // Handle Firebase authentication errors
    private void handleAuthErrors(@NonNull Exception exception) {
        // Handle specific Firebase authentication errors here
        // For example:
        // if (exception instanceof FirebaseAuthInvalidCredentialsException) {
        //     // Invalid password format
        // } else if (exception instanceof FirebaseAuthUserCollisionException) {
        //     // Email already exists, user collision
        // } else {
        //     // Other authentication errors
        // }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if user is already signed in
        if (currentUser != null) {
            // Redirect to main activity or dashboard
            // For example:
             startActivity(new Intent(LoginActivity.this, MainActivity.class));
             finish();
        }
    }
}