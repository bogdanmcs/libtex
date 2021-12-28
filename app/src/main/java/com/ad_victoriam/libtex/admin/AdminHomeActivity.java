package com.ad_victoriam.libtex.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.auth.RegisterUserActivity;
import com.ad_victoriam.libtex.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogOut:
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.bViewBooks:
                startActivity(new Intent(this, BooksActivity.class));
                break;

            case R.id.bRegisterUser:
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;

            default:
        }
    }
}
