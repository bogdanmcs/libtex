package com.ad_victoriam.libtex.user.fragments.home;

import android.app.Dialog;
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
import com.ad_victoriam.libtex.admin.utils.Category;
import com.ad_victoriam.libtex.user.adapters.CategoryDialogAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryDialogFragment extends AppCompatDialogFragment {

    private RecyclerView recyclerView;
    private CategoryDialogAdapter categoryAdapter;

    private final List<String> availableCategories = new ArrayList<>();
    private final List<String> chosenCategories;

    public CategoryDialogFragment(List<String> chosenCategories) {
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
                        Bundle result = new Bundle();
                        result.putStringArrayList("bundleKey", (ArrayList<String>) chosenCategories);
                        getParentFragmentManager().setFragmentResult("requestKey", result);
                    }
                });
        for (Category category: Category.values()) {
            availableCategories.add(category.name());
        }
        categoryAdapter = new CategoryDialogAdapter(this, getContext(), availableCategories, chosenCategories);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(categoryAdapter);

        return builder.create();
    }

    public void addCategory(String category) {
        chosenCategories.add(category);
    }

    public void removeCategory(String category) {
        chosenCategories.remove(category);
    }
}

