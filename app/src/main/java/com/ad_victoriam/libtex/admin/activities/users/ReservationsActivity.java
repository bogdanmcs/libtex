package com.ad_victoriam.libtex.admin.activities.users;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.admin.adapters.AdminReservationAdapter;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.user.adapters.ReservationsAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReservationsActivity extends AppCompatActivity {

    private User user;

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private AdminReservationAdapter reservationsAdapter;

    private final List<Reservation> reservations = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_reservations);

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        setTopAppBar();

        if (user == null && getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");

            setReservations();
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Reservations");
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

    private void setReservations() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reservations.clear();
        reservationsAdapter = new AdminReservationAdapter(this, reservations, user);
        recyclerView.setAdapter(reservationsAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference
                .child(getString(R.string.n_users))
                .child(user.getUid())
                .child(getString(R.string.n_reservations))
                .child(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                Reservation reservation = dataSnapshot.getValue(Reservation.class);

                                if (reservation != null) {
                                    reservation.setUid(dataSnapshot.getKey());
                                    reservation.setLibraryUid(currentUser.getUid());

                                    // get book
                                    databaseReference
                                            .child(getString(R.string.n_books))
                                            .child(reservation.getLibraryUid())
                                            .child(reservation.getBookUid())
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {

                                                    DataSnapshot dataSnapshot2 = task1.getResult();
                                                    Book book = dataSnapshot2.getValue(Book.class);

                                                    if (book != null) {
                                                        book.setUid(reservation.getBookUid());
                                                        reservation.setBook(book);
//                                                        findViewById(R.id.tNoReservations).setVisibility(View.GONE);

                                                        // set the location name
                                                        databaseReference
                                                                .child(this.getString(R.string.n_admins))
                                                                .child(reservation.getLibraryUid())
                                                                .child(this.getString(R.string.n_location))
                                                                .child(this.getString(R.string.p_location_name))
                                                                .get()
                                                                .addOnCompleteListener(task2 -> {
                                                                    if (task2.isSuccessful()) {

                                                                        DataSnapshot dataSnapshot1 = task2.getResult();
                                                                        String locationName = dataSnapshot1.getValue(String.class);

                                                                        if (locationName != null) {
                                                                            reservation.setLocationName(locationName);

                                                                            reservations.add(reservation);
                                                                            reservationsAdapter.notifyItemInserted(reservations.size() - 1);
                                                                        }
                                                                    } else {
                                                                        System.out.println(task2.getResult());
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    System.out.println(task1.getResult());
                                                }
                                            });
                                }
                        }
                    } else {
                        System.out.println(task.getResult());
                    }
                });
    }
}
