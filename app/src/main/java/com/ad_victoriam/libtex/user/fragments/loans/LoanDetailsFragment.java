package com.ad_victoriam.libtex.user.fragments.loans;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.BookFav;
import com.ad_victoriam.libtex.user.models.LibtexLibrary;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoanDetailsFragment extends Fragment {

    private static final String LOAN_TYPE_ACTIVE = "active";
    private static final String LOAN_TYPE_ALL = "all";

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private Book book;
    private String loanType;

    private View mainView;

    private FragmentActivity activity;

    private TextView tTitle;
    private TextView tAuthor;
    private TextView tDescription;
    private TextView tPublisher;
    private TextView tNoOfPages;
    private TextView tCategory;
    private TextView tLocations;

    private boolean isFav = false;
    private String favKey = null;

    public LoanDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            book = getArguments().getParcelable("book");
            loanType = getArguments().getString("loanType");
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
        setFavouriteKeyValue();
        getBooks();

        return mainView;
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
                if (loanType.equals(LOAN_TYPE_ACTIVE)) {
                    Navigation.findNavController(mainView).navigate(R.id.action_loanDetailsFragment_to_activeLoansFragment);
                } else if (loanType.equals(LOAN_TYPE_ALL)) {
                    Navigation.findNavController(mainView).navigate(R.id.action_loanDetailsFragment_to_allLoansFragment);
                }
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.addToFav) {
                    if (isFav) {
                        if (favKey != null) {
                            databaseReference
                                    .child(getString(R.string.n_users))
                                    .child(firebaseUser.getUid())
                                    .child(getString(R.string.n_favourite_books))
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
                                .child(getString(R.string.n_users))
                                .child(firebaseUser.getUid())
                                .child(getString(R.string.n_favourite_books))
                                .push()
                                .getKey();
                        databaseReference
                                .child(getString(R.string.n_users))
                                .child(firebaseUser.getUid())
                                .child(getString(R.string.n_favourite_books))
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

    private void reserveBook(View view) {
        Snackbar.make(mainView, "reserve this book", Snackbar.LENGTH_SHORT).show();
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

    public void getBooks () {

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        List<LibtexLibrary> libraries = new ArrayList<>();
        Map<String, String> libraryNames = new HashMap<>();

        // get libraries details
        databaseReference
                .child(getString(R.string.n_admins))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                String libraryName = dataSnapshot
                                        .child(getString(R.string.n_location))
                                        .child(getString(R.string.p_location_name))
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
                .child(getString(R.string.n_books))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {

                                LibtexLibrary libtexLibrary = new LibtexLibrary();
                                libtexLibrary.setUid(dataSnapshot.getKey());
                                libtexLibrary.setName(libraryNames.get(dataSnapshot.getKey()));

                                for (DataSnapshot b: dataSnapshot.getChildren()) {

                                    Book book = b.getValue(Book.class);

                                    if (book != null) {
                                        book.setUid(b.getKey());
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

    private void setDetails() {
        tTitle = mainView.findViewById(R.id.tTitle);
        tAuthor = mainView.findViewById(R.id.tAuthor);
        tDescription = mainView.findViewById(R.id.tDescription);
        tPublisher = mainView.findViewById(R.id.tPublisher);
        tNoOfPages = mainView.findViewById(R.id.tNoOfPages);
        tCategory = mainView.findViewById(R.id.tCategory);
        tLocations = mainView.findViewById(R.id.tLocations);
        final MaterialButton bReserve = mainView.findViewById(R.id.bReserve);
        bReserve.setOnClickListener(this::reserveBook);

        if (book == null) {
            tTitle.setText(getString(R.string.no_data));
        } else {
            tTitle.setText(book.getTitle());
            tAuthor.setText(book.getAuthorName());
            tDescription.setText(book.getDescription());
            tPublisher.setText(book.getPublisher());
            tNoOfPages.setText(book.getNoOfPages());
            tCategory.setText(beautifyList(book.getChosenCategories()));
            tLocations.setText(beautifyList(book.getLocations()));
        }
    }

    private void setLocations(List<LibtexLibrary> libraries) {
        for (LibtexLibrary library: libraries) {
            for (Book b: library.getBooks()) {
                if (b.isSame(book)) {
                    book.addLocation(library.getName());
                }
            }
        }
    }

    private void setFavouriteKeyValue() {
        databaseReference
                .child(getString(R.string.n_users))
                .child(firebaseUser.getUid())
                .child(getString(R.string.n_favourite_books))
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
}