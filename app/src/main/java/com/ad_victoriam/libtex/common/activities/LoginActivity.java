package com.ad_victoriam.libtex.common.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.user.activities.UserHomeActivity;
import com.ad_victoriam.libtex.user.activities.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputEditText eEmail;
    private TextInputEditText ePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);

        final Button bLogIn = findViewById(R.id.bLogIn);
        final Button bCreateAccount = findViewById(R.id.bCreateAccount);
        bLogIn.setOnClickListener(this::validateCredentials);
        bCreateAccount.setOnClickListener(this::bCreateAccount);
    }

    private void bCreateAccount(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void validateCredentials(View view) {
        String email = eEmail.getText().toString();
        String password = ePassword.getText().toString();

        boolean errorFlag = false;

        if (password.isEmpty()) {
            ePassword.setError("Please fill this field");
            ePassword.requestFocus();
            errorFlag = true;
        } else if (password.length() > 50) {
            ePassword.setError("Maximum length is 50 characters");
            ePassword.requestFocus();
            errorFlag = true;
        }

        if (email.isEmpty()) {
            eEmail.setError("Please fill this field");
            eEmail.requestFocus();
            errorFlag = true;
        } else if (email.length() > 50) {
            eEmail.setError("Maximum length is 50 characters");
            eEmail.requestFocus();
            errorFlag = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eEmail.setError("Email is not valid");
            eEmail.requestFocus();
            errorFlag = true;
        }

        if (errorFlag) {
            return;
        }

        signIn(email, password);
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidUserException | FirebaseAuthInvalidCredentialsException e) {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setMessage("Invalid credentials.")
                                        .setPositiveButton("Try again", (dialogInterface, i) -> {})
                                        .show();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser.getEmail().endsWith("libtex.com")) {
            startActivity(new Intent(this, AdminHomeActivity.class));
        } else {
            startActivity(new Intent(this, UserHomeActivity.class));
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:

                break;
        }
        return true;
    }
}
