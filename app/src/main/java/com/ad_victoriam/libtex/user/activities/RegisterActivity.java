package com.ad_victoriam.libtex.user.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarState.get().setChildMode(this, topAppBar);
        TopAppBarState.get().setTitleMode(this, topAppBar, "Registration");
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
                            Intent intent = new Intent();
                            intent.putExtra("success", "true");
                            setResult(66, intent);
                            finish();
                        } else {

                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthUserCollisionException e) {
                                TextView tEmailHelper = findViewById(R.id.tEmailHelper);
                                tEmailHelper.setText(R.string.email_in_use);
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
        CheckBox cTap = findViewById(R.id.cTap);

        TextView tEmailHelper = findViewById(R.id.tEmailHelper);
        TextView tPasswordHelper = findViewById(R.id.tPasswordHelper);
        TextView tTapHelper = findViewById(R.id.tTapHelper);
        tEmailHelper.setText("");
        tPasswordHelper.setText("");
        tTapHelper.setText("");

        boolean isErrorEmail = true;
        boolean isErrorPassword = true;
        boolean isTapNotChecked = true;
        int EMAIL_MAX_LIMIT_CHARS = 50;
        int PASSWORD_MIN_LIMIT_CHARS = 6;
        int PASSWORD_MAX_LIMIT_CHARS = 32;

        if (email.isEmpty()) {
            tEmailHelper.setText(R.string.empty_field);
        } else if (email.length() > EMAIL_MAX_LIMIT_CHARS) {
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
        } else if (password.length() < PASSWORD_MIN_LIMIT_CHARS){
            String fieldMinLimitMessage = getString(R.string.field_min_limit) + " " + PASSWORD_MIN_LIMIT_CHARS;
            tPasswordHelper.setText(fieldMinLimitMessage);
        } else {
            isErrorPassword = false;
        }

        if (!cTap.isChecked()) {
            tTapHelper.setText(R.string.terms_and_privacy_not_checked);
        } else {
            isTapNotChecked = false;
        }

        if (isErrorEmail || isErrorPassword || isTapNotChecked) {
            return;
        }

        createAccount(email, password);
    }

    private void storeDataIntoDatabase(FirebaseUser firebaseUser) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        User user = new User(firebaseUser.getEmail());
        databaseReference.child("unverified-users").child(firebaseUser.getUid()).setValue(user);
    }
}
