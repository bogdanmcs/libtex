package com.ad_victoriam.libtex.admin.activities.book;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.model.Book;

public class BookDetailsActivity extends AppCompatActivity {

    private Book book;

    private TextView tTitle;
    private TextView tAuthorName;
    private TextView tPublisher;
    private TextView tNoOfPages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_book_details);

        tTitle = findViewById(R.id.tTitle);
        tAuthorName = findViewById(R.id.tAuthorName);
        tPublisher = findViewById(R.id.tPublisher);
        tNoOfPages = findViewById(R.id.tNoOfPages);

        if (getIntent().hasExtra("book")) {
            book = getIntent().getParcelableExtra("book");
            System.out.println("WILL SET SHIT TO:");
            System.out.println("book = {");
            System.out.println(book.getTitle() + " " + book.getAuthorName());
            System.out.println("}");

            tTitle.setText(book.getTitle());
            tAuthorName.setText(book.getAuthorName());
            tPublisher.setText(book.getPublisher());
            tNoOfPages.setText(book.getNoOfPages());
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }
}
