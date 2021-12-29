package com.ad_victoriam.libtex.admin.activities.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

    private List<String> bookTitleList = new ArrayList<>();
    private List<String> bookAuthorNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_books);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        final FloatingActionButton bAddBook = findViewById(R.id.bAddBook);
        bAddBook.setOnClickListener(this::addBook);

        //
//        bookTitleList.add("titlul1");
//        bookTitleList.add("titlul2");
//        bookAuthorNameList.add("autorul1");
//        bookAuthorNameList.add("autorul2");

        recyclerView = findViewById(R.id.recyclerView);

        bookAdapter = new BookAdapter(this, bookTitleList, bookAuthorNameList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookAdapter);

        //
        attachDatabaseBooksListener();
    }

    private void attachDatabaseBooksListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // orderByChild("name")
        databaseReference.child("books").child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // default
                Book book = snapshot.getValue(Book.class);

                if (book != null) {
                    bookAdapter.addItemsToList(book.getTitle(), book.getAuthorName());
                    bookAdapter.notifyDataSetChanged();
                }

//                if (book != null) {
//                    booksViewer = booksViewer.concat(book.getTitle() + "\n");
//                    textViewBooks.setText(booksViewer);
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //
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
        switch (view.getId()) {
            case R.id.bAddBook:
                startActivity(new Intent(this, AddBookActivity.class));
                break;

            default:
        }
    }
}
