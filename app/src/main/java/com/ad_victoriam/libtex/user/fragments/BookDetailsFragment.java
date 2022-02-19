package com.ad_victoriam.libtex.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
                    item.setIcon(AppCompatResources.getDrawable(activity, R.drawable.ic_heart_on_24));
                }
                return false;
            }
        });
    }
}