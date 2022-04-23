package com.ad_victoriam.libtex.user.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.fragments.account.AccountFragment;
import com.ad_victoriam.libtex.user.fragments.books.BooksFragment;
import com.ad_victoriam.libtex.user.fragments.home.HomeFragment;
import com.ad_victoriam.libtex.user.fragments.loans.ActiveLoansFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView,
                new HomeFragment()).commit();
    }

    private final NavigationBarView.OnItemSelectedListener navListener =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment fragment = null;

                    int id = item.getItemId();

                    if (id == R.id.homeFragment) {
                        fragment = new HomeFragment();
                    } else if (id == R.id.activeLoansFragment) {
                        fragment = new ActiveLoansFragment();
                    } else if (id == R.id.booksFragment) {
                        fragment = new BooksFragment();
                    } else if (id == R.id.accountFragment) {
                        fragment = new AccountFragment();
                    }

                    if (fragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainerView, fragment)
                                .commit();
                    }
                    return true;
                }
            };
}
