package com.ad_victoriam.libtex.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.adapters.BookAdapter;
import com.ad_victoriam.libtex.common.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private BookAdapter bookAdapter;

    private RecyclerView recyclerView;

    private List<Book> books = new ArrayList<>();

    private String intentAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_books);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        final FloatingActionButton bAddBook = findViewById(R.id.bAddUser);

        if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("BORROW")) {
            intentAction = "BORROW";
            bAddBook.setVisibility(View.GONE);
            bookAdapter = new BookAdapter(this, books, intentAction, getIntent().getParcelableExtra("user"));
        } else {
            intentAction = "NONE";
            bAddBook.setOnClickListener(this::addBook);
            bookAdapter = new BookAdapter(this, books, intentAction);
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        attachDatabaseBooksListener();
    }

    private void attachDatabaseBooksListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // orderByChild("name")
        databaseReference.child("books").child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Book book = snapshot.getValue(Book.class);

                // check if available quantity > 0
                if (book != null) {

                    if (!intentAction.equals("BORROW") || book.getAvailableQuantity() > 0) {

                        book.setUid(snapshot.getKey());

                        if (!books.contains(book)) {
                            books.add(book);
                        }

                        bookAdapter.notifyItemInserted(books.size() - 1);

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Book book = snapshot.getValue(Book.class);

                if (book != null) {

                    boolean isToBeRemoved = false;
                    if (intentAction.equals("BORROW") && book.getAvailableQuantity() == 0) {
                        isToBeRemoved = true;
                    }

                    book.setUid(snapshot.getKey());

                    int indexOfChangedBook = -1;
                    for (Book b : books) {
                        if (b.getUid().equals(book.getUid())) {
                            indexOfChangedBook = books.indexOf(b);
                            if (isToBeRemoved) {
                                books.remove(indexOfChangedBook);
                            } else {
                                books.set(indexOfChangedBook, book);
                            }
                            break;
                        }
                    }

                    bookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                Book book = snapshot.getValue(Book.class);

                if (book != null) {

                    book.setUid(snapshot.getKey());

                    int indexOfRemovedBook = -1;
                    for (Book b: books) {
                        if (b.getUid().equals(book.getUid())) {
                            indexOfRemovedBook = books.indexOf(b);
                            books.remove(indexOfRemovedBook);
                            break;
                        }
                    }

                    if (indexOfRemovedBook != -1) {
                        bookAdapter.notifyItemRemoved(indexOfRemovedBook);
                    } else {
                        bookAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addBook(View view) {
        if (!getIntent().hasExtra("action")) {
            startActivity(new Intent(this, AddBookActivity.class));
        }
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
