package com.ad_victoriam.libtex.user.loans.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activity.book.BookLoan;
import com.ad_victoriam.libtex.common.model.Book;
import com.ad_victoriam.libtex.user.loans.LoanAdapter;
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

public class AllLoansFragment extends Fragment {

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;

    private LoanAdapter loanAdapter;
    private final List<BookLoan> bookLoans = new ArrayList<>();
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_loans, container, false);

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        String fragmentType = "ALL";
        loanAdapter = new LoanAdapter(context, bookLoans, fragmentType);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(loanAdapter);

        attachDatabaseBookLoansListener();

        return view;
    }

    private void attachDatabaseBookLoansListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference
                .child("users")
                .child(currentUser.getUid())
                .child("book-loans")
                .child("loans-history")
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

}
