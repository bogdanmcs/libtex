package com.ad_victoriam.libtex.admin.activities.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.model.Book;
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
    private SearchView searchView;

    private List<Book> books = new ArrayList<>();

    private String intentAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_books);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        searchView = findViewById(R.id.searchView);
        // ?

        final FloatingActionButton bAddBook = findViewById(R.id.bAddUser);
        bAddBook.setOnClickListener(this::addBook);

        if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("BORROW")) {
            intentAction = "BORROW";
            bookAdapter = new BookAdapter(this, books, intentAction, getIntent().getParcelableExtra("user"));
        } else {
            intentAction = "NONE";
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

                    if (!intentAction.equals("BORROW") || book.getAvailableQuantity() > 0) {

                        book.setUid(snapshot.getKey());

                        boolean foundBook = false;
                        int indexOfChangedBook = -1;
                        for (Book b: books) {
                            if (b.getUid().equals(book.getUid())) {
                                indexOfChangedBook = books.indexOf(b);
                                books.set(indexOfChangedBook, book);
                                foundBook = true;
                                break;
                            }
                        }

                        if (!foundBook) {
                            books.add(book);
                        }

                        if (indexOfChangedBook != -1) {
                            bookAdapter.notifyItemChanged(indexOfChangedBook);
                        } else {
                            if (!foundBook) {
                                bookAdapter.notifyItemChanged(books.size() - 1);
                            } else {
                                bookAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        // remove book because action = "BORROW", but available quantity = 0
                        onChildRemoved(snapshot);
                    }
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
                        }
                    }

                    if (indexOfRemovedBook != -1) {
                        bookAdapter.notifyItemChanged(indexOfRemovedBook);
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
}
