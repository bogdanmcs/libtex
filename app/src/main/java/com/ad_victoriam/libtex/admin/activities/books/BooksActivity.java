package com.ad_victoriam.libtex.admin.activities.books;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.adapters.AdminBookAdapter;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private AdminBookAdapter adminBookAdapter;

    private SearchView searchView;
    private RecyclerView recyclerView;

    private List<AdminBook> adminBooks = new ArrayList<>();
    private List<AdminBook> initAdminBooks = new ArrayList<>();
    private String intentAction;

    private String searchQueryText = "";
    private boolean searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_books);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.addNew) {
                    addNewBook();
                }
                return false;
            }
        });
        ActionMenuItemView addNewItem = topAppBar.findViewById(R.id.addNew);

        if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("BORROW")) {
            intentAction = "BORROW";
            addNewItem.setVisibility(View.GONE);
            TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Assign book");
            adminBookAdapter = new AdminBookAdapter(this, adminBooks, intentAction, getIntent().getParcelableExtra("user"));
        } else {
            intentAction = "NONE";
            TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Books");
            adminBookAdapter = new AdminBookAdapter(this, adminBooks, intentAction);
        }
        searchFilter = false;
        setSearchView();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminBookAdapter);

        getBooks();
    }

    private void setSearchView() {
        searchView = findViewById(R.id.searchView);
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
        adminBooks.clear();
        List<AdminBook> filteredAdminBooks = new ArrayList<>();

        if (newText.isEmpty()) {
            searchFilter = false;
            adminBooks.addAll(initAdminBooks);
        } else {
            searchFilter = true;
            for (AdminBook adminBook: initAdminBooks) {
                if (isNewTextSubstringOfAdminBook(adminBook, newText)) {
                    filteredAdminBooks.add(adminBook);
                }
            }
            adminBooks.addAll(filteredAdminBooks);
        }
        adminBookAdapter.notifyDataSetChanged();
    }

    private boolean isNewTextSubstringOfAdminBook(AdminBook adminBook, String newText) {
        return adminBook.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                adminBook.getAuthorName().toLowerCase().contains(newText.toLowerCase()) ||
                adminBook.getPublisher().toLowerCase().contains(newText.toLowerCase());
    }

    private void getBooks() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = databaseReference
                .child(this.getString(R.string.n_books))
                .child(currentUser.getUid());
        query
                .get()
                .addOnSuccessListener(task -> {
                    for (DataSnapshot dataSnapshot: task.getChildren()) {

                        AdminBook adminBook = dataSnapshot.getValue(AdminBook.class);

                        if (adminBook != null) {

                            // check if available quantity > 0
                            if (!intentAction.equals("BORROW") || adminBook.getAvailableQuantity() > 0) {

                                adminBook.setUid(dataSnapshot.getKey());

                                if (!initAdminBooks.contains(adminBook)) {
                                    initAdminBooks.add(adminBook);
                                }
                                if (!searchFilter) {
                                    adminBooks.add(adminBook);
                                    adminBookAdapter.notifyItemInserted(adminBooks.size() - 1);
                                } else {
                                    executeSearchQueryFilter(searchQueryText);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GET_BOOKS_DB", e.toString());
                });
    }

    public void addNewBook() {
        if (!getIntent().hasExtra("action")) {
            startActivity(new Intent(this, AddBookActivity.class));
        }
    }
}
