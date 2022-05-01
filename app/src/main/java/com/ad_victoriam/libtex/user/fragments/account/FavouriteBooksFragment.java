package com.ad_victoriam.libtex.user.fragments.account;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.adapters.FavouriteBooksAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.BookFav;
import com.ad_victoriam.libtex.user.models.LibtexLibrary;
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

public class FavouriteBooksFragment extends Fragment {

    private DatabaseReference databaseReference;

    private View mainView;
    private FragmentActivity activity;

    private RecyclerView recyclerView;
    private TextView tNoFavBooks;
    private SearchView searchView;
    private FavouriteBooksAdapter favouriteBooksAdapter;

    private List<Book> favouriteBooks;

    private String searchQueryText = "";
    private boolean searchFilter;

    public FavouriteBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        mainView = inflater.inflate(R.layout.fragment_favourite_books, container, false);
        activity = requireActivity();

        searchFilter = false;
        findViews();
        setTopAppBar();
        setSearchView();
        setFavouriteBooks();

        return mainView;
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setChildMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Favourite Books");
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity
                        .getSupportFragmentManager()
                        .popBackStack("accountFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    private void findViews() {
        tNoFavBooks = mainView.findViewById(R.id.tNoFavBooks);
        searchView = mainView.findViewById(R.id.searchView);
    }

    private void setFavouriteBooks() {
        recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        favouriteBooks = new ArrayList<>();
        favouriteBooksAdapter = new FavouriteBooksAdapter(activity, favouriteBooks);
        recyclerView.setAdapter(favouriteBooksAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference
                .child(getString(R.string.n_users))
                .child(currentUser.getUid())
                .child(getString(R.string.n_favourite_books))
                .get()
                .addOnSuccessListener(task -> {
                    updateUi(task.hasChildren());
                    List<BookFav> favouriteBooks = new ArrayList<>();
                    for (DataSnapshot dataSnapshot: task.getChildren()) {

                        BookFav bookFav = dataSnapshot.getValue(BookFav.class);

                        if (bookFav != null) {
                            favouriteBooks.add(bookFav);
                        }
                    }
                    getBooks(favouriteBooks);
                })
                .addOnFailureListener(e -> {
                    Log.e("GET_USER_FAV_BOOKS_DB", e.toString());
                });
    }

    private void updateUi(boolean hasChildren) {
        if (hasChildren) {
            tNoFavBooks.setVisibility(View.INVISIBLE);
            searchView.setVisibility(View.VISIBLE);
        } else {
            tNoFavBooks.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.INVISIBLE);
        }
    }

    private void getBooks(List<BookFav> favouriteBooks) {
        databaseReference
                .child(activity.getString(R.string.n_books))
                .get()
                .addOnSuccessListener(task -> {
                    List<LibtexLibrary> libraries = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : task.getChildren()) {

                        LibtexLibrary libtexLibrary = new LibtexLibrary();
                        libtexLibrary.setUid(dataSnapshot.getKey());

                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                            Book book = dataSnapshot1.getValue(Book.class);

                            if (book != null &&
                                isBookInFavourites(favouriteBooks, book)) {

                                book.setUid(dataSnapshot1.getKey());
                                libtexLibrary.addBook(book);
                            }
                        }
                        libraries.add(libtexLibrary);
                    }
                    setBooks(libraries);
                })
                .addOnFailureListener(e -> {
                    Log.e("GET_BOOKS_DB", e.toString());
                });
    }

    private boolean isBookInFavourites(List<BookFav> favouriteBooks, Book book) {
        for (BookFav bookFav: favouriteBooks) {
            if (bookFav.isSame(book)) {
                return true;
            }
        }
        return false;
    }

    private void setBooks(List<LibtexLibrary> libraries) {
        for (LibtexLibrary library: libraries) {
            for (Book book: library.getBooks()) {
                if (!isDuplicate(book)) {
                    favouriteBooks.add(book);
                    if (!searchFilter) {
                        favouriteBooksAdapter.notifyItemInserted(favouriteBooks.size() - 1);
                    } else {
                        executeSearchQueryFilter(searchQueryText);
                    }
                }
            }
        }
    }

    private boolean isDuplicate(Book book) {
        for (Book b: favouriteBooks) {
            if (b.isSame(book)) {
                // if the new duplicated book is in stock (and the original one isn't),
                // then replace it - stock status should always try to be positive
                if (b.getAvailableQuantity() == 0 && book.getAvailableQuantity() > 0) {
                    b.setUniqueDetails(book);
                }
                return true;
            }
        }
        return false;
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
        List<Book> filteredFavouriteBooks = new ArrayList<>();

        if (newText.isEmpty()) {
            searchFilter = false;
            filteredFavouriteBooks = favouriteBooks;
        } else {
            searchFilter = true;
            for (Book book: favouriteBooks) {
                if (isNewTextSubstringOfBookDetails(book, newText)) {
                    filteredFavouriteBooks.add(book);
                }
            }
        }
        favouriteBooksAdapter = new FavouriteBooksAdapter(activity, filteredFavouriteBooks);
        recyclerView.setAdapter(favouriteBooksAdapter);
    }

    private boolean isNewTextSubstringOfBookDetails(Book book, String newText) {
        return book.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                book.getAuthorName().toLowerCase().contains(newText.toLowerCase());
    }
}