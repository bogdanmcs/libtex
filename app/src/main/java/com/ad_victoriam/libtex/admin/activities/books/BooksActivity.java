package com.ad_victoriam.libtex.admin.activities.books;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private AdminBookAdapter adminBookAdapter;

    private TextView tRecordsCounter;
    private RecyclerView recyclerView;

    private final List<AdminBook> adminBooks = new ArrayList<>();
    private int recordsCounter = 0;
    private String intentAction;

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

        tRecordsCounter = findViewById(R.id.tRecordsCounter);
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
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminBookAdapter);

        attachDatabaseBooksListener();
    }

    private void attachDatabaseBooksListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // orderByChild("name")
        databaseReference.child("books").child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                AdminBook adminBook = snapshot.getValue(AdminBook.class);

                // check if available quantity > 0
                if (adminBook != null) {

                    if (!intentAction.equals("BORROW") || adminBook.getAvailableQuantity() > 0) {

                        adminBook.setUid(snapshot.getKey());

                        if (!adminBooks.contains(adminBook)) {
                            adminBooks.add(adminBook);
                            recordsCounter++;
                            String text = getResources().getString(R.string.records_found) + " " + recordsCounter;
                            tRecordsCounter.setText(text);
                        }
                        adminBookAdapter.notifyItemInserted(adminBooks.size() - 1);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                AdminBook adminBook = snapshot.getValue(AdminBook.class);

                if (adminBook != null) {

                    boolean isToBeRemoved = false;
                    if (intentAction.equals("BORROW") && adminBook.getAvailableQuantity() == 0) {
                        isToBeRemoved = true;
                    }

                    adminBook.setUid(snapshot.getKey());

                    int indexOfChangedBook = -1;
                    for (AdminBook b : adminBooks) {
                        if (b.getUid().equals(adminBook.getUid())) {
                            indexOfChangedBook = adminBooks.indexOf(b);
                            if (isToBeRemoved) {
                                adminBooks.remove(indexOfChangedBook);
                                recordsCounter--;
                                String text;
                                if (recordsCounter > 0) {
                                    text = getResources().getString(R.string.records_found) + " " + recordsCounter;
                                } else {
                                    text = getResources().getString(R.string.no_records_found);
                                }
                                tRecordsCounter.setText(text);
                            } else {
                                adminBooks.set(indexOfChangedBook, adminBook);
                            }
                            break;
                        }
                    }
                    adminBookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                AdminBook adminBook = snapshot.getValue(AdminBook.class);

                if (adminBook != null) {

                    adminBook.setUid(snapshot.getKey());

                    int indexOfRemovedBook = -1;
                    for (AdminBook b: adminBooks) {
                        if (b.getUid().equals(adminBook.getUid())) {
                            indexOfRemovedBook = adminBooks.indexOf(b);
                            adminBooks.remove(indexOfRemovedBook);
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
                    if (indexOfRemovedBook != -1) {
                        adminBookAdapter.notifyItemRemoved(indexOfRemovedBook);
                    } else {
                        adminBookAdapter.notifyDataSetChanged();
                    }
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

    public void addNewBook() {
        if (!getIntent().hasExtra("action")) {
            startActivity(new Intent(this, AddBookActivity.class));
        }
    }
}
