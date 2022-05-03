package com.ad_victoriam.libtex.user.fragments.books;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.adapters.UserAdapter;
import com.ad_victoriam.libtex.common.models.Reservation;
import com.ad_victoriam.libtex.common.utils.ReservationStatus;
import com.ad_victoriam.libtex.user.activities.ReservationDetailsActivity;
import com.ad_victoriam.libtex.user.adapters.ReviewsAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.BookFav;
import com.ad_victoriam.libtex.user.models.LibtexLibrary;
import com.ad_victoriam.libtex.user.models.Loan;
import com.ad_victoriam.libtex.user.models.Review;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class BookDetailsFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private View mainView;
    private FragmentActivity activity;

    private Book book;

    private MaterialRatingBar ratingBar;
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
    private MaterialRatingBar ratingBarEditable;
    private TextInputLayout layoutYourReviewDescription;
    private MaterialButton bSaveReview;
    private MaterialCardView cardViewNoReviews;
    private RecyclerView recyclerView;

    private boolean isFav = false;
    private String favKey = null;

    // dialogs
    private boolean areTermsAccepted = false;
    private String location;

    // transaction
    private boolean isAvailable;

    // reviews
    private Review yourReview = new Review();
    private Double rating;
    private List<Review> reviews = new ArrayList<>();
    private ReviewsAdapter reviewsAdapter;

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
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        setTopAppBar();
        findViews();
        setFavouriteKeyValue();
        getLibrariesAndBooks();
        setDetails();
        getReviews();

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
        ratingBar = mainView.findViewById(R.id.ratingBar);
        bReserve = mainView.findViewById(R.id.bReserve);
        bReserve.setOnClickListener(this::reserveBookPickLibrary);
        ratingBarEditable = mainView.findViewById(R.id.ratingBarEditable);
        ratingBarEditable.setOnRatingChangeListener(this::changeYourRating);
        layoutYourReviewDescription = mainView.findViewById(R.id.layoutYourReviewDescription);
        bSaveReview = mainView.findViewById(R.id.bSaveReview);
        bSaveReview.setOnClickListener(this::saveReview);
        bSaveReview.setEnabled(false);
        recyclerView = mainView.findViewById(R.id.recyclerView);
        cardViewNoReviews = mainView.findViewById(R.id.cardViewNoReviews);
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
                                    .child(currentUser.getUid())
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
                                            Log.e("REMOVE_USER_FAV_BOOK_BY_UID_DB", String.valueOf(task.getException()));
                                        }
                                    });
                        }
                    } else {
                        BookFav bookFav = new BookFav(book.getTitle(), book.getAuthorName(), book.getPublisher());
                        favKey = databaseReference
                                .child(activity.getString(R.string.n_users))
                                .child(currentUser.getUid())
                                .child(activity.getString(R.string.n_favourite_books))
                                .push()
                                .getKey();
                        databaseReference
                                .child(activity.getString(R.string.n_users))
                                .child(currentUser.getUid())
                                .child(activity.getString(R.string.n_favourite_books))
                                .child(favKey)
                                .setValue(bookFav)
                                .addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {
                                        isFav = true;
                                        TopAppBar.get().setFavMode(activity, topAppBar, isFav);
                                        Snackbar.make(mainView, "Book added to favourites", Snackbar.LENGTH_SHORT).show();

                                    } else {
                                        Log.e("SET_USER_FAV_BOOK_BY_UID_DB", String.valueOf(task.getException()));
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
                .child(activity.getString(R.string.n_reservations_2))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                Reservation reservation = dataSnapshot.getValue(Reservation.class);

                                if (reservation != null &&
                                    reservation.getUserUid().equals(currentUser.getUid()) &&
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
                                        break;
                                    }

                                }
                            }
                        } else {
                            Log.e("GET_RESERVATIONS_DB", String.valueOf(task.getException()));
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

                isAvailable = false;

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

                                if (bookAvailableQuantity == 0) {
                                    getLibrariesAndBooks();
                                    setDetails();
                                    Snackbar.make(activity, mainView, activity.getString(R.string.out_of_stock), Snackbar.LENGTH_SHORT).show();
                                } else {
                                    bookAvailableQuantity -= 1;
                                    isAvailable = true;
                                }

                                // Set value and report transaction success
                                mutableData.setValue(bookAvailableQuantity);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean committed,
                                                   DataSnapshot currentData) {
                                // Transaction completed
                                if (committed) {
                                    if (isAvailable) {
                                        setUserReservation(reservation);
                                    } else {
                                        Snackbar.make(activity, mainView, activity.getString(R.string.out_of_stock), Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(activity, mainView, activity.getString(R.string.reservation_unsuccessful), Snackbar.LENGTH_SHORT).show();
                                    Log.e("BOOK_AVAILABILITY_UPDATE_DB", "postTransaction:onComplete:" + databaseError);
                                }
                            }
                        });
            }

            private void setUserReservation(Reservation reservation) {
                databaseReference
                        .child(activity.getString(R.string.n_reservations_2))
                        .push()
                        .setValue(reservation)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                setReservationStatus();
                                getLibrariesAndBooks();
                                setDetails();
                                Snackbar.make(activity, mainView, activity.getString(R.string.reservation_successful), Snackbar.LENGTH_SHORT).show();
                            } else {
                                Log.e("RESERVATION_CREATE_DB", String.valueOf(task.getResult()));
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

                return new Reservation(currentUser.getUid(), libraryUid, bookUid, startDate, endDate, status);
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
                .child(currentUser.getUid())
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
                        Log.e("GET_USERS_FAV_BOOK_BY_UID_DB", String.valueOf(task.getException()));
                    }
                });
    }

    private void getLibrariesAndBooks() {
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
                                                Log.e("GET_BOOKS_DB", String.valueOf(task.getException()));
                                            }
                                        }
                                    });

                        } else {
                            Log.e("GET_ADMINS_DB", String.valueOf(task.getException()));
                        }
                    }
                });
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
        setYourRating();
        getReviews();
        setReservationStatus();
    }

    private void setYourRating() {
        databaseReference
                .child(activity.getString(R.string.n_users))
                .child(currentUser.getUid())
                .child(activity.getString(R.string.n_book_loans))
                .child(activity.getString(R.string.n_loans_history))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        boolean found = false;
                        for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {
                            if (found) break;
                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                Loan loan = dataSnapshot1.getValue(Loan.class);

                                if (loan != null &&
                                    book.getSameBookUids().containsValue(loan.getBookUid())) {

                                    found = true;
                                    break;
                                }
                            }
                        }
                        TextView tInfoText = mainView.findViewById(R.id.tInfoText);
                        if (found) {
                            tInfoText.setVisibility(View.GONE);
                            // check if not already reviewed
                            Query query = databaseReference
                                    .child(activity.getString(R.string.n_reviews))
                                    .orderByChild(activity.getString(R.string.p_review_user_uid))
                                    .equalTo(currentUser.getUid());
                            query
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            boolean found1 = false;
                                            for (DataSnapshot dataSnapshot: task1.getResult().getChildren()) {

                                                Review review = dataSnapshot.getValue(Review.class);

                                                if (review != null &&
                                                    review.isBook(book)) {

                                                    found1 = true;
                                                    yourReview.setUid(dataSnapshot.getKey());
                                                    yourReview.setRating(review.getRating());
                                                    yourReview.setDescription(review.getDescription());
                                                    break;
                                                }
                                            }
                                            if (found1) {
                                                updateSavedReview(yourReview.getRating(), yourReview.getDescription());
                                            }
                                        } else {
                                            Log.e("GET_REVIEW_BY_USER_UID_DB", String.valueOf(task1.getException()));
                                        }
                                    });
                        } else {
                            tInfoText.setVisibility(View.VISIBLE);
                            tInfoText.setText(activity.getString(R.string.loan_before_review));
                            mainView.findViewById(R.id.yourRating).setVisibility(View.GONE);
                            mainView.findViewById(R.id.yourDescription).setVisibility(View.GONE);
                            ratingBarEditable.setVisibility(View.GONE);
                            layoutYourReviewDescription.setVisibility(View.GONE);
                            bSaveReview.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("GET_USER_LOANS_HISTORY_BY_UID_DB", String.valueOf(task.getException()));
                    }
                });
    }

    private void changeYourRating(MaterialRatingBar materialRatingBar, float v) {
        if (v == 0.0) {
            bSaveReview.setEnabled(false);
        } else {
            bSaveReview.setEnabled(true);
        }
    }

    private void updateSavedReview(double yourRating, String yourReviewDescription) {
        ratingBarEditable.setProgress((int) (yourRating * 2));
        layoutYourReviewDescription.getEditText().setText(yourReviewDescription);
        getReviews();
    }

    private void saveReview(View view) {
        if (ratingBarEditable.getRating() > 0.0) {
            String yourReviewDescription = layoutYourReviewDescription.getEditText().getText().toString();
            double yourRating = ratingBarEditable.getRating();

            Review yourReviewUpdated = new Review(
                    book.getTitle(),
                    book.getAuthorName(),
                    book.getPublisher(),
                    currentUser.getUid(),
                    yourRating,
                    yourReviewDescription
            );
            if (isReviewValid(yourReviewUpdated)) {
                if (yourReview.getUid() == null || yourReview.getUid().isEmpty()) {
                    String reviewUid = databaseReference
                            .child(activity.getString(R.string.n_reviews))
                            .push()
                            .getKey();
                    yourReview.setUid(reviewUid);
                    databaseReference
                            .child(activity.getString(R.string.n_reviews))
                            .child(yourReview.getUid())
                            .setValue(yourReviewUpdated)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar.make(view, "Review saved", Snackbar.LENGTH_SHORT).show();
                                    updateSavedReview(yourReviewUpdated.getRating(), yourReviewUpdated.getDescription());
                                } else {
                                    Log.e("POST_REVIEW_DB", String.valueOf(task.getException()));
                                }
                            });
                } else {
                    Map<String, Object> yourReviewUpdate =  new HashMap<>();
                    String path = activity.getString(R.string.n_reviews) + "/" + yourReview.getUid() + "/";
                    yourReviewUpdate.put(path + activity.getString(R.string.p_review_rating), yourReviewUpdated.getRating());
                    yourReviewUpdate.put(path + activity.getString(R.string.p_review_description), yourReviewUpdated.getDescription());
                    databaseReference
                            .updateChildren(yourReviewUpdate)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Snackbar.make(view, "Review saved", Snackbar.LENGTH_SHORT).show();
                                    updateSavedReview(yourReviewUpdated.getRating(), yourReviewUpdated.getDescription());
                                } else {
                                    Log.e("UPDATE_REVIEW_DB", String.valueOf(task.getException()));
                                }
                            });
                }
            }
        }
    }

    private boolean isReviewValid(Review yourReview) {
        if (yourReview.getDescription().length() > 250) {
            layoutYourReviewDescription.setError("Max length of characters is 250");
            layoutYourReviewDescription.requestFocus();
            return false;
        }
        return true;
    }

    private void getReviews() {
        reviews.clear();
        Query query = databaseReference
                .child(activity.getString(R.string.n_reviews))
                .orderByChild("bookTitle")
                .equalTo(book.getTitle());
        query
                .get()
                .addOnSuccessListener(task -> {
                    for (DataSnapshot dataSnapshot: task.getChildren()) {

                        Review review = dataSnapshot.getValue(Review.class);

                        if (review != null &&
                            review.getBookAuthor().equals(book.getAuthorName()) &&
                            review.getBookPublisher().equals(book.getPublisher())) {

                            reviews.add(review);
                        }
                    }
                    updateUi();
                    calculateRating();
                    setReviews();
                })
                .addOnFailureListener(e -> {
                   Log.e("GET_REVIEWS_FOR_BOOK", String.valueOf(e));
                });
    }

    private void updateUi() {
        if (reviews.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            cardViewNoReviews.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            cardViewNoReviews.setVisibility(View.INVISIBLE);
        }
    }

    private void calculateRating() {
        rating = 0.0;
        for (Review review: reviews) {
            rating += review.getRating();
        }
        rating /= reviews.size();
        setRatingBarProgress();
    }

    private void setRatingBarProgress() {
        ratingBar.setProgress((int) (rating * 2));
    }

    private void setReviews() {
        reviewsAdapter = new ReviewsAdapter(activity, reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(reviewsAdapter);
    }
}