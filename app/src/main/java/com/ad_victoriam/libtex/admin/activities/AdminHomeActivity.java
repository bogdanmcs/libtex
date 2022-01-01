package com.ad_victoriam.libtex.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.auth.RegisterUserActivity;
import com.ad_victoriam.libtex.admin.activities.book.BooksActivity;
import com.ad_victoriam.libtex.admin.activities.user.UsersActivity;
import com.ad_victoriam.libtex.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
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
}
