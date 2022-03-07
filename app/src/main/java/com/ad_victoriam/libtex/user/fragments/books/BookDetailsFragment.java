package com.ad_victoriam.libtex.user.fragments.books;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.activities.ReservationDetailsActivity;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.BookFav;
import com.ad_victoriam.libtex.user.models.LibtexLibrary;
import com.ad_victoriam.libtex.user.models.Reservation;
import com.ad_victoriam.libtex.user.utils.ReservationStatus;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookDetailsFragment extends Fragment {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private Book book;

    private View mainView;

    private FragmentActivity activity;

    private TextView tTitle;
    private TextView tAuthor;
    private TextView tDescription;
    private TextView tPublisher;
    private TextView tNoOfPages;
    private TextView tCategory;
    private TextView tLocations;
    private TextView tLocationsOutOfStock;
    private TextView tStockStatus;
    private MaterialButton bReserve;

    private boolean isFav = false;
    private String favKey = null;

    // dialogs
    private boolean areTermsAccepted = false;
    private String location;

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable("book");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_book_details, container, false);
        activity = requireActivity();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        setTopAppBar();
        findViews();
        setFavouriteKeyValue();
        getLibrariesAndBooks();
        setDetails();

        return mainView;
    }

    private void findViews() {
        tTitle = mainView.findViewById(R.id.tTitle);
        tAuthor = mainView.findViewById(R.id.tAuthor);
        tDescription = mainView.findViewById(R.id.tDescription);
        tPublisher = mainView.findViewById(R.id.tPublisher);
        tNoOfPages = mainView.findViewById(R.id.tNoOfPages);
        tCategory = mainView.findViewById(R.id.tCategory);
        tLocations = mainView.findViewById(R.id.tLocations);
        tLocationsOutOfStock = mainView.findViewById(R.id.tLocationsOutOfStock);
        tStockStatus = mainView.findViewById(R.id.tStockStatus);
        bReserve = mainView.findViewById(R.id.bReserve);
        bReserve.setOnClickListener(this::reserveBookPickLibrary);
    }


    private void setTopAppBar() {
        if (favKey != null) {
            isFav = true;
        }
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setChildMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Book details");
        TopAppBar.get().setFavMode(activity, topAppBar, isFav);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity
                        .getSupportFragmentManager()
                        .popBackStack();
            }
        });

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == 1) {
                    if (isFav) {
                        if (favKey != null) {
                            databaseReference
                                    .child(activity.getString(R.string.n_users))
                                    .child(firebaseUser.getUid())
                                    .child(activity.getString(R.string.n_favourite_books))
                                    .child(favKey)
                                    .removeValue()
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()) {
                                            isFav = false;
                                            favKey = null;
                                            TopAppBar.get().setFavMode(activity, topAppBar, isFav);
                                            Snackbar.make(mainView, "Book removed from favourites", Snackbar.LENGTH_SHORT).show();

                                        } else {
                                            System.out.println(task.getResult());
                                        }
                                    });
                        }
                    } else {
                        BookFav bookFav = new BookFav(book.getTitle(), book.getAuthorName(), book.getPublisher());
                        favKey = databaseReference
                                .child(activity.getString(R.string.n_users))
                                .child(firebaseUser.getUid())
                                .child(activity.getString(R.string.n_favourite_books))
                                .push()
                                .getKey();
                        databaseReference
                                .child(activity.getString(R.string.n_users))
                                .child(firebaseUser.getUid())
                                .child(activity.getString(R.string.n_favourite_books))
                                .child(favKey)
                                .setValue(bookFav)
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {
                                        isFav = true;
                                        TopAppBar.get().setFavMode(activity, topAppBar, isFav);
                                        Snackbar.make(mainView, "Book added to favourites", Snackbar.LENGTH_SHORT).show();

                                    } else {
                                        System.out.println(task.getResult());
                                    }
                                });
                    }
                }
                return false;
            }
        });
    }

    private void setDetails() {
        if (book == null) {
            tTitle.setText(activity.getString(R.string.no_data));
        } else {
            tTitle.setText(book.getTitle());
            tAuthor.setText(book.getAuthorName());
            tDescription.setText(book.getDescription());
            tPublisher.setText(book.getPublisher());
            tNoOfPages.setText(book.getNoOfPages());
            tCategory.setText(beautifyList(book.getChosenCategories()));
            if (book.getLocations().isEmpty()) {
                tLocations.setText("-");
            } else {
                tLocations.setText(beautifyList(book.getLocationsNames()));
            }
            if (book.getLocationsOutOfStock().isEmpty()) {
                mainView.findViewById(R.id.div4).setVisibility(View.GONE);
                mainView.findViewById(R.id.outOfStock).setVisibility(View.GONE);
                tLocationsOutOfStock.setVisibility(View.GONE);
            } else {
                mainView.findViewById(R.id.div4).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.outOfStock).setVisibility(View.VISIBLE);
                tLocationsOutOfStock.setVisibility(View.VISIBLE);
                tLocationsOutOfStock.setText(beautifyList(book.getLocationsOutOfStockNames()));
            }
            if (book.getAvailableQuantity() > 0) {
                tStockStatus.setText(activity.getString(R.string.in_stock));
                int color = ContextCompat.getColor(activity, R.color.green);
                tStockStatus.setTextColor(color);
            } else {
                tStockStatus.setText(activity.getString(R.string.out_of_stock));
                int color = ContextCompat.getColor(activity, R.color.red);
                tStockStatus.setTextColor(color);
            }
        }
    }

    private void setReservationStatus() {
        databaseReference
                .child(activity.getString(R.string.n_users))
                .child(firebaseUser.getUid())
                .child(activity.getString(R.string.n_reservations))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {

                            boolean found = false;
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                if (found) break;
                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                    if (found) break;
                                    Reservation reservation = dataSnapshot1.getValue(Reservation.class);

                                    if (reservation != null &&
                                        isSameBook(reservation.getBookUid()) &&
                                        reservation.getStatus() != ReservationStatus.CANCELLED &&
                                        reservation.getStatus() != ReservationStatus.COMPLETED) {

                                        LocalDateTime reservationEndDate = LocalDateTime.parse(reservation.getEndDate());

                                        if (reservationEndDate.isAfter(LocalDateTime.now())) {
                                            // active reservation
                                            int color = ContextCompat.getColor(activity, R.color.red);
                                            bReserve.setText(activity.getString(R.string.reservation_duplicated));
                                            bReserve.setTextColor(color);
                                            bReserve.setStrokeColorResource(R.color.red);
                                            bReserve.setEnabled(false);
                                            found = true;
                                        }
                                    }
                                }
                            }
                        } else {
                            System.out.println(task.getResult());
                        }
                    }

                    private boolean isSameBook(String bookUid) {
                        for (Map.Entry<String, String> entry: book.getSameBookUids().entrySet()) {
                            if (entry.getValue().equals(bookUid)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    private void reserveBookPickLibrary(View secondaryView) {

        if (book.getAvailableQuantity() == 0) {
            notifyBookUnavailable();
            return;
        }

        Dialog reservationDialog1 = new Dialog(activity);
        reservationDialog1.setContentView(R.layout.dialog_reservation_1);
        reservationDialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reservationDialog1.setCancelable(false);

        Spinner spinner = reservationDialog1.findViewById(R.id.spinner);
        List<String> locations = new ArrayList<>(book.getLocationsNames());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                activity,
                R.layout.support_simple_spinner_dropdown_item,
                locations);
        spinner.setAdapter(arrayAdapter);

        MaterialButton bCancel = reservationDialog1.findViewById(R.id.bCancel);
        MaterialButton bProceed = reservationDialog1.findViewById(R.id.bProceed);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                location = adapterView.getItemAtPosition(i).toString();
                bProceed.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reservationDialog1.dismiss();
            }
        });
        bProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) {
                    reserveBookConfirm(secondaryView, location);
                    reservationDialog1.dismiss();
                }
            }
        });
        reservationDialog1.show();
    }

    private void reserveBookConfirm(View view, String location) {
        Dialog reservationDialog2 = new Dialog(activity);
        reservationDialog2.setContentView(R.layout.dialog_reservation_2);
        reservationDialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        reservationDialog2.setCancelable(false);

        TextView tReservationBook = reservationDialog2.findViewById(R.id.tReservationBook);
        TextView tReservationLocation = reservationDialog2.findViewById(R.id.tReservationLocation);
        TextView tReservationDescription = reservationDialog2.findViewById(R.id.tReservationDescription);

        String text = activity.getString(R.string.reserve_2_alert_part_1) + " " + book.getTitle() + ".";
        tReservationBook.setText(text);
        text = activity.getString(R.string.reserve_2_alert_part_2) + " " + location;
        tReservationLocation.setText(text);
        tReservationDescription.setText(activity.getString(R.string.d_reserve_2_alert_part_3));

        MaterialButton bReadMore = reservationDialog2.findViewById(R.id.bReadMore);
        MaterialButton bAcceptTerms = reservationDialog2.findViewById(R.id.bAcceptTerms);
        TextView tAcceptTermsHelper = reservationDialog2.findViewById(R.id.tAcceptTermsHelper);
        MaterialButton bCancel = reservationDialog2.findViewById(R.id.bCancel);
        MaterialButton bConfirm = reservationDialog2.findViewById(R.id.bConfirm);

        bReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, ReservationDetailsActivity.class));
            }
        });
        bAcceptTerms.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!areTermsAccepted) {
                    int color = ContextCompat.getColor(activity, R.color.light_green);
                    bAcceptTerms.setBackgroundColor(color);
                    tAcceptTermsHelper.setText(activity.getString(R.string.reservation_terms_accepted));
                    color = ContextCompat.getColor(activity, R.color.dark_green);
                    tAcceptTermsHelper.setTextColor(color);
                    bConfirm.setEnabled(true);
                    areTermsAccepted = true;
                    ObjectAnimator.ofFloat(view, "rotation", 0, 360).start();
                }
                return true;
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areTermsAccepted = false;
                reservationDialog2.dismiss();
            }
        });
        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                areTermsAccepted = false;
                reservationDialog2.dismiss();

                String libraryUid = getLibraryUid(location);
                Reservation reservation = createNewReservation(libraryUid);

                databaseReference
                        .child(activity.getString(R.string.n_books))
                        .child(libraryUid)
                        .child(reservation.getBookUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                DataSnapshot dataSnapshot = task.getResult();
                                Book book = dataSnapshot.getValue(Book.class);

                                if (book != null) {
                                    if (book.getAvailableQuantity() == 0) {
                                        getLibrariesAndBooks();
                                        setDetails();
                                        Snackbar.make(activity, mainView, activity.getString(R.string.out_of_stock), Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Map<String, Object> availableQuantityUpdate =  new HashMap<>();
                                        availableQuantityUpdate.put(
                                                activity.getString(R.string.n_books) + "/" +
                                                libraryUid + "/" +
                                                reservation.getBookUid() + "/" +
                                                activity.getString(R.string.p_book_available_quantity),

                                                book.getAvailableQuantity() - 1
                                        );
                                        setUserReservation(libraryUid, reservation, availableQuantityUpdate);
                                    }
                                }

                            } else {
                                System.out.println(task.getResult());
                                Snackbar.make(activity, mainView, activity.getString(R.string.reservation_unsuccessful), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }

            private void setUserReservation(String libraryUid, Reservation reservation, Map<String, Object> availableQuantityUpdate) {
                databaseReference
                        .updateChildren(availableQuantityUpdate)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {

                                databaseReference
                                        .child(activity.getString(R.string.n_users))
                                        .child(firebaseUser.getUid())
                                        .child(activity.getString(R.string.n_reservations))
                                        .child(libraryUid)
                                        .push()
                                        .setValue(reservation)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                setReservationStatus();
                                                getLibrariesAndBooks();
                                                setDetails();
                                                Snackbar.make(activity, mainView, activity.getString(R.string.reservation_successful), Snackbar.LENGTH_SHORT).show();
                                            } else {
                                                System.out.println(task2.getResult());
                                                Snackbar.make(activity, mainView, activity.getString(R.string.reservation_unsuccessful), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                System.out.println(task1.getResult());
                                Snackbar.make(activity, mainView, activity.getString(R.string.reservation_unsuccessful), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }

            private String getLibraryUid(String location) {
                for (Map.Entry<String, String> entry: book.getLocations().entrySet()) {
                    if (location.equals(entry.getValue())) {
                        return entry.getKey();
                    }
                }
                return null;
            }

            private Reservation createNewReservation(String libraryUid) {

                String bookUid = getActualBookUid(libraryUid);
                LocalDateTime date = LocalDateTime.now();
                String startDate = date.toString();
                String endDate = date.plusHours(24).toString();
                ReservationStatus status = ReservationStatus.APPROVED;

                return new Reservation(bookUid, startDate, endDate, status);
            }

            private String getActualBookUid(String libraryUid) {
                return book.getSameBookUids().get(libraryUid);
            }
        });
        reservationDialog2.show();
    }

    private void notifyBookUnavailable() {
        Snackbar.make(mainView, "ce livre n'est pas disponible", Snackbar.LENGTH_SHORT).show();
    }

    public String beautifyList(List<String> list) {

        String listToString = "";

        boolean first = true;
        for (String item: list) {
            if (first) {
                first = false;
            } else {
                item = "; " + item;
            }
            listToString = listToString.concat(item);
        }
        return listToString;
    }

    private void setFavouriteKeyValue() {
        databaseReference
                .child(activity.getString(R.string.n_users))
                .child(firebaseUser.getUid())
                .child(activity.getString(R.string.n_favourite_books))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                            BookFav bookFav = dataSnapshot.getValue(BookFav.class);

                            if (bookFav != null && bookFav.isSame(book)) {
                                favKey = dataSnapshot.getKey();
                                setTopAppBar();
                                break;
                            }
                        }
                    } else {
                        System.out.println(task.getResult());
                    }
                });
    }

    private void getLibrariesAndBooks() {
        if (book.getLocations().isEmpty()) {

            List<LibtexLibrary> libraries = new ArrayList<>();
            Map<String, String> libraryNames = new HashMap<>();

            // get libraries details
            databaseReference
                    .child(activity.getString(R.string.n_admins))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                    String libraryName = dataSnapshot
                                            .child(activity.getString(R.string.n_location))
                                            .child(activity.getString(R.string.p_location_name))
                                            .getValue(String.class);

                                    libraryNames.put(dataSnapshot.getKey(), libraryName);
                                }
                            } else {
                                System.out.println(task.getResult());
                            }
                        }
                    });

            // get all books
            databaseReference
                    .child(activity.getString(R.string.n_books))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {

                                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {

                                    LibtexLibrary libtexLibrary = new LibtexLibrary();
                                    libtexLibrary.setUid(dataSnapshot.getKey());
                                    libtexLibrary.setName(libraryNames.get(dataSnapshot.getKey()));

                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                        Book book = dataSnapshot1.getValue(Book.class);

                                        if (book != null) {
                                            book.setUid(dataSnapshot1.getKey());
                                            libtexLibrary.addBook(book);
                                        }
                                    }
                                    libraries.add(libtexLibrary);
                                }
                                setLocations(libraries);
                                setDetails();
                            } else {
                                System.out.println(task.getResult());
                            }
                        }
                    });
        }
    }

    private void setLocations(List<LibtexLibrary> libraries) {
        for (LibtexLibrary library: libraries) {
            for (Book b: library.getBooks()) {
                if (b.isSame(book)) {
                    if (b.getAvailableQuantity() > 0) {
                        book.addLocation(library);
                    } else {
                        book.addLocationOutOfStock(library);
                    }
                    book.addSameBookUid(library.getUid(), b.getUid());
                }
            }
        }
        setReservationStatus();
    }
}