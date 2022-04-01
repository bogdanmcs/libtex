package com.ad_victoriam.libtex.admin.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.books.AdminBookDetailsActivity;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.admin.models.AdminLoan;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.common.utils.ReservationStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminReservationAdapter extends RecyclerView.Adapter<AdminReservationAdapter.ReservationViewHolder> {

    private DatabaseReference databaseReference;
    private View mainView;
    private final FragmentActivity activity;
    private final List<Reservation> reservations;
    private final User user;

    public AdminReservationAdapter(FragmentActivity activity, List<Reservation> reservations, User user) {
        this.activity = activity;
        this.reservations = reservations;
        this.user = user;
    }

    @NonNull
    @Override
    public AdminReservationAdapter.ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mainView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_reservation, parent, false);
        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        return new AdminReservationAdapter.ReservationViewHolder(mainView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReservationAdapter.ReservationViewHolder holder, int position) {

        Reservation reservation = reservations.get(position);

        String text = reservation.getBook().getTitle() + " - " +
                reservation.getBook().getAuthorName();
        holder.tBookDetails.setText(text);
        holder.tLocation.setText(reservation.getLocationName());

        LocalDateTime reservationEndDate = LocalDateTime.parse(reservation.getEndDate());

        DateFormat Date = DateFormat.getDateInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(reservationEndDate.getYear(), reservationEndDate.getMonthValue() - 1, reservationEndDate.getDayOfMonth());

        String reservationEndDateText = Date.format(calendar.getTime());
        reservationEndDateText = reservationEndDateText.concat(
                " " + reservationEndDate.getHour() + ":" + reservationEndDate.getMinute());

        holder.tExpirationDate.setText(reservationEndDateText);
        holder.bConfirmReservation.setEnabled(false);

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_check_circle_outline_50));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_completed));
            holder.bCancel.setVisibility(View.GONE);

        } else if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.APPROVED && reservationEndDate.isBefore(LocalDateTime.now())) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_cancel_50));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_cancelled));
            holder.bCancel.setVisibility(View.GONE);

        } else if (reservation.getStatus() == ReservationStatus.APPROVED) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_flag_circle_50));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_approved));
            holder.bCancel.setOnClickListener(view -> confirmReservationCancellation(view, reservation, holder));
            holder.bConfirmReservation.setEnabled(true);
            holder.bConfirmReservation.setBackgroundColor(ContextCompat.getColor(activity, R.color.confirm_reservation_green));
        }
        holder.bViewBookDetails.setOnClickListener(view -> viewBookDetails(reservation));
        holder.bConfirmReservation.setOnClickListener(view -> confirmReservation(view, reservation, holder));
    }

    private void confirmReservation(View view, Reservation reservation, ReservationViewHolder holder) {
        new AlertDialog.Builder(activity)
                .setMessage(
                        "Are you sure you want to confirm reservation by " +
                                user.getFullName() + " ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    assignBookToUser(view, reservation, holder);
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }

    private void assignBookToUser(View view, Reservation reservation, ReservationViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference
                .child(activity.getString(R.string.n_books))
                .child(reservation.getLibraryUid())
                .child(reservation.getBookUid())
                .child(activity.getString(R.string.p_book_available_quantity))
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        Integer bookAvailableQuantity = mutableData.getValue(Integer.class);

                        if (bookAvailableQuantity == null) {
                            return Transaction.success(mutableData);
                        }

                        bookAvailableQuantity -= 1;

                        // Set value and report transaction success
                        mutableData.setValue(bookAvailableQuantity);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot currentData) {
                        // Transaction completed
                        if (committed) {
                            AdminLoan adminLoan = new AdminLoan(reservation.getBookUid());
                            String bookLoanKey = databaseReference
                                    .child("users")
                                    .child(user.getUid())
                                    .child("book-loans")
                                    .push()
                                    .getKey();

                            databaseReference
                                    .child("users")
                                    .child(user.getUid())
                                    .child("book-loans")
                                    .child("current-loans")
                                    .child(currentUser.getUid())
                                    .child(bookLoanKey)
                                    .setValue(adminLoan);

                            databaseReference
                                    .child("users")
                                    .child(user.getUid())
                                    .child("book-loans")
                                    .child("loans-history")
                                    .child(currentUser.getUid())
                                    .child(bookLoanKey)
                                    .setValue(adminLoan);

                            updateReservationStatusCompleted(view, reservation, holder);

                            Snackbar.make(view, "Book assigned successfully", Snackbar.LENGTH_SHORT).show();

                        } else {
                            Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT).show();
                            Log.e("BOOK_AVAILABILITY_UPDATE_ERROR", "postTransaction:onComplete:" + databaseError);
                        }
                    }

                    private void updateReservationStatusCompleted(View view, Reservation reservation, AdminReservationAdapter.ReservationViewHolder holder) {
                        Map<String, Object> reservationStatusUpdate =  new HashMap<>();

                        reservationStatusUpdate.put(
                                activity.getString(R.string.n_reservations_2) + "/" +
                                        reservation.getUid() + "/" +
                                        activity.getString(R.string.p_reservation_status),

                                ReservationStatus.COMPLETED
                        );

                        databaseReference
                                .updateChildren(reservationStatusUpdate)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_check_circle_outline_50));
                                        holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_completed));
                                        holder.bConfirmReservation.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_grey));
                                        holder.bCancel.setVisibility(View.GONE);
                                        Snackbar.make(view, "Loan was successful", Snackbar.LENGTH_SHORT).show();

                                    } else {
                                        Log.e("RESERVATION_STATUS_UPDATE_ERROR", String.valueOf(task.getResult()));
                                    }
                                });
                    }
                });
    }

    private void confirmReservationCancellation(View view, Reservation reservation, AdminReservationAdapter.ReservationViewHolder holder) {
        new AlertDialog.Builder(activity)
                .setMessage("Are you sure you want to cancel this reservation ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {

                    if (reservation.getStatus() == ReservationStatus.APPROVED) {
                        cancelReservation(view, reservation, holder);
                    }
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }

    private void cancelReservation(View view, Reservation reservation, AdminReservationAdapter.ReservationViewHolder holder) {

        databaseReference
                .child(activity.getString(R.string.n_books))
                .child(reservation.getLibraryUid())
                .child(reservation.getBookUid())
                .child(activity.getString(R.string.p_book_available_quantity))
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
                        if (committed) {
                            updateReservationStatusCancelled(view, reservation, holder);

                        } else {
                            Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT).show();
                            Log.e("BOOK_AVAILABILITY_UPDATE_ERROR", "postTransaction:onComplete:" + databaseError);
                        }
                    }

                    private void updateReservationStatusCancelled(View view, Reservation reservation, AdminReservationAdapter.ReservationViewHolder holder) {
                        Map<String, Object> reservationStatusUpdate =  new HashMap<>();

                        reservationStatusUpdate.put(
                                activity.getString(R.string.n_reservations_2) + "/" +
                                reservation.getUid() + "/" +
                                activity.getString(R.string.p_reservation_status),

                                ReservationStatus.CANCELLED
                        );

                        databaseReference
                                .updateChildren(reservationStatusUpdate)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_cancel_24));
                                        holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_cancelled));
                                        holder.bCancel.setVisibility(View.GONE);
                                        Snackbar.make(view, "Reservation cancelled", Snackbar.LENGTH_SHORT).show();

                                    } else {
                                        Log.e("RESERVATION_STATUS_UPDATE_ERROR", String.valueOf(task.getResult()));
                                    }
                                });
                    }
                });
    }

    private void viewBookDetails(Reservation reservation) {
        databaseReference
                .child(activity.getString(R.string.n_books))
                .child(reservation.getLibraryUid())
                .child(reservation.getBookUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        DataSnapshot dataSnapshot = task.getResult();
                        AdminBook book = dataSnapshot.getValue(AdminBook.class);

                        if (book != null) {
                            Intent intent = new Intent(activity, AdminBookDetailsActivity.class);
                            intent.putExtra("book", book);
                            activity.startActivity(intent);
                        }
                    } else {
                        System.out.println(task.getResult());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {

        TextView tBookDetails;
        TextView tLocation;
        View div;
        ImageView iReservationStatus;
        TextView tReservationStatus;
        TextView tExpirationDate;
        MaterialButton bCancel;
        MaterialButton bViewBookDetails;
        MaterialButton bConfirmReservation;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tBookDetails = itemView.findViewById(R.id.tBookDetails);
            tLocation = itemView.findViewById(R.id.tLocation);
            div = itemView.findViewById(R.id.div);
            iReservationStatus = itemView.findViewById(R.id.iReservationStatus);
            tReservationStatus = itemView.findViewById(R.id.tReservationStatus);
            tExpirationDate = itemView.findViewById(R.id.tExpirationDate);
            bCancel = itemView.findViewById(R.id.bCancel);
            bViewBookDetails = itemView.findViewById(R.id.bViewBookDetails);
            bConfirmReservation = itemView.findViewById(R.id.bConfirmReservation);
        }
    }
}
