package com.ad_victoriam.libtex.admin.activities.books;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        tTitle = findViewById(R.id.tTitle);
        tAuthorName = findViewById(R.id.tAuthorName);
        tPublisher = findViewById(R.id.tPublisher);
        tNoOfPages = findViewById(R.id.tNoOfPages);
        tAvailableQuantity = findViewById(R.id.tAvailableQuantity);
        tTotalQuantity = findViewById(R.id.tTotalQuantity);
        tDescription = findViewById(R.id.tDescription);

        if (book == null && getIntent().hasExtra("book")) {
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
        final FloatingActionButton bEditBook = findViewById(R.id.bEditBook);
        bDeleteBook.setOnClickListener(this::deleteBook);
        bEditBook.setOnClickListener(this::editBook);
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

    private void editBook(View view) {
        startActivity(new Intent(this, EditBookActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);
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
