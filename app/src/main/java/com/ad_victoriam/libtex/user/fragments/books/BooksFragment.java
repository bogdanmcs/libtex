package com.ad_victoriam.libtex.user.fragments.books;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import java.util.Map;

public class BooksFragment extends Fragment {

    private DatabaseReference databaseReference;

    private View mainView;

    private FragmentActivity activity;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private final List<LibtexLibrary> libraries = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();
    private final Map<String, String> libraryNames = new HashMap<>();
    private BooksAdapter booksAdapter;

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

        setTopAppBar();
        setRecyclerView();

        swipeRefreshLayout = mainView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLibrariesAndBooks();
                booksAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getLibrariesAndBooks();

        return mainView;
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Books");
    }

    private void setRecyclerView() {
        recyclerView = mainView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        booksAdapter = new BooksAdapter(activity, books);
        recyclerView.setAdapter(booksAdapter);
    }

    private void getLibrariesAndBooks() {
        libraries.clear();
        books.clear();

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

                                for (DataSnapshot b: dataSnapshot.getChildren()) {

                                    Book book = b.getValue(Book.class);

                                    if (book != null) {
                                        book.setUid(b.getKey());
                                        libtexLibrary.addBook(book);
                                    }
                                }

                                libraries.add(libtexLibrary);
                            }

                            setBooks();
                            booksAdapter.notifyItemInserted(books.size() - 1);

                        } else {
                            System.out.println(task.getResult());
                        }
                    }
                });
    }

    private void setBooks() {
        for (LibtexLibrary library: libraries) {
            for (Book book: library.getBooks()) {

                String libraryName = libraryNames.get(library.getUid());
                if (!isDuplicate(book)) {
                    book.addLocation(libraryName);
                    books.add(book);
                } else {
                    for (Book b: books) {
                        if (b.isSame(book)) {
                            b.addLocation(libraryName);
                        }
                    }
                }
            }
        }
    }

    private boolean isDuplicate(Book book) {
        for (Book b: books) {
            if (b.isSame(book)) {
                return true;
            }
        }
        return false;
    }
}