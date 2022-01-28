package com.ad_victoriam.libtex.common.utils;

import android.content.Context;
import android.graphics.Color;

import com.ad_victoriam.libtex.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;

public class TopAppBarState {

    private static TopAppBarState instance;

    public static synchronized TopAppBarState get() {
        if (instance == null) {
            instance = new TopAppBarState();
        }
        return instance;
    }

    private final int backArrowResId = R.drawable.ic_baseline_arrow_back_24;

    public int getBackArrowResId() {
        return backArrowResId;
    }

    public void setChildMode(Context context, MaterialToolbar materialToolbar) {
        int colorResId = context.getResources().getColor(R.color.royal_blue, context.getTheme());
        materialToolbar.setNavigationIcon(backArrowResId);
        materialToolbar.setNavigationIconTint(colorResId);
        materialToolbar.setBackgroundColor(Color.WHITE);
        materialToolbar.setElevation(0);
    }

    public void setSearchMode() {
        //
    }

    public void setTitleMode(Context context, MaterialToolbar materialToolbar, String title) {
        int colorResId = context.getResources().getColor(R.color.royal_blue, context.getTheme());
        materialToolbar.setTitle(title);
        materialToolbar.setTitleCentered(true);
        materialToolbar.setTitleTextColor(colorResId);
    }

    public void setAdminHomeIcon(Context context, MaterialToolbar materialToolbar) {

    }
}