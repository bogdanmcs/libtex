package com.ad_victoriam.libtex.user.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.activities.LoginActivity;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private FragmentActivity activity;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        activity = requireActivity();
        setTopAppBar();

        final MaterialButton bSignOut = view.findViewById(R.id.bSignOut);
        bSignOut.setOnClickListener(this::signOut);

        return view;
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = activity.findViewById(R.id.topAppBar);
        TopAppBar.get().setNormalMode(activity, topAppBar);
        TopAppBar.get().setTitleMode(activity, topAppBar, "Account");
    }

    private void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        activity.finish();
        startActivity(new Intent(activity, LoginActivity.class));
    }
}