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
import com.ad_victoriam.libtex.admin.adapters.AdminAllLoanAdapter;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.admin.models.AdminLoan;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
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

public class AllLoansActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private AdminAllLoanAdapter adminAllLoanAdapter;

    private TextView tRecordsCounter;
    private RecyclerView recyclerView;

    private User user;
    private final List<AdminLoan> adminLoans = new ArrayList<>();
    private int recordsCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_loans);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        TopAppBarAdmin.get().setTitleMode(this, topAppBar, "All loans");
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
        adminAllLoanAdapter = new AdminAllLoanAdapter(this, adminLoans, user);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adminAllLoanAdapter);

        attachDatabaseBooksListener();
    }

    private void attachDatabaseBooksListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // orderByChild("name")
        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("loans-history")
                .child(currentUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        AdminLoan adminLoan = snapshot.getValue(AdminLoan.class);

                        if (adminLoan != null) {
                            // get book loan uid and get book details from "books/currentLib/bookLoanUid"
                            adminLoan.setBookLoanUid(snapshot.getKey());
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

                                                    AdminBook adminBook = dataSnapshot.getValue(AdminBook.class);

                                                    if (adminBook != null && adminLoan.getBookUid().equals(dataSnapshot.getKey())) {
                                                        // got the book
                                                        adminBook.setUid(dataSnapshot.getKey());
                                                        adminLoan.setBook(adminBook);
                                                        if (!adminLoans.contains(adminLoan)) {
                                                            adminLoans.add(adminLoan);
                                                            recordsCounter++;
                                                            String text = getResources().getString(R.string.records_found) + " " + recordsCounter;
                                                            tRecordsCounter.setText(text);
                                                        }
                                                        adminAllLoanAdapter.notifyItemInserted(adminLoans.size() - 1);
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

                        AdminLoan adminLoan = snapshot.getValue(AdminLoan.class);

                        if (adminLoan != null) {

                            adminLoan.setBookLoanUid(snapshot.getKey());

                            int indexOfChangedBookLoan = -1;
                            for (AdminLoan b: adminLoans) {
                                if (b.getBookLoanUid().equals(adminLoan.getBookLoanUid())) {
                                    indexOfChangedBookLoan = adminLoans.indexOf(b);
                                    // keep book data - no need to query again
                                    AdminBook adminBookData = b.getBook();
                                    adminLoan.setBook(adminBookData);
                                    adminLoans.set(indexOfChangedBookLoan, adminLoan);
                                    break;
                                }
                            }

                            if (indexOfChangedBookLoan != -1) {
                                adminAllLoanAdapter.notifyItemChanged(indexOfChangedBookLoan);
                            } else {
                                adminAllLoanAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        AdminLoan adminLoan = snapshot.getValue(AdminLoan.class);

                        if (adminLoan != null) {
                            adminLoan.setBookLoanUid(snapshot.getKey());

                            int indexOfRemovedBookLoan = -1;
                            for (AdminLoan b: adminLoans) {
                                if (b.getBookLoanUid().equals(adminLoan.getBookLoanUid())) {
                                    indexOfRemovedBookLoan = adminLoans.indexOf(b);
                                    adminLoans.remove(indexOfRemovedBookLoan);
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
                            adminAllLoanAdapter.notifyDataSetChanged();
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
