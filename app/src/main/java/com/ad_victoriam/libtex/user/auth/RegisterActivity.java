package com.ad_victoriam.libtex.user.auth;

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
import com.ad_victoriam.libtex.common.auth.LoginActivity;
import com.ad_victoriam.libtex.common.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputEditText eEmail;
    private TextInputEditText ePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);

        final Button bRegister = findViewById(R.id.bRegister);
        final Button bLogIn = findViewById(R.id.bLogIn);
        bRegister.setOnClickListener(this::validateCredentials);
        bLogIn.setOnClickListener(this::goToLogin);
    }

    private void goToLogin(View view) {
        finish();
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            storeDataIntoDatabase(firebaseUser);
                            mAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthUserCollisionException e) {
                                eEmail.setError("Email is already in use");
                                eEmail.requestFocus();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
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
            eEmail.setError("Email address is not valid");
            eEmail.requestFocus();
            errorFlag = true;
        }

        if (errorFlag) {
            return;
        }

        createAccount(email, password);
    }

    private void storeDataIntoDatabase(FirebaseUser firebaseUser) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        User user = new User(firebaseUser.getEmail());
        databaseReference.child("unverified-users").child(firebaseUser.getUid()).setValue(user);
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
