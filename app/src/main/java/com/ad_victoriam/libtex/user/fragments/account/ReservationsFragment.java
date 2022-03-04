package com.ad_victoriam.libtex.user.fragments.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.adapters.ReservationsAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.Reservation;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {

    private DatabaseReference databaseReference;

    private View mainView;
    private FragmentActivity activity;

    private RecyclerView recyclerView;
    private ReservationsAdapter reservationsAdapter;

    private final List<Reservation> reservations = new ArrayList<>();
    private boolean noReservations = false;

    public ReservationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        mainView = inflater.inflate(R.layout.fragment_reservations, container, false);
        activity = requireActivity();

        setTopAppBar();
        setReservations();

        return mainView;
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setChildMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Reservations");
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity
                        .getSupportFragmentManager()
                        .popBackStack();
            }
        });
    }

    private void setReservations() {
        recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        reservationsAdapter = new ReservationsAdapter(activity, reservations);
        recyclerView.setAdapter(reservationsAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference
                .child(getString(R.string.n_users))
                .child(currentUser.getUid())
                .child(getString(R.string.n_reservations))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {
                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                    Reservation reservation = dataSnapshot1.getValue(Reservation.class);

                                    if (reservation != null) {
                                        reservation.setLibraryUid(dataSnapshot.getKey());

                                        // get book
                                        databaseReference
                                                .child(getString(R.string.n_books))
                                                .child(reservation.getLibraryUid())
                                                .child(reservation.getBookUid())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                        if (task.isSuccessful()) {

                                                            DataSnapshot dataSnapshot2 = task.getResult();
                                                            Book book = dataSnapshot2.getValue(Book.class);

                                                            if (book != null) {
                                                                book.setUid(reservation.getBookUid());
                                                                reservation.setBook(book);
                                                                if(!noReservations) noReservations = true;
                                                                reservations.add(reservation);
                                                                reservationsAdapter.notifyItemInserted(reservations.size() - 1);
                                                            }

                                                        } else {
                                                            System.out.println(task.getResult());
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                            if (noReservations) {
                                TextView tNoReservations = mainView.findViewById(R.id.tNoReservations);
                                tNoReservations.setVisibility(View.VISIBLE);
                            }
                        } else {
                            System.out.println(task.getResult());
                        }
                    }
                });
    }
}
