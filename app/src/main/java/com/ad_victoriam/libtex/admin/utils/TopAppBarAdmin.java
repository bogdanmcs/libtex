package com.ad_victoriam.libtex.admin.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.ad_victoriam.libtex.R;
import com.google.android.material.appbar.MaterialToolbar;

public class TopAppBarAdmin {

    private static TopAppBarAdmin instance;

    public static synchronized TopAppBarAdmin get() {
        if (instance == null) {
            instance = new TopAppBarAdmin();
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

    private void setAux(Context context, MaterialToolbar materialToolbar) {
        materialToolbar.setBackgroundColor(Color.WHITE);
        materialToolbar.setElevation(0);
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
}
