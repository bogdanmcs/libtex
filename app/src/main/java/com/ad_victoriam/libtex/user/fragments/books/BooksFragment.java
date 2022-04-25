package com.ad_victoriam.libtex.user.fragments.books;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.adapters.BooksAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.LibtexLibrary;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BooksFragment extends Fragment {

    private DatabaseReference databaseReference;

    private View mainView;

    private FragmentActivity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SearchView searchView;

    private final List<LibtexLibrary> libraries = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private BooksAdapter booksAdapter;

    private boolean initBooks;
    private String searchQueryText = "";

    public BooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_books, container, false);
        activity = requireActivity();

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        initBooks = false;
        setTopAppBar();
        findViews();
        setSearchView();
        setRecyclerView();
        setSwipeRefreshLayout();
        getLibrariesAndBooks();

        return mainView;
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Books");
    }

    private void findViews() {
        recyclerView = mainView.findViewById(R.id.recyclerView);
        swipeRefreshLayout = mainView.findViewById(R.id.swipeRefreshLayout);
        searchView = mainView.findViewById(R.id.searchView);
    }

    private void setRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        booksAdapter = new BooksAdapter(activity, books);
        recyclerView.setAdapter(booksAdapter);
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLibrariesAndBooks();

            }
        });
    }

    private void setSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                executeSearchQueryFilter(newText);
                booksAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void executeSearchQueryFilter(String newText) {
        searchQueryText = newText;
        if (initBooks) {
            List<Book> filteredBooks = new ArrayList<>();

            for (Book book: books) {
                if (isNewTextSubstringOfBookDetails(book, newText)) {
                    filteredBooks.add(book);
                }
            }
            booksAdapter = new BooksAdapter(activity, filteredBooks);
            recyclerView.setAdapter(booksAdapter);
        }
    }

    private boolean isNewTextSubstringOfBookDetails(Book book, String newText) {
        return book.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                book.getAuthorName().toLowerCase().contains(newText.toLowerCase());
    }

    private void getLibrariesAndBooks() {
        libraries.clear();
        books.clear();

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

                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                    Book book = dataSnapshot1.getValue(Book.class);

                                    if (book != null) {
                                        book.setUid(dataSnapshot1.getKey());
                                        libtexLibrary.addBook(book);
                                    }
                                }

                                libraries.add(libtexLibrary);
                            }
                            setBooks();

                        } else {
                            System.out.println(task.getResult());
                            Log.e("GET_BOOKS_DB", String.valueOf(task.getException()));
                        }
                    }
                });
    }

    private void setBooks() {
        for (LibtexLibrary library: libraries) {
            for (Book book: library.getBooks()) {
                if (!isDuplicate(book)) {
                    books.add(book);
                }
            }
        }
        doPostDataOperations();
    }

    private void doPostDataOperations() {
        initBooks = true;
        if (!searchQueryText.isEmpty()) {
            executeSearchQueryFilter(searchQueryText);
        }
        booksAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private boolean isDuplicate(Book book) {
        for (Book b: books) {
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
}