package com.ad_victoriam.libtex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.auth.LoginActivity;
import com.ad_victoriam.libtex.user.UserHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getEmail().endsWith("libtex.com")) {
                startActivity(new Intent(this, AdminHomeActivity.class));
            } else {
                startActivity(new Intent(this, UserHomeActivity.class));
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}