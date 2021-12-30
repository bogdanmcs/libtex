package com.ad_victoriam.libtex.admin.activities.book;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.model.Book;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddBookActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    TextInputEditText eBookTitle;
    TextInputEditText eBookAuthorName;
    TextInputEditText eBookPublisher;
    TextInputEditText eBookNoOfPages;
    TextInputEditText eBookDescription;
    TextInputEditText eBookTotalQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_book);

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        eBookTitle = findViewById(R.id.eBookTitle);
        eBookAuthorName = findViewById(R.id.eBookAuthorName);
        eBookPublisher = findViewById(R.id.eBookPublisher);
        eBookNoOfPages = findViewById(R.id.eBookNoOfPages);
        eBookDescription = findViewById(R.id.eBookDescription);
        eBookTotalQuantity = findViewById(R.id.eBookTotalQuantity);

        final Button bAddBook = findViewById(R.id.bAdd);

        bAddBook.setOnClickListener(this::addBook);
    }

    private void addBook(View view) {

        String bookTitle = eBookTitle.getText().toString();
        String bookAuthorName = eBookAuthorName.getText().toString();
        String bookPublisher = eBookPublisher.getText().toString();
        String bookNoOfPages = eBookNoOfPages.getText().toString();
        String bookDescription = eBookDescription.getText().toString();
        String bookTotalQuantity = eBookTotalQuantity.getText().toString();

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

        String bookTotalQuantity = eBookTotalQuantity.getText().toString();
        if (bookTotalQuantity.isEmpty()) {
            eBookTotalQuantity.setError("Please fill this field.");
            eBookTotalQuantity.requestFocus();
            errorFlag = true;
        } else if (bookTotalQuantity.length() > 4) {
            eBookTotalQuantity.setError("Maximum length is 4 characters");
            eBookTotalQuantity.requestFocus();
            errorFlag = true;
        } else {
            try {
                if (Integer.parseInt(bookTotalQuantity) < 0 ) {
                    eBookTotalQuantity.setError("Total quantity cannot be negative");
                    eBookTotalQuantity.requestFocus();
                    errorFlag = true;
                }
            } catch (NumberFormatException e) {
                eBookTotalQuantity.setError("Total quantity must be a decimal number");
                eBookTotalQuantity.requestFocus();
                errorFlag = true;
            }
        }

        String bookNoOfPages = eBookNoOfPages.getText().toString();
        if (bookNoOfPages.isEmpty()) {
            eBookNoOfPages.setError("Please fill this field.");
            eBookNoOfPages.requestFocus();
            errorFlag = true;
        } else if (bookNoOfPages.length() > 4) {
            eBookNoOfPages.setError("Maximum length is 4 characters");
            eBookNoOfPages.requestFocus();
            errorFlag = true;
        } else {
            try {
                if (Integer.parseInt(bookNoOfPages) <= 0 ) {
                    eBookNoOfPages.setError("No. of pages must be positive");
                    eBookNoOfPages.requestFocus();
                    errorFlag = true;
                }
            } catch (NumberFormatException e) {
                eBookNoOfPages.setError("No of pages must be a decimal number");
                eBookNoOfPages.requestFocus();
                errorFlag = true;
            }
        }

        List<TextInputEditText> bookDetails = new ArrayList<>();
        bookDetails.add(eBookDescription);
        bookDetails.add(eBookPublisher);
        bookDetails.add(eBookAuthorName);
        bookDetails.add(eBookTitle);

        for (TextInputEditText bookDetail: bookDetails) {
            String textToString = bookDetail.getText().toString();
            if (textToString.isEmpty()) {
                bookDetail.setError("Please fill this field.");
                bookDetail.requestFocus();
                errorFlag = true;
            } else if (textToString.length() > 50) {
                bookDetail.setError("Maximum length is 50 characters");
                bookDetail.requestFocus();
                errorFlag = true;
            }
        }

        return !errorFlag;
    }


}
