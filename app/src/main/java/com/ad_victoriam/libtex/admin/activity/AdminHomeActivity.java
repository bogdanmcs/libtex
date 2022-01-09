package com.ad_victoriam.libtex.admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activity.book.BooksActivity;
import com.ad_victoriam.libtex.admin.activity.user.UsersActivity;
import com.ad_victoriam.libtex.common.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.bLogOut:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            case R.id.bViewUsers:
                startActivity(new Intent(this, UsersActivity.class));
                break;

            case R.id.bViewBooks:
                startActivity(new Intent(this, BooksActivity.class));
                break;

            default:
        }
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
