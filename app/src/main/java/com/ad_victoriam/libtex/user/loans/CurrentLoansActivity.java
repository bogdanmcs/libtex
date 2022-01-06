package com.ad_victoriam.libtex.user.loans;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.book.BookLoan;
import com.ad_victoriam.libtex.admin.activities.book.BookLoanAdapter;
import com.ad_victoriam.libtex.model.Book;
import com.ad_victoriam.libtex.model.User;
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

public class CurrentLoansActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private LoanAdapter loanAdapter;

    private RecyclerView recyclerView;

    private User user;

    private final List<BookLoan> bookLoans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_current_loans);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        loanAdapter = new LoanAdapter(this, bookLoans);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(loanAdapter);

        attachDatabaseBookLoansListener();
    }

    private void attachDatabaseBookLoansListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference
                .child("users")
                .child(currentUser.getUid())
                .child("book-loans")
                .child("current-loans")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            BookLoan bookLoan = dataSnapshot.getValue(BookLoan.class);

                            // get the book
                            if (bookLoan != null) {
                                String libraryUid = snapshot.getKey();
                                bookLoan.setBookLoanUid(dataSnapshot.getKey());

                                databaseReference
                                        .child("books")
                                        .child(libraryUid)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                                if (task.isSuccessful()) {
                                                    for (DataSnapshot dataSnapshot1: task.getResult().getChildren()) {
                                                        Book book = dataSnapshot1.getValue(Book.class);

                                                        if (book != null) {
                                                            book.setUid(dataSnapshot1.getKey());

                                                            if (book.getUid().equals(bookLoan.getBookUid())) {
                                                                bookLoan.setBook(book);
                                                                bookLoan.setLibraryUid(libraryUid);

                                                                if (!bookLoans.contains(bookLoan)) {
                                                                    bookLoans.add(bookLoan);
                                                                }
                                                                loanAdapter.notifyItemInserted(bookLoans.size() - 1);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    System.out.println(task.getResult().getValue());
                                                }
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            BookLoan bookLoan = dataSnapshot.getValue(BookLoan.class);

                            if (bookLoan != null) {
                                bookLoan.setBookLoanUid(dataSnapshot.getKey());
                                int indexOfChangedBookLoan = -1;

                                for (BookLoan b: bookLoans) {
                                    if (b.getBookLoanUid().equals(bookLoan.getBookLoanUid())) {
                                        indexOfChangedBookLoan = bookLoans.indexOf(b);
                                        bookLoan.setBook(b.getBook());
                                        bookLoans.set(indexOfChangedBookLoan, bookLoan);
                                        break;
                                    }
                                }

                                if (indexOfChangedBookLoan != -1) {
                                    loanAdapter.notifyItemChanged(indexOfChangedBookLoan);
                                } else {
                                    loanAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    // todo
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

//                        BookLoan bookLoan = snapshot.getValue(BookLoan.class);
//
//                        if (bookLoan != null) {
//                            bookLoan.setBookLoanUid(snapshot.getKey());
//
//                            int indexOfRemovedBookLoan = -1;
//                            for (BookLoan b: bookLoans) {
//                                if (b.getBookLoanUid().equals(bookLoan.getBookLoanUid())) {
//                                    indexOfRemovedBookLoan = bookLoans.indexOf(b);
//                                    bookLoans.remove(indexOfRemovedBookLoan);
//                                }
//                            }
//
//                            if (indexOfRemovedBookLoan != -1) {
//                                bookLoanAdapter.notifyItemChanged(indexOfRemovedBookLoan);
//                            } else {
//                                bookLoanAdapter.notifyDataSetChanged();
//                            }
//                        }
                        }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
