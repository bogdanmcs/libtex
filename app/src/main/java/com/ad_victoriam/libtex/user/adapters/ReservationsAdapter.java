package com.ad_victoriam.libtex.user.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.models.Reservation;

import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {

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
        return new ReservationsAdapter.ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ReservationViewHolder holder, int position) {
        holder.iBookImage.setOnClickListener(view -> viewBookDetails(view, position));
        holder.tBookTitle.setOnClickListener(view -> viewBookDetails(view, position));
        holder.tBookAuthorName.setOnClickListener(view -> viewBookDetails(view, position));
        holder.tBookPublisher.setOnClickListener(view -> viewBookDetails(view, position));

        holder.tBookTitle.setText(reservations.get(position).getBook().getTitle());
        holder.tBookAuthorName.setText(reservations.get(position).getBook().getAuthorName());
        holder.tBookPublisher.setText(reservations.get(position).getBook().getPublisher());
    }

    private void viewBookDetails(View view, int position) {
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {

        ImageView iBookImage;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;
        View div;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            iBookImage = itemView.findViewById(R.id.iBookImage);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
            div = itemView.findViewById(R.id.div);
        }
    }

}
