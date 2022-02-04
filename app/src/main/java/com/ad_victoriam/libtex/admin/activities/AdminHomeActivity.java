package com.ad_victoriam.libtex.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.books.BooksActivity;
import com.ad_victoriam.libtex.admin.activities.users.UsersActivity;
import com.ad_victoriam.libtex.common.activities.LoginActivity;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarState.get().setTitleMode(this, topAppBar, "Home");
        TopAppBarState.get().setAdminHome(this, topAppBar);

        findViewById(R.id.bViewUsers).setOnClickListener(this::viewUsers);
        findViewById(R.id.bViewBooks).setOnClickListener(this::viewBooks);
        findViewById(R.id.bViewSettings).setOnClickListener(this::viewSettings);
        findViewById(R.id.bSignOut).setOnClickListener(this::signOut);
    }

    private void signOut(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void viewSettings(View view) {
    }

    private void viewBooks(View view) {
        startActivity(new Intent(this, BooksActivity.class));
    }

    private void viewUsers(View view) {
        startActivity(new Intent(this, UsersActivity.class));
    }
}
