package com.ad_victoriam.libtex.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button bViewCurrentLoans = findViewById(R.id.bViewCurrentLoans);
        final Button bLogOut = findViewById(R.id.bLogOut);
        bViewCurrentLoans.setOnClickListener(this::viewCurrentLoans);
        bLogOut.setOnClickListener(this::logOut);

    }

    private void viewCurrentLoans(View view) {
        startActivity(new Intent(this, CurrentLoansActivity.class));
    }

    private void logOut(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.search);
//        menuItem.setVisible(false);
        return true;
    }
}
