package com.ad_victoriam.libtex.user.fragments.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.adapters.BooksAdapter;
import com.ad_victoriam.libtex.user.adapters.ReservationsAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class ReservationsFragment extends Fragment {

    private DatabaseReference databaseReference;

    private View mainView;
    private FragmentActivity activity;

    private RecyclerView recyclerView;

    private ReservationsAdapter reservationsAdapter;
    private final List<Reservation> reservations = new ArrayList<>();

    private boolean initReservations;
    private String searchQueryText = "";

    private boolean searchFilter;

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

        initReservations = false;
        searchFilter = false;

        setTopAppBar();
        setReservations();
        setSearchView();

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
        reservations.clear();
        reservationsAdapter = new ReservationsAdapter(activity, reservations);
        recyclerView.setAdapter(reservationsAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference
                .child(getString(R.string.n_reservations_2))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Iterable<DataSnapshot> dataSnapshots = task.getResult().getChildren();
                        long reservationsSize = StreamSupport.stream(dataSnapshots.spliterator(), false).count();

                        if (reservationsSize == 0) {
                            mainView.findViewById(R.id.tNoReservations).setVisibility(View.VISIBLE);

                        } else {
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                Reservation reservation = dataSnapshot.getValue(Reservation.class);

                                if (reservation != null &&
                                        reservation.getUserUid().equals(currentUser.getUid())) {

                                    reservation.setUid(dataSnapshot.getKey());
                                    // get book
                                    databaseReference
                                            .child(getString(R.string.n_books))
                                            .child(reservation.getLibraryUid())
                                            .child(reservation.getBookUid())
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {

                                                    DataSnapshot dataSnapshot1 = task1.getResult();
                                                    Book book = dataSnapshot1.getValue(Book.class);

                                                    if (book != null) {
                                                        book.setUid(reservation.getBookUid());
                                                        reservation.setBook(book);

                                                        // set the location name
                                                        databaseReference
                                                                .child(activity.getString(R.string.n_admins))
                                                                .child(reservation.getLibraryUid())
                                                                .child(activity.getString(R.string.n_location))
                                                                .child(activity.getString(R.string.p_location_name))
                                                                .get()
                                                                .addOnCompleteListener(task2 -> {
                                                                    if (task2.isSuccessful()) {

                                                                        DataSnapshot dataSnapshot2 = task2.getResult();
                                                                        String locationName = dataSnapshot2.getValue(String.class);

                                                                        if (locationName != null) {
                                                                            reservation.setLocationName(locationName);

                                                                            mainView.findViewById(R.id.tNoReservations).setVisibility(View.INVISIBLE);

                                                                            reservations.add(reservation);
                                                                            if (!searchFilter) {
                                                                                reservationsAdapter.notifyItemInserted(reservations.size() - 1);
                                                                            } else {
                                                                                executeSearchQueryFilter(searchQueryText);
                                                                            }
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
                        }
                    } else {
                        System.out.println(task.getResult());
                    }
                });
    }

    private void setSearchView() {
        SearchView searchView = mainView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                executeSearchQueryFilter(newText);
                return true;
            }
        });
    }

    private void executeSearchQueryFilter(String newText) {
        searchQueryText = newText;
        List<Reservation> filteredReservations = new ArrayList<>();

        if (newText.isEmpty()) {
            searchFilter = false;
            filteredReservations = reservations;
        } else {
            searchFilter = true;
            for (Reservation reservation: reservations) {
                if (isNewTextSubstringOfReservationDetails(reservation, newText)) {
                    filteredReservations.add(reservation);
                }
            }
        }
        reservationsAdapter = new ReservationsAdapter(activity, filteredReservations);
        recyclerView.setAdapter(reservationsAdapter);
        reservationsAdapter.notifyDataSetChanged();
    }

    private boolean isNewTextSubstringOfReservationDetails(Reservation reservation, String newText) {
        return reservation.getBook().getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                reservation.getBook().getAuthorName().toLowerCase().contains(newText.toLowerCase()) ||
                reservation.getLocationName().toLowerCase().contains(newText.toLowerCase()) ||
                reservation.getEndDate().toLowerCase().contains(newText.toLowerCase()) ||
                reservation.getStatus().toString().toLowerCase().contains(newText.toLowerCase());
    }

//    private void doPostDataOperations() {
//        initReservations = true;
//        if (!searchQueryText.isEmpty()) {
//            executeSearchQueryFilter(searchQueryText);
//        }
//        reservationsAdapter.notifyDataSetChanged();
//    }
}
