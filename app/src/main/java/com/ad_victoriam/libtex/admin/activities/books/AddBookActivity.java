package com.ad_victoriam.libtex.admin.activities.books;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.common.models.Book;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddBookActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    TextInputLayout layoutTitle;
    TextInputLayout layoutAuthorName;
    TextInputLayout layoutPublisher;
    TextInputLayout layoutNoOfPages;
    TextInputLayout layoutDescription;
    TextInputLayout layoutTotalQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_book);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarState.get().setChildMode(this, topAppBar);
        TopAppBarState.get().setTitleMode(this, topAppBar, "Add new book");
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

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        layoutTitle = findViewById(R.id.layoutTitle);
        layoutAuthorName = findViewById(R.id.layoutAuthorName);
        layoutPublisher = findViewById(R.id.layoutPublisher);
        layoutNoOfPages = findViewById(R.id.layoutNoOfPages);
        layoutDescription = findViewById(R.id.layoutDescription);
        layoutTotalQuantity = findViewById(R.id.layoutTotalQuantity);

        final Button bAddBook = findViewById(R.id.bConfirmAddition);

        bAddBook.setOnClickListener(this::addBook);
    }

    private void addBook(View view) {

        String bookTitle = layoutTitle.getEditText().getText().toString();
        String bookAuthorName = layoutAuthorName.getEditText().getText().toString();
        String bookPublisher = layoutPublisher.getEditText().getText().toString();
        String bookNoOfPages = layoutNoOfPages.getEditText().getText().toString();
        String bookDescription = layoutDescription.getEditText().getText().toString();
        String bookTotalQuantity = layoutTotalQuantity.getEditText().getText().toString();

        if (areBookDetailsValid()) {
            Book book = new Book(bookTitle, bookAuthorName, bookPublisher, bookNoOfPages, bookDescription, Integer.parseInt(bookTotalQuantity));
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference.child("books").child(currentUser.getUid()).push().setValue(book);
            Toast.makeText(getApplicationContext(), "Book added successfully.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean areBookDetailsValid() {
        boolean errorFlag = false;

        int DESCRIPTION_MAX_LIMIT = 200;
        int STANDARD_MAX_LIMIT = 50;
        int IDX_MAX_LIMIT = 4;

        String bookTotalQuantity = layoutTotalQuantity.getEditText().getText().toString();
        if (bookTotalQuantity.isEmpty()) {
            layoutTotalQuantity.setError(getString(R.string.empty_field));
            errorFlag = true;
        } else if (bookTotalQuantity.length() > 4) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + IDX_MAX_LIMIT;
            layoutTotalQuantity.setError(fieldMaxLimitMessage);
            errorFlag = true;
        } else {
            try {
                if (Integer.parseInt(bookTotalQuantity) < 0 ) {
                    layoutTotalQuantity.setError(getString(R.string.is_negative));
                    errorFlag = true;
                }
            } catch (NumberFormatException e) {
                layoutTotalQuantity.setError(getString(R.string.is_not_decimal));
                errorFlag = true;
            }
        }

        String bookNoOfPages = layoutNoOfPages.getEditText().getText().toString();
        if (bookNoOfPages.isEmpty()) {
            layoutNoOfPages.setError(getString(R.string.empty_field));
            errorFlag = true;
        } else if (bookNoOfPages.length() > 4) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + IDX_MAX_LIMIT;
            layoutNoOfPages.setError(fieldMaxLimitMessage);
            errorFlag = true;
        } else {
            try {
                if (Integer.parseInt(bookNoOfPages) <= 0 ) {
                    layoutNoOfPages.setError(getString(R.string.is_negative));
                    errorFlag = true;
                }
            } catch (NumberFormatException e) {
                layoutNoOfPages.setError(getString(R.string.is_not_decimal));
                errorFlag = true;
            }
        }

        String bookDescription = layoutDescription.getEditText().getText().toString();
        if (bookDescription.isEmpty()) {
            layoutDescription.setError(getString(R.string.empty_field));
            layoutDescription.requestFocus();
            errorFlag = true;
        } else if (bookDescription.length() > 200) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + DESCRIPTION_MAX_LIMIT;
            layoutDescription.setError(fieldMaxLimitMessage);
            errorFlag = true;
        }

        List<TextInputLayout> bookDetails = new ArrayList<>();
        bookDetails.add(layoutTitle);
        bookDetails.add(layoutAuthorName);
        bookDetails.add(layoutPublisher);

        for (TextInputLayout bookDetail: bookDetails) {
            String textToString = bookDetail.getEditText().getText().toString();
            if (textToString.isEmpty()) {
                bookDetail.setError(getString(R.string.empty_field));
                bookDetail.requestFocus();
                errorFlag = true;
            } else if (textToString.length() > 50) {
                String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
                bookDetail.setError(fieldMaxLimitMessage);
                errorFlag = true;
            }
        }

        return !errorFlag;
    }
}
