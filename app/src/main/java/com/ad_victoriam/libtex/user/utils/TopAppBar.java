package com.ad_victoriam.libtex.user.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.ActionMenuItemView;

import com.ad_victoriam.libtex.R;
import com.google.android.material.appbar.MaterialToolbar;

public class TopAppBar {

    private static TopAppBar instance;

    public static synchronized TopAppBar get() {
        if (instance == null) {
            instance = new TopAppBar();
        }
        return instance;
    }

    public void setNormalMode(Context context, MaterialToolbar materialToolbar) {
        materialToolbar.setNavigationIcon(null);
        setAux(context, materialToolbar);
    }

    public void setChildMode(Context context, MaterialToolbar materialToolbar) {
        int colorLibtexPrimary = context.getResources().getColor(R.color.libtex_primary, context.getTheme());
        int backArrowResId = R.drawable.ic_baseline_arrow_back_24;
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

    public void setTitleMode(Context context, MaterialToolbar materialToolbar, String title) {
        int colorResId = context.getResources().getColor(R.color.royal_blue, context.getTheme());
        materialToolbar.setTitle(title);
        materialToolbar.setTitleCentered(true);
        materialToolbar.setTitleTextColor(colorResId);
    }

    @SuppressLint("RestrictedApi")
    public void setFavMode(Context context, MaterialToolbar materialToolbar, boolean favIsOn) {

        ActionMenuItemView menuItem = materialToolbar.findViewById(R.id.addToFav);
        if (favIsOn) {
            menuItem.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_heart_on_24));
        } else {
            menuItem.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_heart_24));
        }
        menuItem.setVisibility(View.VISIBLE);
    }
}
