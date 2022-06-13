package com.ad_victoriam.libtex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.common.activities.LoginActivity;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.ad_victoriam.libtex.common.utils.ReservationStatus;
import com.ad_victoriam.libtex.user.activities.UserHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        turnOnDatabasePersistence();
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

    private void turnOnDatabasePersistence() {
        FirebaseDatabase.getInstance(this.getString(R.string.firebase_url)).setPersistenceEnabled(true);
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
                .addOnSuccessListener(task -> {
                    for (DataSnapshot dataSnapshot: task.getChildren()) {

                        Reservation reservation = dataSnapshot.getValue(Reservation.class);

                        if (reservation != null &&
                                reservation.getStatus() == ReservationStatus.APPROVED) {

                            reservation.setUid(dataSnapshot.getKey());
                            LocalDateTime reservationEndDate = LocalDateTime.parse(reservation.getEndDate());

                            if (reservationEndDate.isBefore(LocalDateTime.now())) {
                                updateReservationStatus(databaseReference, reservation);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GET_RESERVATIONS_DB", e.toString());
                });
    }

    private void updateReservationStatus(DatabaseReference databaseReference, Reservation reservation) {

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
                            databaseReference
                                    .updateChildren(childrenUpdates)
                                    .addOnSuccessListener(task1 -> {
                                        increaseBookAvailability(reference, reservation);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("UPDATE_RESERVATION_STATUS_DB", e.toString());
                                    });
                        }
                    } else {
                        Log.e("GET_BOOK_AVAILABLE_QTY_DB", String.valueOf(task.getException()));
                    }
                });
    }

    private void increaseBookAvailability(DatabaseReference databaseReference, Reservation reservation) {
        databaseReference
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                        Integer bookAvailableQuantity = mutableData.getValue(Integer.class);

                        if (bookAvailableQuantity == null) {
                            return Transaction.success(mutableData);
                        }

                        bookAvailableQuantity += 1;

                        // Set value and report transaction success
                        mutableData.setValue(bookAvailableQuantity);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed,
                                           DataSnapshot currentData) {
                        // Transaction completed
                        if (!committed) {
                            Log.e("BOOK_AVAILABILITY_UPDATE_ERROR", "postTransaction:onComplete:" + databaseError);
                        }
                    }
                });
    }
}