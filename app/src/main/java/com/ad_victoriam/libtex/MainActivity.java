package com.ad_victoriam.libtex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.common.activities.LoginActivity;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.ad_victoriam.libtex.common.utils.ReservationStatus;
import com.ad_victoriam.libtex.user.activities.UserHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* * * * * * * * * * * * * * * * * * * * */
        new Thread(new RefreshRunnable()).start();
        /* * * * * * * * * * * * * * * * * * * * */

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (currentUser.getEmail().endsWith("libtex.com")) {
                startActivity(new Intent(this, AdminHomeActivity.class));
            } else {
                startActivity(new Intent(this, UserHomeActivity.class));
            }
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }

    class RefreshRunnable implements Runnable {

        @Override
        public void run() {

            DatabaseReference databaseReference = FirebaseDatabase
                    .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference();

            while(true) {
                System.out.println("Running refreshable...");
                try {
                    refreshBookAvailability(databaseReference);
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshBookAvailability(DatabaseReference databaseReference) {
        databaseReference
                .child(this.getString(R.string.n_reservations_2))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                            Reservation reservation = dataSnapshot.getValue(Reservation.class);

                            if (reservation != null &&
                                    reservation.getStatus() == ReservationStatus.APPROVED) {

                                reservation.setUid(dataSnapshot.getKey());
                                LocalDateTime reservationEndDate = LocalDateTime.parse(reservation.getEndDate());

                                if (reservationEndDate.isBefore(LocalDateTime.now())) {
                                    updateDetails(databaseReference, reservation);
                                }
                            }
                        }
                    } else {
                        System.out.println(task.getResult());
                    }
                });
    }

    private void updateDetails(DatabaseReference databaseReference, Reservation reservation) {

        DatabaseReference reference = databaseReference
                .child(this.getString(R.string.n_books))
                .child(reservation.getLibraryUid())
                .child(reservation.getBookUid())
                .child(this.getString(R.string.p_book_available_quantity));
        reference
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        DataSnapshot dataSnapshot = task.getResult();
                        Integer availableQuantity = dataSnapshot.getValue(Integer.class);

                        if (availableQuantity != null) {

                            Map<String, Object> childrenUpdates =  new HashMap<>();

                            childrenUpdates.put(
                                    this.getString(R.string.n_reservations_2) + "/" +
                                    reservation.getUid() + "/" +
                                    this.getString(R.string.p_reservation_status),

                                    ReservationStatus.CANCELLED
                            );
                            childrenUpdates.put(
                                    this.getString(R.string.n_books) + "/" +
                                    reservation.getLibraryUid() + "/" +
                                    reservation.getBookUid() + "/" +
                                    this.getString(R.string.p_book_available_quantity),

                                    availableQuantity + 1
                            );
                            databaseReference
                                    .updateChildren(childrenUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (!task1.isSuccessful()) {
                                            System.out.println(task1.getResult());
                                        }
                                    });
                        }
                    } else {
                        System.out.println(task.getResult());
                    }
                });
    }
}