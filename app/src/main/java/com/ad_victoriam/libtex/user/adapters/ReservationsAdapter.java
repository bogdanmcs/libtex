package com.ad_victoriam.libtex.user.adapters;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.fragments.books.BookDetailsFragment;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.ad_victoriam.libtex.common.utils.ReservationStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
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

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

    private DatabaseReference databaseReference;
    private final FragmentActivity activity;
    private final List<Reservation> reservations;

    public ReservationsAdapter(FragmentActivity activity, List<Reservation> reservations) {
        this.activity = activity;
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ReservationsAdapter.ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reservation, parent, false);
        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        return new ReservationsAdapter.ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ReservationViewHolder holder, int position) {

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

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_check_circle_outline_50));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_completed));
            holder.bCancel.setVisibility(View.INVISIBLE);

        } else if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                   reservation.getStatus() == ReservationStatus.APPROVED && reservationEndDate.isBefore(LocalDateTime.now())) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_cancel_50));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_cancelled));
            holder.bCancel.setVisibility(View.INVISIBLE);

        } else if (reservation.getStatus() == ReservationStatus.APPROVED) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_flag_circle_50));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_approved));
            holder.bCancel.setVisibility(View.VISIBLE);
            holder.bCancel.setOnClickListener(view -> confirmReservationCancellation(view, reservation, holder));
        }
        holder.bViewBookDetails.setOnClickListener(view -> viewBookDetails(reservation));
    }

    private void confirmReservationCancellation(View view, Reservation reservation, ReservationViewHolder holder) {
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

    private void cancelReservation(View view, Reservation reservation, ReservationViewHolder holder) {

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
                            updateReservationStatus(view, reservation, holder);

                        } else {
                            Snackbar.make(view, "An error occurred", Snackbar.LENGTH_SHORT).show();
                            Log.e("BOOK_AVAILABILITY_UPDATE_ERROR", "postTransaction:onComplete:" + databaseError);
                        }
                    }

                    private void updateReservationStatus(View view, Reservation reservation, ReservationViewHolder holder) {
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

                                        reservation.setStatus(ReservationStatus.CANCELLED);
                                        holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_cancel_50));
                                        holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_cancelled));
                                        holder.bCancel.setVisibility(View.INVISIBLE);
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
                        Book book = dataSnapshot.getValue(Book.class);

                        if (book != null) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("book", book);

                            BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
                            bookDetailsFragment.setArguments(bundle);

                            activity
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainerView, bookDetailsFragment)
                                    .addToBackStack("reservationsFragment")
                                    .commit();
                        }
                    } else {
                        Log.e("GET_BOOK_BY_UID_DB", String.valueOf(task.getException()));
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
        }
    }
}
