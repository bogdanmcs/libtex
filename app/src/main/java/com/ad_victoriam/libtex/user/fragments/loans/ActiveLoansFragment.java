package com.ad_victoriam.libtex.user.fragments.loans;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.adapters.ActiveLoansAdapter;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveLoansFragment extends Fragment {

    private DatabaseReference databaseReference;
    private View mainView;
    private FragmentActivity activity;

    private MaterialButton bActiveLoans;
    private MaterialButton bAllLoans;
    private TextView tNoLoans;
    private SearchView searchView;
    private RecyclerView recyclerView;

    private ActiveLoansAdapter activeLoansAdapter;
    private List<Loan> loans;

    private ValueEventListener loansListener;

    private String searchQueryText = "";
    private boolean searchFilter;

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
        tNoLoans = mainView.findViewById(R.id.tNoLoans);
        searchView = mainView.findViewById(R.id.searchView);
        recyclerView = mainView.findViewById(R.id.recyclerView);
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Loans");
    }

    private void setLoans() {
        bAllLoans.setOnClickListener(this::switchToAll);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        loans = new ArrayList<>();
        activeLoansAdapter = new ActiveLoansAdapter(activity, loans);
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
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,
                new AllLoansFragment()).commit();
    }

    private void attachLoansListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loansListener = databaseReference
                .child(getString(R.string.n_users))
                .child(currentUser.getUid())
                .child(getString(R.string.n_book_loans))
                .child(getString(R.string.n_current_loans))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        updateUi(snapshot.hasChildren());
                        for (DataSnapshot dataSnapshot0: snapshot.getChildren()) {
                            for (DataSnapshot dataSnapshot: dataSnapshot0.getChildren()) {
                                Loan loan = dataSnapshot.getValue(Loan.class);
                                // get the book
                                if (loan != null) {
                                    String libraryUid = dataSnapshot0.getKey();
                                    loan.setBookLoanUid(dataSnapshot.getKey());

                                    databaseReference
                                            .child(activity.getString(R.string.n_books))
                                            .child(libraryUid)
                                            .get()
                                            .addOnSuccessListener(task -> {
                                                for (DataSnapshot dataSnapshot1 : task.getChildren()) {

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
                                                                activeLoansAdapter.notifyItemInserted(loans.size() - 1);
                                                            } else {
                                                                executeSearchQueryFilter(searchQueryText);
                                                            }
                                                            break;
                                                        }
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("GET_BOOKS_BY_LIBRARY_UID", e.toString());
                                            });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("GET_USER_LOANS_HISTORY", error.toString());
                    }

                    private void updateUi(boolean isData) {
                        if (isData) {
                            tNoLoans.setVisibility(View.INVISIBLE);
                            searchView.setVisibility(View.VISIBLE);
                        } else {
                            tNoLoans.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.INVISIBLE);
                        }
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
        activeLoansAdapter = new ActiveLoansAdapter(activity, filteredLoans);
        recyclerView.setAdapter(activeLoansAdapter);
    }

    private boolean isNewTextSubstringOfLoanDetails(Loan loan, String newText) {
        return loan.getBook().getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                loan.getBook().getAuthorName().toLowerCase().contains(newText.toLowerCase()) ||
                loan.getBook().getPublisher().toLowerCase().contains(newText.toLowerCase()) ||
                loan.getLoanTimestamp().toLowerCase().contains(newText.toLowerCase()) ||
                loan.getDeadlineTimestamp().toLowerCase().contains(newText.toLowerCase());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.removeEventListener(loansListener);
    }
}