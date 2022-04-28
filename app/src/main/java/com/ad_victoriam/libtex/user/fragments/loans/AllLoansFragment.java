package com.ad_victoriam.libtex.user.fragments.loans;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.adapters.ActiveLoansAdapter;
import com.ad_victoriam.libtex.user.adapters.AllLoansAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.Loan;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
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

public class AllLoansFragment extends Fragment {

    private DatabaseReference databaseReference;
    private View mainView;
    private FragmentActivity activity;

    private MaterialButton bActiveLoans;
    private MaterialButton bAllLoans;
    private RecyclerView recyclerView;

    private AllLoansAdapter allLoansAdapter;
    private final List<Loan> loans = new ArrayList<>();

    private ChildEventListener loansListener;

    private String searchQueryText = "";
    private boolean searchFilter;

    public AllLoansFragment() {
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

        mainView = inflater.inflate(R.layout.fragment_all_loans, container, false);
        activity = requireActivity();

        searchFilter = false;
        findViews();
        setSearchView();
        setTopAppBar();
        setLoans();

        return mainView;
    }

    private void findViews() {
        bActiveLoans = mainView.findViewById(R.id.bActiveLoans);
        bAllLoans = mainView.findViewById(R.id.bAllLoans);
        recyclerView = mainView.findViewById(R.id.recyclerView);
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Loans");
    }

    private void setLoans() {
        bActiveLoans.setOnClickListener(this::switchToActive);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        allLoansAdapter = new AllLoansAdapter(activity, loans);
        recyclerView.setAdapter(allLoansAdapter);

        setAllUi();
        attachLoansListener();
    }

    private void setAllUi() {
        bActiveLoans.setBackgroundColor(Color.WHITE);
        bActiveLoans.setTextColor(Color.BLACK);
        bAllLoans.setBackgroundColor(getResources().getColor(R.color.libtex_primary, activity.getTheme()));
        bAllLoans.setTextColor(Color.WHITE);
    }

    private void switchToActive(View view) {
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,
                new ActiveLoansFragment()).commit();
    }

    private void attachLoansListener() {
        loans.clear();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loansListener = databaseReference
                .child(getString(R.string.n_users))
                .child(currentUser.getUid())
                .child(getString(R.string.n_book_loans))
                .child(getString(R.string.n_loans_history))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {

                            Loan loan = dataSnapshot.getValue(Loan.class);
//                            if (!isFragmentAttached()) {
//                                return;
//                            }
                            // get the book
                            if (loan != null) {
                                String libraryUid = snapshot.getKey();
                                loan.setBookLoanUid(dataSnapshot.getKey());

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

                                                            if (book.getUid().equals(loan.getBookUid())) {
                                                                loan.setBook(book);
                                                                loan.setLibraryUid(libraryUid);

                                                                if (!loans.contains(loan)) {
                                                                    loans.add(loan);
                                                                }
                                                                if (!searchFilter) {
                                                                    allLoansAdapter.notifyItemInserted(loans.size() - 1);
                                                                } else {
                                                                    executeSearchQueryFilter(searchQueryText);
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Log.e("GET_BOOKS_BY_LIBRARY_UID", String.valueOf(task.getException()));
                                                }
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Loan loan = dataSnapshot.getValue(Loan.class);

                            if (loan != null) {
                                loan.setBookLoanUid(dataSnapshot.getKey());
                                int indexOfChangedLoan = -1;

                                for (Loan b: loans) {
                                    if (b.getBookLoanUid().equals(loan.getBookLoanUid())) {
                                        indexOfChangedLoan = loans.indexOf(b);
                                        loan.setBook(b.getBook());
                                        loans.set(indexOfChangedLoan, loan);
                                        break;
                                    }
                                }
                                if (!searchFilter) {
                                    if (indexOfChangedLoan != -1) {
                                        allLoansAdapter.notifyItemChanged(indexOfChangedLoan);
                                    } else {
                                        allLoansAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    executeSearchQueryFilter(searchQueryText);
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

    private void setSearchView() {
        SearchView searchView = mainView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                executeSearchQueryFilter(newText);
                return true;
            }
        });
    }

    private void executeSearchQueryFilter(String newText) {
        searchQueryText = newText;
        List<Loan> filteredLoans = new ArrayList<>();

        if (newText.isEmpty()) {
            searchFilter = false;
            filteredLoans = loans;
        } else {
            searchFilter = true;
            for (Loan loan: loans) {
                if (isNewTextSubstringOfLoanDetails(loan, newText)) {
                    filteredLoans.add(loan);
                }
            }
        }
        allLoansAdapter = new AllLoansAdapter(activity, filteredLoans);
        recyclerView.setAdapter(allLoansAdapter);
    }

    private boolean isNewTextSubstringOfLoanDetails(Loan loan, String newText) {
        if (loan.getReturnTimestamp() != null &&
            loan.getReturnTimestamp().toLowerCase().contains(newText.toLowerCase())) {
            return true;
        }

        return loan.getBook().getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                loan.getBook().getAuthorName().toLowerCase().contains(newText.toLowerCase()) ||
                loan.getBook().getPublisher().toLowerCase().contains(newText.toLowerCase()) ||
                loan.getLoanTimestamp().toLowerCase().contains(newText.toLowerCase());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.removeEventListener(loansListener);
    }
}