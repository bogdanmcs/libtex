package com.ad_victoriam.libtex.user.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.material.appbar.MaterialToolbar;

public class ReservationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_details);

        setTopAppBar();
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBar.get().setTitleMode(this, topAppBar, "About reservations");
        TopAppBar.get().setBookDetailsMode(this, topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
