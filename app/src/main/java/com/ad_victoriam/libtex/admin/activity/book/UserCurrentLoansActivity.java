package com.ad_victoriam.libtex.admin.activity.book;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.model.Book;
import com.ad_victoriam.libtex.common.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserCurrentLoansActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private TextView loansStatus;

    private User user;
    private BookLoanAdapter bookLoanAdapter;
    private final List<BookLoan> bookLoans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_current_loans);

        loansStatus = findViewById(R.id.loansStatus);
        final String NO_BOOKS_FOUND = "No books found.";
        loansStatus.setText(NO_BOOKS_FOUND);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        user = getIntent().getParcelableExtra("user");
        bookLoanAdapter = new BookLoanAdapter(this, bookLoans, user);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookLoanAdapter);

        attachDatabaseBooksListener();
    }

    private void attachDatabaseBooksListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // orderByChild("name")
        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("current-loans")
                .child(currentUser.getUid())
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                BookLoan bookLoan = snapshot.getValue(BookLoan.class);

                if (bookLoan != null) {
                    // get book loan uid and get book details from "books/currentLib/bookLoanUid"
                    bookLoan.setBookLoanUid(snapshot.getKey());
                    loansStatus.setText("");
                    databaseReference
                            .child("books")
                            .child(currentUser.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                                Book book = dataSnapshot.getValue(Book.class);

                                                if (book != null && bookLoan.getBookUid().equals(dataSnapshot.getKey())) {
                                                    // got the book
                                                    book.setUid(dataSnapshot.getKey());
                                                    bookLoan.setBook(book);
                                                    if (!bookLoans.contains(bookLoan)) {
                                                        bookLoans.add(bookLoan);
                                                    }

                                                    bookLoanAdapter.notifyItemInserted(bookLoans.size() - 1);

                                                    break;
                                                }
                                            }
                                        } else {
                                            System.out.println(task.getResult().getValue());
                                        }
                                }
                            });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                BookLoan bookLoan = snapshot.getValue(BookLoan.class);

                if (bookLoan != null) {

                    bookLoan.setBookLoanUid(snapshot.getKey());

                    int indexOfChangedBookLoan = -1;
                    for (BookLoan b: bookLoans) {
                        if (b.getBookLoanUid().equals(bookLoan.getBookLoanUid())) {
                            indexOfChangedBookLoan = bookLoans.indexOf(b);
                            // keep book data - no need to query again
                            Book bookData = b.getBook();
                            bookLoan.setBook(bookData);
                            bookLoans.set(indexOfChangedBookLoan, bookLoan);
                            break;
                        }
                    }

                    if (indexOfChangedBookLoan != -1) {
                        bookLoanAdapter.notifyItemChanged(indexOfChangedBookLoan);
                    } else {
                        bookLoanAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                BookLoan bookLoan = snapshot.getValue(BookLoan.class);

                if (bookLoan != null) {
                    bookLoan.setBookLoanUid(snapshot.getKey());

                    int indexOfRemovedBookLoan = -1;
                    for (BookLoan b: bookLoans) {
                        if (b.getBookLoanUid().equals(bookLoan.getBookLoanUid())) {
                            indexOfRemovedBookLoan = bookLoans.indexOf(b);
                            bookLoans.remove(indexOfRemovedBookLoan);
                            break;
                        }
                    }

                    bookLoanAdapter.notifyDataSetChanged();
//                    if (indexOfRemovedBookLoan != -1) {
//                        bookLoanAdapter.notifyItemRemoved(indexOfRemovedBookLoan);
//                    } else {
//                        bookLoanAdapter.notifyDataSetChanged();
//                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
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
