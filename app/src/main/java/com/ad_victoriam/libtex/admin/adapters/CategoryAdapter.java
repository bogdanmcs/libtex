package com.ad_victoriam.libtex.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.utils.CategoryDialog;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final CategoryDialog categoryDialog;
    private Context context;
    private final List<String> availableCategories;

    private final List<String> chosenCategories;

    public CategoryAdapter(CategoryDialog categoryDialog, Context context, List<String> availableCategories, List<String> chosenCategories) {
        this.categoryDialog = categoryDialog;
        this.context = context;
        this.availableCategories = availableCategories;
        this.chosenCategories = chosenCategories;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        String availableCategory = availableCategories.get(position);
        holder.tCategory.setText(availableCategory);
        if (chosenCategories.contains(availableCategory)) {
            holder.cCategory.setChecked(true);
        }
        holder.cCategory.setOnCheckedChangeListener((compoundButton, b) -> chooseCategory(compoundButton, position));
    }

    private void chooseCategory(CompoundButton compoundButton, int position) {
        if (compoundButton.isChecked()) {
            categoryDialog.addCategory(availableCategories.get(position));
        } else {
            categoryDialog.removeCategory(availableCategories.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return availableCategories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tCategory;
        CheckBox cCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tCategory = itemView.findViewById(R.id.tCategory);
            cCategory = itemView.findViewById(R.id.cCategory);
        }
    }
}
