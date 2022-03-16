package com.ad_victoriam.libtex.admin.adapters;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.ad_victoriam.libtex.admin.activities.books.AdminBookDetailsActivity;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.common.utils.ReservationStatus;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminReservationAdapter extends RecyclerView.Adapter<AdminReservationAdapter.ReservationViewHolder> {

    private DatabaseReference databaseReference;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reservation, parent, false);
        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        return new AdminReservationAdapter.ReservationViewHolder(view);
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

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_check_circle_outline_24));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_completed));
            holder.bCancel.setVisibility(View.GONE);

        } else if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.APPROVED && reservationEndDate.isBefore(LocalDateTime.now())) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_cancel_24));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_cancelled));
            holder.bCancel.setVisibility(View.GONE);

        } else if (reservation.getStatus() == ReservationStatus.APPROVED) {
            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_flag_circle_24));
            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_approved));
            holder.bCancel.setOnClickListener(view -> confirmReservationCancellation(view, reservation, holder));
        }
        holder.bViewBookDetails.setOnClickListener(view -> viewBookDetails(reservation));
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

        Map<String, Object> reservationStatusUpdate =  new HashMap<>();

        reservationStatusUpdate.put(
                activity.getString(R.string.n_reservations_2) + "/" +
                reservation.getUid() + "/" +
                activity.getString(R.string.p_reservation_status),

                ReservationStatus.CANCELLED
        );
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
                            reservationStatusUpdate.put(
                                    activity.getString(R.string.n_books) + "/" +
                                    reservation.getLibraryUid() + "/" +
                                    reservation.getBookUid() + "/" +
                                    activity.getString(R.string.p_book_available_quantity),

                                    book.getAvailableQuantity() + 1
                            );
                            databaseReference
                                    .updateChildren(reservationStatusUpdate)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            holder.iReservationStatus.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_cancel_24));
                                            holder.tReservationStatus.setText(activity.getString(R.string.reservation_status_cancelled));
                                            holder.bCancel.setVisibility(View.GONE);
                                            Snackbar.make(view, "Reservation cancelled", Snackbar.LENGTH_SHORT).show();

                                        } else {
                                            System.out.println(task1.getResult());
                                        }
                                    });
                        }
                    } else {
                        System.out.println(task.getResult());
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
