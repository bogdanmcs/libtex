package com.ad_victoriam.libtex.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.material.appbar.MaterialToolbar;

public class BookDetailsFragment extends Fragment {

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

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_book_details, container, false);
        activity = requireActivity();

        setTopAppBar();
        setDetails();

        return mainView;
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBarState.get().setChildMode(activity, topAppBar);
        TopAppBarState.get().setTitleMode(activity, topAppBar, "Book details");
        TopAppBarState.get().setFavMode(activity, topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(mainView).navigate(R.id.action_bookDetailsFragment_to_booksFragment);
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.addToFav) {
                    if (isFav) {
                        // TODO: remove from user's favs
                        item.setIcon(AppCompatResources.getDrawable(activity, R.drawable.ic_heart_24));
                    } else {
                        // TODO: add to user's favs
                        item.setIcon(AppCompatResources.getDrawable(activity, R.drawable.ic_heart_on_24));
                    }
                    isFav = !isFav;
                }
                return false;
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
    }

}