package com.ad_victoriam.libtex.admin.activities.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.book.BooksActivity;
import com.ad_victoriam.libtex.admin.activities.book.UserCurrentLoansActivity;
import com.ad_victoriam.libtex.model.User;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailsActivity extends AppCompatActivity {

    private User user;

    private TextView tEmail;
    private TextView tFirstName;
    private TextView tLastName;
    private TextView tIdCardSerialNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_details);

        tEmail = findViewById(R.id.tEmail);
        tFirstName = findViewById(R.id.tFirstName);
        tLastName = findViewById(R.id.tLastName);
        tIdCardSerialNumber = findViewById(R.id.tIDCardSerialNumber);

        if (user == null && getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");

            tEmail.setText(user.getEmail());
            tFirstName.setText(user.getFirstName());
            tLastName.setText(user.getLastName());
            tIdCardSerialNumber.setText(user.getIdCardSerialNumber());
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        final Button bDeleteUser = findViewById(R.id.bDeleteUser);
        final Button bAssignBook = findViewById(R.id.bAssignBook);
        final Button bReturnBook = findViewById(R.id.bReturnBook);
        final FloatingActionButton bEditUser = findViewById(R.id.bEditUser);
        bDeleteUser.setOnClickListener(this::deleteUser);
        bAssignBook.setOnClickListener(this::assignBook);
        bReturnBook.setOnClickListener(this::returnBook);
        bEditUser.setOnClickListener(this::editUser);
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
