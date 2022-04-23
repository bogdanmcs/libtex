package com.ad_victoriam.libtex.admin.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.adapters.CategoryAdapter;
import com.ad_victoriam.libtex.admin.utils.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDialog extends AppCompatDialogFragment {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

    private CategoryDialogListener categoryDialogListener;

    private final List<String> availableCategories = new ArrayList<>();
    private final List<String> chosenCategories;

    public CategoryDialog(List<String> chosenCategories) {
        this.chosenCategories = chosenCategories;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_category, null);

        builder.setView(view)
                .setTitle("Choose category")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        categoryDialogListener.saveCategories(chosenCategories);
                    }
                });

        for (Category category: Category.values()) {
            availableCategories.add(category.name());
        }

        categoryAdapter = new CategoryAdapter(this, getContext(), availableCategories, chosenCategories);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoryAdapter);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            categoryDialogListener = (CategoryDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement CategoryDialogListener");
        }
    }

    public void addCategory(String category) {
        chosenCategories.add(category);
    }

    public void removeCategory(String category) {
        chosenCategories.remove(category);
    }

    public interface CategoryDialogListener{

        void saveCategories(List<String> chosenCategories);
    }
}
