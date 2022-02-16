package com.ad_victoriam.libtex.user.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.models.Book;
import com.ad_victoriam.libtex.common.models.BookLoan;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.ad_victoriam.libtex.user.adapters.ActiveLoansAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ActiveLoansFragment extends Fragment {

    private DatabaseReference databaseReference;

    private MaterialButton bActiveLoans;
    private MaterialButton bAllLoans;
    private RecyclerView recyclerView;

    private ActiveLoansAdapter activeLoansAdapter;
    private final List<BookLoan> loans = new ArrayList<>();

    private View mainView;
    private FragmentActivity activity;

    private ChildEventListener loansListener;

    public ActiveLoansFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        mainView = inflater.inflate(R.layout.fragment_active_loans, container, false);

        activity = requireActivity();
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBarState.get().setNormalMode(activity, topAppBar);
        TopAppBarState.get().setTitleMode(activity, topAppBar, "Loans");

        initialize();

        return mainView;
    }

    private void initialize() {
        bActiveLoans = mainView.findViewById(R.id.bActiveLoans);
        bAllLoans = mainView.findViewById(R.id.bAllLoans);
        bAllLoans.setOnClickListener(this::switchToAll);

        recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        activeLoansAdapter = new ActiveLoansAdapter(getContext(), loans);
        recyclerView.setAdapter(activeLoansAdapter);

        setActiveUi();
        attachLoansListener();
    }

    private void setActiveUi() {
        bActiveLoans.setBackgroundColor(getResources().getColor(R.color.libtex_primary, activity.getTheme()));
        bActiveLoans.setTextColor(Color.WHITE);
        bAllLoans.setBackgroundColor(Color.WHITE);
        bAllLoans.setTextColor(Color.BLACK);
    }

    private void switchToAll(View view) {
        Navigation.findNavController(view).navigate(R.id.action_activeLoansFragment_to_allLoansFragment);
    }

    private void attachLoansListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        loansListener = databaseReference
                .child(getString(R.string.users))
                .child(currentUser.getUid())
                .child(getString(R.string.book_loans))
                .child(getString(R.string.current_loans))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            BookLoan bookLoan = dataSnapshot.getValue(BookLoan.class);
//                            if (!isFragmentAttached()) {
//                                return;
//                            }
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

                                                                if (!loans.contains(bookLoan)) {
                                                                    loans.add(bookLoan);
                                                                }
                                                                activeLoansAdapter.notifyItemInserted(loans.size() - 1);
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
                                int indexOfChangedLoan = -1;

                                for (BookLoan b: loans) {
                                    if (b.getBookLoanUid().equals(bookLoan.getBookLoanUid())) {
                                        indexOfChangedLoan = loans.indexOf(b);
                                        bookLoan.setBook(b.getBook());
                                        loans.set(indexOfChangedLoan, bookLoan);
                                        break;
                                    }
                                }
                                if (indexOfChangedLoan != -1) {
                                    activeLoansAdapter.notifyItemChanged(indexOfChangedLoan);
                                } else {
                                    activeLoansAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

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

                    private boolean isFragmentAttached() {
                        return isAdded() & activity != null;
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.removeEventListener(loansListener);
    }
}