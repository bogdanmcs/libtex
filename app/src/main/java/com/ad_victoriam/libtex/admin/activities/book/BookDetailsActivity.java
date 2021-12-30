package com.ad_victoriam.libtex.admin.activities.book;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.MainActivity;
import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.model.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookDetailsActivity extends AppCompatActivity {

    private Book book;

    private TextView tTitle;
    private TextView tAuthorName;
    private TextView tPublisher;
    private TextView tNoOfPages;
    private TextView tDescription;
    private TextView tAvailableQuantity;
    private TextView tTotalQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_book_details);

        tTitle = findViewById(R.id.tTitle);
        tAuthorName = findViewById(R.id.tAuthorName);
        tPublisher = findViewById(R.id.tPublisher);
        tNoOfPages = findViewById(R.id.tNoOfPages);
        tDescription = findViewById(R.id.tDescription);
        tAvailableQuantity = findViewById(R.id.tAvailableQuantity);
        tTotalQuantity = findViewById(R.id.tTotalQuantity);

        if (getIntent().hasExtra("book")) {
            book = getIntent().getParcelableExtra("book");

            tTitle.setText(book.getTitle());
            tAuthorName.setText(book.getAuthorName());
            tPublisher.setText(book.getPublisher());
            tNoOfPages.setText(book.getNoOfPages());
            tDescription.setText(book.getDescription());
            tAvailableQuantity.setText(String.valueOf(book.getAvailableQuantity()));
            tTotalQuantity.setText(String.valueOf(book.getTotalQuantity()));
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        final Button bDeleteBook = findViewById(R.id.bDeleteBook);
        bDeleteBook.setOnClickListener(this::deleteBook);
    }

    private void deleteBook(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to remove this book?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    // verify if deletion is successful or not
                    databaseReference.child("books").child(currentUser.getUid()).child(book.getUid()).removeValue();
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }
}
