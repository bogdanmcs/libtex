package com.ad_victoriam.libtex.user.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WhatToReadFragment extends Fragment {

    private DatabaseReference databaseReference;
    private View mainView;
    private FragmentActivity activity;

    private TextInputLayout layoutCategory;

    private final List<Book> books = new ArrayList<>();
    private boolean areBooksDone = false;
    private List<String> chosenCategories = new ArrayList<>();

    public WhatToReadFragment() {
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
        mainView = inflater.inflate(R.layout.fragment_what_to_read, container, false);
        activity = requireActivity();

        getBooks();
        findViews();
        setTopAppBar();
        setCategoryDialogFragmentListener();

        return mainView;
    }

    private void findViews() {
        layoutCategory = mainView.findViewById(R.id.layoutCategory);
        TextInputEditText eCategory = mainView.findViewById(R.id.eCategory);
        eCategory.setFocusable(false);
        eCategory.setOnClickListener(this::chooseCategory);

        MaterialButton bSurpriseMe = mainView.findViewById(R.id.bSurpriseMe);
        bSurpriseMe.setOnClickListener(this::showResultedBook);
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setChildMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "What to read");
        topAppBar.setNavigationOnClickListener(view -> activity
                .getSupportFragmentManager()
                .popBackStack());
    }

    private void getBooks() {
        databaseReference
                .child(activity.getString(R.string.n_books))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {
                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                Book book = dataSnapshot1.getValue(Book.class);

                                if (book != null) {

                                    book.setUid(dataSnapshot1.getKey());
                                    books.add(book);
                                }
                            }
                        }
                        areBooksDone = true;
                    } else {
                        Log.e("GET_BOOKS_DB", String.valueOf(task.getException()));
                    }
                });
    }

    private void setCategoryDialogFragmentListener() {
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                List<String> chosenCategories = bundle.getStringArrayList("bundleKey");
                updateChosenCategories(chosenCategories);
            }
        });
    }

    private void chooseCategory(View view) {
        CategoryDialogFragment categoryDialog = new CategoryDialogFragment(chosenCategories);
        categoryDialog.show(activity.getSupportFragmentManager(), "chooseCategory");
    }

    public void updateChosenCategories(List<String> chosenCategories) {
        this.chosenCategories = chosenCategories;

        String listedCategories = "";
        boolean first = true;
        for (String category: chosenCategories) {
            if (first) {
                first = false;
            } else {
                category = "; " + category;
            }
            listedCategories = listedCategories.concat(category);
        }
        Objects.requireNonNull(layoutCategory.getEditText()).setText(listedCategories);
    }

    private void showResultedBook(View view) {
        if (areBooksDone) {
            Book book = getRandomBookBasedOnCategories();
            WhatToReadResultDialogFragment whatToReadDialog = new WhatToReadResultDialogFragment(book);
            whatToReadDialog.show(activity.getSupportFragmentManager(), "whatToReadResult");
        } else {
            Snackbar.make(view, activity.getString(R.string.still_processing_books), Snackbar.LENGTH_SHORT).show();
        }
    }

    private Book getRandomBookBasedOnCategories() {
        List<Book> filteredBooks = getFilteredBooks();
        if (filteredBooks.size() == 0) {
            return null;
        } else {
            Random random = new Random();
            Book book = filteredBooks.get(random.nextInt(filteredBooks.size()));
            return book;
        }
    }

    private List<Book> getFilteredBooks() {
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book: books) {
            if (doesBookIncludeChosenCategories(book)) {
                filteredBooks.add(book);
            }
        }
        return filteredBooks;
    }

    private boolean doesBookIncludeChosenCategories(Book book) {
        return book.getChosenCategories().containsAll(chosenCategories);
    }
}
