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

        final Button bAddBook = findViewById(R.id.bAdd);

        bAddBook.setOnClickListener(this::addBook);
    }

    private void addBook(View view) {

        String bookTitle = eBookTitle.getText().toString();
        String bookAuthorName = eBookAuthorName.getText().toString();
        String bookPublisher = eBookPublisher.getText().toString();
        String bookNoOfPages = eBookNoOfPages.getText().toString();

        if (areBookDetailsValid()) {
            Book book = new Book(bookTitle, bookAuthorName, bookPublisher, bookNoOfPages);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference.child("books").child(currentUser.getUid()).push().setValue(book);
            Toast.makeText(getApplicationContext(), "Book added successfully.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean areBookDetailsValid() {
        boolean errorFlag = false;

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
                Integer.parseInt(bookNoOfPages);
            } catch (NumberFormatException e) {
                eBookNoOfPages.setError("No of pages must be a decimal number");
                eBookNoOfPages.requestFocus();
                errorFlag = true;
            }
        }

        List<TextInputEditText> bookDetails = new ArrayList<>();
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
