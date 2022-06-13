package com.ad_victoriam.libtex.admin.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.admin.adapters.AdminActiveLoanAdapter;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.admin.models.AdminLoan;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
import com.ad_victoriam.libtex.common.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ActiveLoansActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private AdminActiveLoanAdapter adminActiveLoanAdapter;

    private TextView tRecordsCounter;
    private RecyclerView recyclerView;

    private User user;
    private final List<AdminLoan> adminLoans = new ArrayList<>();
    private final List<AdminLoan> initAdminLoans = new ArrayList<>();

    private String searchQueryText = "";
    private boolean searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_active_loans);

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        if (getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");
        }
        searchFilter = false;
        findViews();
        setTopAppBar();
        setSearchView();
        setRecyclerView();
        getUserActiveLoans();
    }

    private void findViews() {
        tRecordsCounter = findViewById(R.id.tRecordsCounter);
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Active loans");
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
    }

    private void setRecyclerView() {
        adminActiveLoanAdapter = new AdminActiveLoanAdapter(this, adminLoans, user);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminActiveLoanAdapter);
    }

    private void setSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                executeSearchQueryFilter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && searchFilter) {
                    executeSearchQueryFilter(newText);
                }
                return true;
            }
        });
    }

    private void executeSearchQueryFilter(String newText) {
        searchQueryText = newText;
        adminLoans.clear();
        List<AdminLoan> filteredAdminLoans = new ArrayList<>();

        if (newText.isEmpty()) {
            searchFilter = false;
            adminLoans.addAll(initAdminLoans);
        } else {
            searchFilter = true;
            for (AdminLoan adminLoan: initAdminLoans) {
                if (isNewTextSubstringOfAdminLoan(adminLoan, newText)) {
                    filteredAdminLoans.add(adminLoan);
                }
            }
            adminLoans.addAll(filteredAdminLoans);
        }
        adminActiveLoanAdapter.notifyDataSetChanged();
    }

    private boolean isNewTextSubstringOfAdminLoan(AdminLoan adminLoan, String newText) {
        return adminLoan.getBook().getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                adminLoan.getBook().getAuthorName().toLowerCase().contains(newText.toLowerCase()) ||
                adminLoan.getBook().getPublisher().toLowerCase().contains(newText.toLowerCase()) ||
                adminLoan.getLoanTimestamp().toLowerCase().contains(newText.toLowerCase()) ||
                adminLoan.getDeadlineTimestamp().toLowerCase().contains(newText.toLowerCase());
    }

    private void getUserActiveLoans() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = databaseReference
                .child(this.getString(R.string.n_users))
                .child(user.getUid())
                .child(this.getString(R.string.n_book_loans))
                .child(this.getString(R.string.n_current_loans))
                .child(currentUser.getUid());
        query
                .get()
                .addOnSuccessListener(task -> {
                    updateUi(task.hasChildren());
                    for (DataSnapshot dataSnapshot: task.getChildren()) {

                        AdminLoan adminLoan = dataSnapshot.getValue(AdminLoan.class);

                        if (adminLoan != null) {
                            // get book loan uid and get book details from "books/currentLib/bookLoanUid"
                            adminLoan.setBookLoanUid(dataSnapshot.getKey());
                            Query query1 = databaseReference
                                    .child(this.getString(R.string.n_books))
                                    .child(currentUser.getUid());
                            query1
                                    .get()
                                    .addOnSuccessListener(task1 -> {
                                        for (DataSnapshot dataSnapshot1 : task1.getChildren()) {

                                            AdminBook adminBook = dataSnapshot1.getValue(AdminBook.class);

                                            if (adminBook != null && adminLoan.getBookUid().equals(dataSnapshot1.getKey())) {
                                                // got the book
                                                adminBook.setUid(dataSnapshot1.getKey());
                                                adminLoan.setBook(adminBook);
                                                if (!initAdminLoans.contains(adminLoan)) {
                                                    initAdminLoans.add(adminLoan);
                                                }
                                                if (!searchFilter) {
                                                    adminLoans.add(adminLoan);
                                                    adminActiveLoanAdapter.notifyItemInserted(adminLoans.size() - 1);
                                                } else {
                                                    executeSearchQueryFilter(searchQueryText);
                                                }
                                                break;
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("GET_LIBRARY_BOOKS_DB", e.toString());
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GET_USER_ACTIVE_LOANS_DB", e.toString());
                });
    }

    private void updateUi(boolean hasChildren) {
        if (hasChildren) {
            tRecordsCounter.setVisibility(View.GONE);
        } else {
            tRecordsCounter.setVisibility(View.VISIBLE);
            String noRecordsMessage = getApplicationContext().getString(R.string.no_records_found);
            tRecordsCounter.setText(noRecordsMessage);
        }
    }
}
