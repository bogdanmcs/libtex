package com.ad_victoriam.libtex.user.fragments.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.adapters.FavouriteBooksAdapter;
import com.ad_victoriam.libtex.user.models.BookFav;
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
    private FavouriteBooksAdapter favouriteBooksAdapter;

    private final List<BookFav> favouriteBooks = new ArrayList<>();

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

        setTopAppBar();
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

    private void setFavouriteBooks() {
        recyclerView = mainView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        favouriteBooksAdapter = new FavouriteBooksAdapter(activity, favouriteBooks);
        recyclerView.setAdapter(favouriteBooksAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference
                .child(getString(R.string.n_users))
                .child(currentUser.getUid())
                .child(getString(R.string.n_favourite_books))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                BookFav bookFav = dataSnapshot.getValue(BookFav.class);

                                if (bookFav != null) {
                                    favouriteBooks.add(bookFav);
                                }
                            }
                            favouriteBooksAdapter.notifyDataSetChanged();
                        } else {
                            System.out.println(task.getResult());
                        }
                    }
                });
    }
}