package com.ad_victoriam.libtex.user.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BooksFragment extends Fragment {

    private DatabaseReference databaseReference;

    private FragmentActivity activity;

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

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        activity = requireActivity();
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBarState.get().setNormalMode(activity, topAppBar);
        TopAppBarState.get().setTitleMode(activity, topAppBar, "Books");

        attachBooksListener();

        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    private void attachBooksListener() {

    }
}