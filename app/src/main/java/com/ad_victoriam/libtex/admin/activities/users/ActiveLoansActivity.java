package com.ad_victoriam.libtex.admin.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.common.models.BookLoan;
import com.ad_victoriam.libtex.admin.adapters.AdminActiveLoanAdapter;
import com.ad_victoriam.libtex.common.models.Book;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ActiveLoansActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private AdminActiveLoanAdapter adminActiveLoanAdapter;

    private TextView tRecordsCounter;
    private RecyclerView recyclerView;

    private User user;
    private final List<BookLoan> bookLoans = new ArrayList<>();
    private int recordsCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_active_loans);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarState.get().setChildMode(this, topAppBar);
        TopAppBarState.get().setTitleMode(this, topAppBar, "Active loans");
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

        tRecordsCounter = findViewById(R.id.tRecordsCounter);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        user = getIntent().getParcelableExtra("user");
        adminActiveLoanAdapter = new AdminActiveLoanAdapter(this, bookLoans, user);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminActiveLoanAdapter);

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
                    tRecordsCounter.setText("");
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
                                                        recordsCounter++;
                                                        String text = getResources().getString(R.string.records_found) + " " + recordsCounter;
                                                        tRecordsCounter.setText(text);
                                                    }
                                                    adminActiveLoanAdapter.notifyItemInserted(bookLoans.size() - 1);
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
                        adminActiveLoanAdapter.notifyItemChanged(indexOfChangedBookLoan);
                    } else {
                        adminActiveLoanAdapter.notifyDataSetChanged();
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
                            recordsCounter--;
                            String text;
                            if (recordsCounter > 0) {
                                text = getResources().getString(R.string.records_found) + " " + recordsCounter;
                            } else {
                                text = getResources().getString(R.string.no_records_found);
                            }
                            tRecordsCounter.setText(text);
                            break;
                        }
                    }
                    adminActiveLoanAdapter.notifyDataSetChanged();
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
}
