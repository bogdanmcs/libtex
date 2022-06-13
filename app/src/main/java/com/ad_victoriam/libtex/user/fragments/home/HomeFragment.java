package com.ad_victoriam.libtex.user.fragments.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.models.GeneralLoan;
import com.ad_victoriam.libtex.user.adapters.MostPopularAdapter;
import com.ad_victoriam.libtex.user.adapters.RecommendationsAdapter;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.Loan;
import com.ad_victoriam.libtex.user.models.RatedBook;
import com.ad_victoriam.libtex.user.models.Review;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private View mainView;
    private FragmentActivity activity;

    private RecyclerView recommendationsRecyclerView;
    private RecommendationsAdapter recommendationsAdapter;
    private TextView tNoRecommendations;

    private RecyclerView mostPopularRecyclerView;
    private MostPopularAdapter mostPopularAdapter;
    private TextView tNoMostPopular;

    private RecyclerView highestRatedRecyclerView;
    private MostPopularAdapter highestRatedAdapter;
    private TextView tNoHighestRated;

    private List<Book> recommendedBooks = new ArrayList<>();
    private List<Book> mostPopularBooks = new ArrayList<>();
    private List<Book> highestRatedBooks = new ArrayList<>();

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

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        findViews();
        setTopAppBar();
        getUserLoansHistory();
        getGeneralLoans();

        return mainView;
    }

    private void findViews() {
        recommendationsRecyclerView = mainView.findViewById(R.id.recommendationsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (activity, LinearLayoutManager.HORIZONTAL, false);
        recommendationsRecyclerView.setLayoutManager(linearLayoutManager);
        recommendationsAdapter = new RecommendationsAdapter(activity, recommendedBooks);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);
        tNoRecommendations = mainView.findViewById(R.id.tNoRecommendations);

        mostPopularRecyclerView = mainView.findViewById(R.id.mostPopularRecyclerView);
        linearLayoutManager = new LinearLayoutManager
                (activity, LinearLayoutManager.HORIZONTAL, false);
        mostPopularRecyclerView.setLayoutManager(linearLayoutManager);
        mostPopularAdapter = new MostPopularAdapter(activity, mostPopularBooks);
        mostPopularRecyclerView.setAdapter(mostPopularAdapter);
        tNoMostPopular = mainView.findViewById(R.id.tNoMostPopular);

        highestRatedRecyclerView = mainView.findViewById(R.id.highestRatedRecyclerView);
        linearLayoutManager = new LinearLayoutManager
                (activity, LinearLayoutManager.HORIZONTAL, false);
        highestRatedRecyclerView.setLayoutManager(linearLayoutManager);
        highestRatedAdapter = new MostPopularAdapter(activity, highestRatedBooks);
        highestRatedRecyclerView.setAdapter(highestRatedAdapter);
        tNoHighestRated = mainView.findViewById(R.id.tNoHighestRated);

        MaterialButton bWhatToRead = mainView.findViewById(R.id.bWhatToRead);
        bWhatToRead.setOnClickListener(this::goWhatToRead);
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Home");
    }

    private void getUserLoansHistory() {
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

                                if (loan != null &&
                                    !bookUids.contains(loan.getBookUid())) {

                                    bookUids.add(loan.getBookUid());
                                }
                            }
                        }
                        getBooks(bookUids);

                    } else {
                        Log.e("GET_USER_LOANS_HISTORY", String.valueOf(task.getException()));
                    }
                });
    }

    private void getBooks(List<String> bookUids) {
        recommendedBooks.clear();
        List<Book> recommendableBooks = recommendedBooks;
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
                                        recommendableBooks.add(book);
                                    }
                                }
                            }
                        }
                        setRecommendations(recommendableBooks, userBooks);
                        List<Book> books = new ArrayList<>();
                        books.addAll(recommendableBooks);
                        books.addAll(userBooks);
                        getReviews(books);

                    } else {
                        Log.e("GET_BOOKS", String.valueOf(task.getException()));
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
        mostPreferredCategories = mostPreferredCategories.stream()
                .sorted(Comparator.comparing(List::size))
                .collect(Collectors.toList());

        if (!mostPreferredCategories.isEmpty()) {
            List<String> primaryCategory = mostPreferredCategories.remove(0);

            List<Book> toRemove = new ArrayList<>();
            for (Book book: books) {

                boolean isRecommendable = false;

                if (mostPreferredCategories.size() == 0) {
                    if (book.getChosenCategories().containsAll(primaryCategory)) {
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
        } else {
            recommendedBooks.clear();
        }
        updateRecommendationsUi(recommendedBooks);
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
            recommendationsRecyclerView.setVisibility(View.GONE);
        } else {
            tNoRecommendations.setVisibility(View.GONE);
            recommendationsAdapter.notifyDataSetChanged();
        }
    }

    private void getGeneralLoans() {
        Query query = databaseReference
                .child(activity.getString(R.string.n_general_loans))
                .orderByChild("counter")
                .limitToLast(20);
        List<GeneralLoan> generalLoans = new ArrayList<>();
        query
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                            GeneralLoan generalLoan = dataSnapshot.getValue(GeneralLoan.class);

                            if (generalLoan != null) {
                                generalLoans.add(generalLoan);
                            }
                        }
                        getBooksAndSetMostPopular(generalLoans);

                    } else {
                        Log.e("GET_GENERAL_LOANS_DB", String.valueOf(task.getException()));
                    }
                });
    }

    private void getBooksAndSetMostPopular(List<GeneralLoan> generalLoans) {
        List<Book> books = new ArrayList<>();

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
                        setMostPopular(generalLoans, books);

                    } else {
                        Log.e("GET_BOOKS", String.valueOf(task.getException()));
                    }
                });
    }

    private void setMostPopular(List<GeneralLoan> generalLoans, List<Book> books) {
        for (Book book: books) {
            if (isBookInGeneralLoans(book, generalLoans) &&
                isNotDuplicated(book)) {

                mostPopularBooks.add(book);
            }
        }
        updateMostPopularUi();
    }

    private boolean isBookInGeneralLoans(Book book, List<GeneralLoan> generalLoans) {
        for (GeneralLoan generalLoan: generalLoans) {
            if (isSameBook(book, generalLoan)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotDuplicated(Book book) {
        for (Book b: mostPopularBooks) {
            if (b.isSame(book)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameBook(Book book, GeneralLoan generalLoan) {
        return book.getTitle().equals(generalLoan.getBookTitle()) &&
                book.getAuthorName().equals(generalLoan.getBookAuthor()) &&
                book.getPublisher().equals(generalLoan.getBookPublisher());
    }

    private void updateMostPopularUi() {
        if (mostPopularBooks.size() == 0) {
            mostPopularRecyclerView.setVisibility(View.GONE);
        } else {
            tNoMostPopular.setVisibility(View.GONE);
            mostPopularAdapter.notifyDataSetChanged();
        }
    }

    private void getReviews(List<Book> books) {
        Map<Book, Double> ratedBooks = new HashMap<>();
        databaseReference
                .child(activity.getString(R.string.n_reviews))
                .get()
                .addOnSuccessListener(task -> {
                    updateHighestRatedUi(task.hasChildren());
//                    Map<Book, List<>>
                    for (DataSnapshot dataSnapshot: task.getChildren()) {

                        Review review = dataSnapshot.getValue(Review.class);

                        if (review != null) {
                            Book reviewBook = getBookOfReview(books, review);

                            if (reviewBook != null) {
                                Book ratedBook = getContainedRatedBook(ratedBooks, reviewBook);

                                if (ratedBook != null) {
                                    Double newRating = ratedBooks.get(ratedBook);
                                    newRating += review.getRating();
                                    ratedBooks.replace(ratedBook, newRating);
                                } else {
                                    ratedBooks.put(reviewBook, review.getRating());
                                }
                            }
                        }
                    }
                    setHighestRated(ratedBooks);
                })
                .addOnFailureListener(e -> {
                    Log.e("GET_REVIEWS_DB", e.toString());
                });
    }

    private Book getContainedRatedBook(Map<Book, Double> ratedBooks, Book reviewBook) {
       for (Map.Entry<Book, Double> entry: ratedBooks.entrySet()) {
           if (entry.getKey().isSame(reviewBook)) {
               return entry.getKey();
           }
       }
       return null;
    }

    private Book getBookOfReview(List<Book> books, Review review) {
        for (Book book: books) {
            if (isSameBook(book, review)) {
                return book;
            }
        }
        return null;
    }

    private void setHighestRated(Map<Book, Double> books) {
        List<RatedBook> ratedBooks = new ArrayList<>();
        for (Map.Entry<Book, Double> entry: books.entrySet()) {
            ratedBooks.add(new RatedBook(entry.getKey(), entry.getValue()));
        }
        ratedBooks = ratedBooks.stream()
                .sorted(Comparator.comparing(RatedBook::getRating).reversed())
                .limit(50)
                .collect(Collectors.toList());
        highestRatedBooks.clear();
        for (RatedBook ratedBook: ratedBooks) {
            highestRatedBooks.add(ratedBook.getBook());
        }
        highestRatedAdapter.notifyDataSetChanged();
    }

    private boolean isSameBook(Book book, Review review) {
        return book.getTitle().equals(review.getBookTitle()) &&
                book.getAuthorName().equals(review.getBookAuthor()) &&
                book.getPublisher().equals(review.getBookPublisher());
    }

    private void updateHighestRatedUi(boolean hasChildren) {
        if (!hasChildren) {
            highestRatedRecyclerView.setVisibility(View.GONE);
        } else {
            tNoHighestRated.setVisibility(View.GONE);
            highestRatedAdapter.notifyDataSetChanged();
        }
    }

    private void goWhatToRead(View view) {
        activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new WhatToReadFragment())
                .addToBackStack("homeFragment")
                .commit();
    }
}