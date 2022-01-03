package com.ad_victoriam.libtex.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.auth.LoginActivity;
import com.ad_victoriam.libtex.user.loans.CurrentLoansActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

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
}
