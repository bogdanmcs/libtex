package com.ad_victoriam.libtex.admin.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.models.User;

import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailsActivity extends AppCompatActivity {

    private User user;

    private TextView eEmail;
    private TextView eFullName;
    private TextView eIdCardSeries;
    private TextView eIdCardNumber;
    private TextView eDob;
    private TextView ePhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_details);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarState.get().setChildMode(this, topAppBar);
        TopAppBarState.get().setTitleMode(this, topAppBar, "User details");
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                }
                return false;
            }
        });

        eEmail = findViewById(R.id.eEmail);
        eFullName = findViewById(R.id.eFullName);
        eIdCardSeries = findViewById(R.id.eIdCardSeries);
        eIdCardNumber = findViewById(R.id.eIdCardNumber);
        eDob = findViewById(R.id.eDob);
        ePhoneNumber = findViewById(R.id.ePhoneNumber);
        eEmail.setFocusable(false);
        eFullName.setFocusable(false);
        eIdCardSeries.setFocusable(false);
        eIdCardNumber.setFocusable(false);
        eDob.setFocusable(false);
        ePhoneNumber.setFocusable(false);

        if (user == null && getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");

            eEmail.setText(user.getEmail());
            eFullName.setText(user.getFullName());
            eIdCardSeries.setText(user.getIdCardSeries());
            eIdCardNumber.setText(user.getIdCardNumber());
            eDob.setText(user.getDateOfBirthday());
            ePhoneNumber.setText(user.getPhoneNumber());
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        final Button bDeleteUser = findViewById(R.id.bDeleteUser);
        final Button bAssignBook = findViewById(R.id.bAssignBook);
        final Button bReturnBook = findViewById(R.id.bReturnBook);
//        final FloatingActionButton bEditUser = findViewById(R.id.bEditUser);
        bDeleteUser.setOnClickListener(this::deleteUser);
        bAssignBook.setOnClickListener(this::assignBook);
        bReturnBook.setOnClickListener(this::returnBook);
//        bEditUser.setOnClickListener(this::editUser);
    }

    private void deleteUser(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to remove this user?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    // verify if deletion is successful or not
                    databaseReference.child("users").child(user.getUid()).removeValue();
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }

    private void assignBook(View view) {
        Intent intent = new Intent(this, BooksActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("action", "BORROW");
        startActivity(intent);
    }

    private void returnBook(View view) {
        // view current loaned books for this user
        Intent intent = new Intent(this, UserCurrentLoansActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void editUser(View view) {
//        startActivity(new Intent(this, EditUserActivity.class));
    }


}