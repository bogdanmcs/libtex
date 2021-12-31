package com.ad_victoriam.libtex.user.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.auth.LoginActivity;
import com.ad_victoriam.libtex.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputEditText eEmail;
    private TextInputEditText ePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        mAuth = FirebaseAuth.getInstance();

        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);

        final Button bRegister = findViewById(R.id.bRegister);
        bRegister.setOnClickListener(this::validateCredentials);
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
                                // do nothing
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
            eEmail.setError("Email is not valid");
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
        databaseReference.child("users").child(firebaseUser.getUid()).setValue(user);
    }
}
