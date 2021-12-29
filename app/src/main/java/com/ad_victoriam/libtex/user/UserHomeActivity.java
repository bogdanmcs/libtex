package com.ad_victoriam.libtex.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bLogOut:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            default:
        }
    }
}
