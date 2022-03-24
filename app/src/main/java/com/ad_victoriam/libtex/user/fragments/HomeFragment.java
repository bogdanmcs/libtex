package com.ad_victoriam.libtex.user.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.Loan;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private DatabaseReference databaseReference;

    private View mainView;
    private FragmentActivity activity;

    private MaterialButton bRec;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_home, container, false);
        activity = requireActivity();
        databaseReference = FirebaseDatabase
                .getInstance(activity.getString(R.string.firebase_url))
                .getReference();

        findViews();
        setTopAppBar();

        return mainView;
    }

    private void findViews() {
        bRec = mainView.findViewById(R.id.bRec);
        bRec.setOnClickListener(this::getRecs);
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Home");
    }

    private void getRecs(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        List<String> bookUids = new ArrayList<>();

        databaseReference
                .child(activity.getString(R.string.n_users))
                .child(currentUser.getUid())
                .child(activity.getString(R.string.n_book_loans))
                .child(activity.getString(R.string.n_loans_history))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {
                            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                Loan loan = dataSnapshot1.getValue(Loan.class);

                                if (loan != null) {
                                    bookUids.add(loan.getBookUid());
                                }
                            }
                        }
                        getBooks(bookUids);

                    } else {
                        Log.e("GET_USER_LOANS_HISTORY", String.valueOf(task.getResult()));
                    }
                });
    }

    private void getBooks(List<String> bookUids) {
        List<Book> books = new ArrayList<>();
        List<Book> userBooks = new ArrayList<>();

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

                                    if (bookUids.contains(book.getUid())) {
                                        userBooks.add(book);
                                    } else {
                                        books.add(book);
                                    }
                                }
                            }
                        }
                        setRecommendations(books, userBooks);

                    } else {
                        Log.e("GET_BOOKS", String.valueOf(task.getResult()));
                    }
                });
    }

    private void setRecommendations(List<Book> books, List<Book> userBooks) {
        // split books categories into pyramid format (limit: 1...n)
        Map<List<String>, Integer> bookCategoriesPyramid = getCategoriesPyramidMap(userBooks);

        // use highest value from each pyramid layer
        // map: (sizeOfList, noOfAppearances)
        Map<Integer, Integer> categoryAppearances = new HashMap<>();
        Map<Integer, List<String>> categoryTypes = new HashMap<>();

        for (Map.Entry<List<String>, Integer> entry: bookCategoriesPyramid.entrySet()) {

            if (categoryAppearances.containsKey(entry.getKey().size())) {

                Integer currentMax = categoryAppearances.get(entry.getKey().size());

                if (currentMax != null &&
                    entry.getValue() > currentMax) {

                    categoryAppearances.replace(entry.getKey().size(), entry.getValue());
                    categoryTypes.replace(entry.getKey().size(), entry.getKey());
                } else {
                    Log.e("SET_RECOM_HIGHEST_CATEG_USE", "Pyramid get value - null Integer");
                }
            } else {
                categoryAppearances.put(entry.getKey().size(), entry.getValue());
                categoryTypes.put(entry.getKey().size(), entry.getKey());
            }
        }

        List<List<String>> mostPreferredCategories = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry: categoryTypes.entrySet()) {
            mostPreferredCategories.add(entry.getValue());
        }

        List<Book> toRemove = new ArrayList<>();
        for (Book book: books) {

            boolean isRecommendable = false;

            if (mostPreferredCategories.size() == 1) {
                if (book.getChosenCategories().containsAll(mostPreferredCategories.get(0))) {
                    isRecommendable = true;
                }
            } else {
                for (List<String> categories: mostPreferredCategories) {
                    if (book.getChosenCategories().containsAll(categories)) {
                        isRecommendable = true;
                        break;
                    }
                }
            }
            if (!isRecommendable) {
                toRemove.add(book);
            }
        }
        books.removeAll(toRemove);
        updateRecommendationsUi(books);
    }

    @NonNull
    private Map<List<String>, Integer> getCategoriesPyramidMap(List<Book> books) {

        Map<List<String>, Integer> bookCategoriesPyramid = new HashMap<>();

        for (Book book: books) {
            for (int i = 1; i <= book.getChosenCategories().size(); i++) {

                List<List<String>> combs = getCategoryCombinationsOfKRange(book.getChosenCategories(), i);

                for (List<String> c: combs) {

                    if (bookCategoriesPyramid.containsKey(c)) {
                        Integer counter = bookCategoriesPyramid.get(c);
                        if (counter != null) {
                            bookCategoriesPyramid.replace(c, counter + 1);
                        } else {
                            Log.wtf("SET_RECOM", "Hashmap value - null Integer object");
                        }
                    } else {
                        bookCategoriesPyramid.put(c, 1);
                    }
                }
            }
        }
        return bookCategoriesPyramid;
    }


    private final List<List<String>> data = new ArrayList<>();

    private List<List<String>> getCategoryCombinationsOfKRange(List<String> categories, int k) {
        data.clear();

        List<String> currentData = new ArrayList<>();

        getCombinations(currentData, categories, k);

        removeDuplicates();

        return data;
    }

    private void removeDuplicates() {
        List<List<String>> toRemove = new ArrayList<>();

        for (List<String> d: data) {
            for (List<String> d1: data) {

                if (d != d1 &&
                    hasSameCategories(d, d1) &&
                    !toRemove.contains(d) &&
                    !toRemove.contains(d1)) {

                    toRemove.add(d1);
                }

            }
        }
        data.removeAll(toRemove);
    }

    private boolean hasSameCategories(List<String> categories1, List<String> categories2) {
        for (String category: categories1) {
            if (!categories2.contains(category)) {
                return false;
            }
        }

        return true;
    }

    private void getCombinations(List<String> currentData, List<String> categories, int k) {
        if (currentData.size() == k) {
            List<String> newList = new ArrayList<>(currentData);
            data.add(newList);
        } else {
            for (String category: categories) {
                if (!currentData.contains(category)) {
                    currentData.add(category);
                    getCombinations(currentData, categories, k);
                    currentData.remove(currentData.size() - 1);
                }
            }
        }
    }

    private void updateRecommendationsUi(List<Book> books) {
        if (books.size() == 0) {
            // no recommendations yet, keep reading
        } else {
            // show recommended books
        }
    }
}