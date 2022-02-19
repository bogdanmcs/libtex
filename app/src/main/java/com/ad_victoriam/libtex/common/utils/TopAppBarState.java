package com.ad_victoriam.libtex.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.ActionMenuItem;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

    public void setNormalMode(Context context, MaterialToolbar materialToolbar) {
        materialToolbar.setNavigationIcon(null);
        setAux(context, materialToolbar);
    }

    public void setChildMode(Context context, MaterialToolbar materialToolbar) {
        int colorLibtexPrimary = context.getResources().getColor(R.color.libtex_primary, context.getTheme());
        materialToolbar.setNavigationIcon(backArrowResId);
        materialToolbar.setNavigationIconTint(colorLibtexPrimary);
        setAux(context, materialToolbar);
    }

    @SuppressLint("RestrictedApi")
    private void setAux(Context context, MaterialToolbar materialToolbar) {
        materialToolbar.setBackgroundColor(Color.WHITE);
        materialToolbar.setElevation(0);
        ActionMenuItemView menuItem = materialToolbar.findViewById(R.id.addToFav);
        menuItem.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_heart_24));
        menuItem.setVisibility(View.GONE);
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

    public void setAdminHome(Context context, MaterialToolbar materialToolbar) {
        materialToolbar.setElevation(0);
        materialToolbar.findViewById(R.id.home).setVisibility(View.GONE);
    }

    public void setFavMode(Context context, MaterialToolbar materialToolbar) {
        ActionMenuItemView addToFav = materialToolbar.findViewById(R.id.addToFav);
        addToFav.setVisibility(View.VISIBLE);
    }
}
