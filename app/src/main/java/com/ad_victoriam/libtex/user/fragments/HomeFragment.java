package com.ad_victoriam.libtex.user.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.material.appbar.MaterialToolbar;

public class HomeFragment extends Fragment {

    private FragmentActivity activity;

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

        activity = requireActivity();
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Home");

        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}