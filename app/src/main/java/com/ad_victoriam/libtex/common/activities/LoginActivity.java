package com.ad_victoriam.libtex.common.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.user.activities.RegisterActivity;
import com.ad_victoriam.libtex.user.activities.UserHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
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

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setVisibility(View.INVISIBLE);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.bTest:
//                        Toast.makeText(getApplicationContext(), "hello menu listener", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//                return true;
//            }
//        });

        mAuth = FirebaseAuth.getInstance();

        eEmail = findViewById(R.id.eEmail);
        ePassword = findViewById(R.id.ePassword);

        final Button bLogIn = findViewById(R.id.bLogIn);
        final Button bCreateAccount = findViewById(R.id.bCreateAccount);
        bLogIn.setOnClickListener(this::validateCredentials);
        bCreateAccount.setOnClickListener(this::bCreateAccount);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 66) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            if (intent.hasExtra("success") && intent.getStringExtra("success").equals("true")) {
                                Toast.makeText(getApplicationContext(), "Registration was successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
    );

    private void bCreateAccount(View view) {
        activityResultLauncher.launch(new Intent(this, RegisterActivity.class));
    }

    private void validateCredentials(View view) {
        String email = eEmail.getText().toString();
        String password = ePassword.getText().toString();

        TextView tEmailHelper = findViewById(R.id.tEmailHelper);
        TextView tPasswordHelper = findViewById(R.id.tPasswordHelper);
        tEmailHelper.setText("");
        tPasswordHelper.setText("");

        boolean isErrorEmail = true;
        boolean isErrorPassword = true;
        int EMAIL_MAX_LIMIT_CHARS = 50;
        int PASSWORD_MAX_LIMIT_CHARS = 32;

        if (email.isEmpty()) {
            tEmailHelper.setText(R.string.empty_field);
        } else if (email.length() > 50) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + EMAIL_MAX_LIMIT_CHARS;
            tEmailHelper.setText(fieldMaxLimitMessage);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tEmailHelper.setText(R.string.email_not_valid);
        } else {
            isErrorEmail = false;
        }

        if (password.isEmpty()) {
            tPasswordHelper.setText(R.string.empty_field);
        } else if (password.length() > PASSWORD_MAX_LIMIT_CHARS) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + PASSWORD_MAX_LIMIT_CHARS;
            tPasswordHelper.setText(fieldMaxLimitMessage);
        } else {
            isErrorPassword = false;
        }

        if (isErrorEmail || isErrorPassword) {
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
}
