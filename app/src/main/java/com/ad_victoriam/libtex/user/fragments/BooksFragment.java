package com.ad_victoriam.libtex.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.ad_victoriam.libtex.user.adapters.BooksAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.LibtexLibrary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BooksFragment extends Fragment {

    private DatabaseReference databaseReference;

    private FragmentActivity activity;

    private RecyclerView recyclerView;
//    private

    //    private Map<String, List<Book>> libraries;
    private List<LibtexLibrary> libraries = new ArrayList<>();
    private List<Book> books = new ArrayList<>();
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

        View mainView = inflater.inflate(R.layout.fragment_books, container, false);

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        activity = requireActivity();
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBarState.get().setNormalMode(activity, topAppBar);
        TopAppBarState.get().setTitleMode(activity, topAppBar, "Books");

        recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        booksAdapter = new BooksAdapter(activity, books);
        recyclerView.setAdapter(booksAdapter);

        getLibrariesAndBooks();

        return mainView;
    }

    private void getLibrariesAndBooks() {
        libraries = new ArrayList<>();


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
                            booksAdapter.notifyDataSetChanged();

                        } else {
                            System.out.println(task.getResult());
                        }
                    }
                });
    }

    private void setBooks() {
        for (LibtexLibrary library: libraries) {
            for (Book book: library.getBooks()) {

                // TODO: library uid will be replaced with name /& location

                if (!isDuplicate(book)) {
                    book.addAvailableShowroom(library.getUid());
                    books.add(book);
                } else {
                    for (Book b: books) {
                        if (b.isSame(book)) {
                            b.addAvailableShowroom(library.getUid());
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