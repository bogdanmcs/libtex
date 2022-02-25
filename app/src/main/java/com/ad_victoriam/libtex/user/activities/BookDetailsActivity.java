package com.ad_victoriam.libtex.user.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.utils.TopAppBar;
import com.google.android.material.appbar.MaterialToolbar;

public class BookDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        setTopAppBar();
    }

    private void setTopAppBar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBar.get().setTitleMode(this, topAppBar, "Book Details");
        TopAppBar.get().setBookDetailsMode(this, topAppBar);
        TopAppBar.get().setFavMode(this, topAppBar, false);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
