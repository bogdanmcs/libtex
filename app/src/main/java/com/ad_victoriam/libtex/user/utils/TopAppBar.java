package com.ad_victoriam.libtex.user.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.content.res.AppCompatResources;

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

    public void setBookDetailsMode(Context context, MaterialToolbar materialToolbar) {
        int backArrowResId = R.drawable.ic_baseline_cancel_24;
        materialToolbar.setNavigationIcon(backArrowResId);
        materialToolbar.setNavigationIconTint(Color.WHITE);
        materialToolbar.setBackgroundColor(Color.DKGRAY);
        materialToolbar.setTitleTextColor(Color.WHITE);
    }

    private void setAux(Context context, MaterialToolbar materialToolbar) {
        materialToolbar.setBackgroundColor(Color.WHITE);
        materialToolbar.setElevation(0);
        Menu menu = materialToolbar.getMenu();
        menu.removeGroup(0);
    }

    public void setTitleMode(Context context, MaterialToolbar materialToolbar, String title) {
        int colorResId = context.getResources().getColor(R.color.royal_blue, context.getTheme());
        materialToolbar.setTitle(title);
        materialToolbar.setTitleCentered(true);
        materialToolbar.setTitleTextColor(colorResId);
    }

    public void setFavMode(Context context, MaterialToolbar materialToolbar, boolean favIsOn) {
        Menu menu = materialToolbar.getMenu();
        menu.removeGroup(0);
        MenuItem menuItem = menu.add(0, Menu.FIRST, 0, "Fav");

        if (favIsOn) {
            menuItem.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_heart_on_24));
        } else {
            menuItem.setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_heart_24));
        }
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}
