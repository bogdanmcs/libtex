package com.ad_victoriam.libtex.admin.activities.books;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.admin.utils.CategoryDialog;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminBookDetailsActivity extends AppCompatActivity implements CategoryDialog.CategoryDialogListener {

    DatabaseReference databaseReference;

    private AdminBook adminBook;

    private SwitchMaterial sEditMode;
    private MaterialButton bSaveEditChanges;
    private TextInputLayout layoutTitle;
    private TextInputLayout layoutAuthorName;
    private TextInputLayout layoutPublisher;
    private TextInputLayout layoutCategory;
    private TextInputLayout layoutDescription;
    private TextInputLayout layoutNoOfPages;
    private TextInputLayout layoutTotalQuantity;
    private TextInputLayout layoutAvailableQuantity;

    private List<String> chosenCategories = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_book_details);

        databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Book details");
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                }
                return false;
            }
        });

        sEditMode = findViewById(R.id.sEditMode);
        sEditMode.setOnCheckedChangeListener(this::setEditMode);

        bSaveEditChanges = findViewById(R.id.bSaveEditChanges);
        bSaveEditChanges.setEnabled(false);
        bSaveEditChanges.setOnClickListener(this::bSaveEditChanges);

        initializeDetailsUi();

        if (adminBook == null && getIntent().hasExtra("book")) {
            adminBook = getIntent().getParcelableExtra("book");

            layoutTitle.getEditText().setText(adminBook.getTitle());
            layoutAuthorName.getEditText().setText(adminBook.getAuthorName());
            layoutPublisher.getEditText().setText(adminBook.getPublisher());
            chosenCategories = adminBook.getChosenCategories();
            layoutCategory.getEditText().setText(getCategoriesString(chosenCategories));
            layoutDescription.getEditText().setText(adminBook.getDescription());
            layoutNoOfPages.getEditText().setText(adminBook.getNoOfPages());
            layoutTotalQuantity.getEditText().setText(String.valueOf(adminBook.getTotalQuantity()));
            layoutAvailableQuantity.getEditText().setText(String.valueOf(adminBook.getAvailableQuantity()));
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        final Button bDeleteBook = findViewById(R.id.bDeleteBook);
        bDeleteBook.setOnClickListener(this::deleteBook);
    }

    private void initializeDetailsUi() {
        layoutTitle = findViewById(R.id.layoutTitle);
        layoutAuthorName = findViewById(R.id.layoutAuthorName);
        layoutPublisher = findViewById(R.id.layoutPublisher);
        layoutCategory = findViewById(R.id.layoutCategory);
        layoutCategory.getEditText().setOnClickListener(this::chooseCategory);
        layoutNoOfPages = findViewById(R.id.layoutNoOfPages);
        layoutDescription = findViewById(R.id.layoutDescription);
        layoutTotalQuantity = findViewById(R.id.layoutTotalQuantity);
        layoutAvailableQuantity = findViewById(R.id.layoutAvailableQuantity);
        setEditableState(false);
    }

    private void chooseCategory(View view) {
        CategoryDialog categoryDialog = new CategoryDialog(chosenCategories);
        categoryDialog.show(getSupportFragmentManager(), "chooseCategory");
    }

    private void bSaveEditChanges(View view) {

        if (areBookDetailsValid()) {
            String bookTitle = layoutTitle.getEditText().getText().toString();
            String bookAuthorName = layoutAuthorName.getEditText().getText().toString();
            String bookPublisher = layoutPublisher.getEditText().getText().toString();
            String bookNoOfPages = layoutNoOfPages.getEditText().getText().toString();
            String bookDescription = layoutDescription.getEditText().getText().toString();
            String bookAvailableQuantity = layoutAvailableQuantity.getEditText().getText().toString();

            int quantityDifference = Integer.parseInt(bookAvailableQuantity) - adminBook.getAvailableQuantity();
            int totalQuantityNewValue = adminBook.getTotalQuantity() + quantityDifference;
            String bookTotalQuantity = String.valueOf(totalQuantityNewValue);

            AdminBook updatedAdminBook = new AdminBook(
                    bookTitle, bookAuthorName, bookPublisher, chosenCategories,
                    bookNoOfPages, bookDescription, Integer.parseInt(bookTotalQuantity),
                    Integer.parseInt(bookAvailableQuantity));

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Map<String, Object> childUpdates =  new HashMap<>();
            String bookPath = "books/" + currentUser.getUid() + "/" + adminBook.getUid();
            childUpdates.put(bookPath + "/title", updatedAdminBook.getTitle());
            childUpdates.put(bookPath + "/authorName", updatedAdminBook.getAuthorName());
            childUpdates.put(bookPath + "/publisher", updatedAdminBook.getPublisher());
            childUpdates.put(bookPath + "/chosenCategories", updatedAdminBook.getChosenCategories());
            childUpdates.put(bookPath + "/description", updatedAdminBook.getDescription());
            childUpdates.put(bookPath + "/noOfPages", updatedAdminBook.getNoOfPages());
            childUpdates.put(bookPath + "/totalQuantity", updatedAdminBook.getTotalQuantity());
            childUpdates.put(bookPath + "/availableQuantity", updatedAdminBook.getAvailableQuantity());

            databaseReference.updateChildren(childUpdates);
            setEditableState(false);
            layoutTotalQuantity.getEditText().setText(bookTotalQuantity);
            Snackbar.make(view, "Book has been updated successfully", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean areBookDetailsValid() {
        boolean errorFlag = false;

        int DESCRIPTION_MAX_LIMIT = 200;
        int STANDARD_MAX_LIMIT = 50;
        int IDX_MAX_LIMIT = 4;
        layoutTitle.setError(null);
        layoutAuthorName.setError(null);
        layoutPublisher.setError(null);
        layoutCategory.setError(null);
        layoutDescription.setError(null);
        layoutNoOfPages.setError(null);
        layoutTotalQuantity.setError(null);

        String bookTotalQuantity = layoutTotalQuantity.getEditText().getText().toString();
        if (bookTotalQuantity.isEmpty()) {
            layoutTotalQuantity.setError(getString(R.string.empty_field));
            errorFlag = true;
        } else if (bookTotalQuantity.length() > 4) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + IDX_MAX_LIMIT;
            layoutTotalQuantity.setError(fieldMaxLimitMessage);
            errorFlag = true;
        } else {
            try {
                if (Integer.parseInt(bookTotalQuantity) < 0 ) {
                    layoutTotalQuantity.setError(getString(R.string.is_negative));
                    errorFlag = true;
                }
            } catch (NumberFormatException e) {
                layoutTotalQuantity.setError(getString(R.string.is_not_decimal));
                errorFlag = true;
            }
        }

        String bookAvailableQuantity = layoutAvailableQuantity.getEditText().getText().toString();
        if (bookAvailableQuantity.isEmpty()) {
            layoutAvailableQuantity.setError(getString(R.string.empty_field));
            errorFlag = true;
        } else if (bookAvailableQuantity.length() > 4) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + IDX_MAX_LIMIT;
            layoutAvailableQuantity.setError(fieldMaxLimitMessage);
            errorFlag = true;
        } else {
            try {
                if (Integer.parseInt(bookAvailableQuantity) < 0 ) {
                    layoutAvailableQuantity.setError(getString(R.string.is_negative));
                    errorFlag = true;
                } else if (Integer.parseInt(bookAvailableQuantity) > Integer.parseInt(bookTotalQuantity)) {
                    layoutAvailableQuantity.setError(getString(R.string.available_qt_exceeds));
                    errorFlag = true;
                }
            } catch (NumberFormatException e) {
                layoutAvailableQuantity.setError(getString(R.string.is_not_decimal));
                errorFlag = true;
            }
        }

        String bookNoOfPages = layoutNoOfPages.getEditText().getText().toString();
        if (bookNoOfPages.isEmpty()) {
            layoutNoOfPages.setError(getString(R.string.empty_field));
            errorFlag = true;
        } else if (bookNoOfPages.length() > 4) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + IDX_MAX_LIMIT;
            layoutNoOfPages.setError(fieldMaxLimitMessage);
            errorFlag = true;
        } else {
            try {
                if (Integer.parseInt(bookNoOfPages) <= 0 ) {
                    layoutNoOfPages.setError(getString(R.string.is_negative));
                    errorFlag = true;
                }
            } catch (NumberFormatException e) {
                layoutNoOfPages.setError(getString(R.string.is_not_decimal));
                errorFlag = true;
            }
        }

        String bookDescription = layoutDescription.getEditText().getText().toString();
        if (bookDescription.isEmpty()) {
            layoutDescription.setError(getString(R.string.empty_field));
            layoutDescription.requestFocus();
            errorFlag = true;
        } else if (bookDescription.length() > 200) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + DESCRIPTION_MAX_LIMIT;
            layoutDescription.setError(fieldMaxLimitMessage);
            errorFlag = true;
        }

        List<TextInputLayout> bookDetails = new ArrayList<>();
        bookDetails.add(layoutTitle);
        bookDetails.add(layoutAuthorName);
        bookDetails.add(layoutPublisher);

        for (TextInputLayout bookDetail: bookDetails) {
            String textToString = bookDetail.getEditText().getText().toString();
            if (textToString.isEmpty()) {
                bookDetail.setError(getString(R.string.empty_field));
                bookDetail.requestFocus();
                errorFlag = true;
            } else if (textToString.length() > 50) {
                String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
                bookDetail.setError(fieldMaxLimitMessage);
                errorFlag = true;
            }
        }

        if (chosenCategories.isEmpty()) {
            layoutCategory.setError(getString(R.string.category_not_selected));
            errorFlag = true;
        }

        return !errorFlag;
    }

    private void setEditMode(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isChecked()) {
            setEditableState(true);
        } else {
            setEditableState(false);
        }
    }

    private void setEditableState(boolean isEditable) {
        if (isEditable) {
            bSaveEditChanges.setEnabled(true);
            bSaveEditChanges.setBackgroundColor(getResources().getColor(R.color.dark_sea_green, getTheme()));
            layoutCategory.setEnabled(true);
            layoutCategory.getEditText().setFocusable(false);
        } else {
            bSaveEditChanges.setEnabled(false);
            bSaveEditChanges.setBackgroundColor(getResources().getColor(R.color.light_grey, getTheme()));
            layoutCategory.setEnabled(false);
        }
        sEditMode.setChecked(isEditable);
        layoutTitle.setEnabled(isEditable);
        layoutAuthorName.setEnabled(isEditable);
        layoutPublisher.setEnabled(isEditable);
        layoutDescription.setEnabled(isEditable);
        layoutNoOfPages.setEnabled(isEditable);
        layoutTotalQuantity.setEnabled(false);
        layoutAvailableQuantity.setEnabled(isEditable);
    }

    private void deleteBook(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to remove this book?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    // verify if deletion is successful or not
                    databaseReference.child("books").child(currentUser.getUid()).child(adminBook.getUid()).removeValue();
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }

    private String getCategoriesString(List<String> chosenCategories) {

        String listedCategories = "";
        boolean first = true;
        for (String category: chosenCategories) {
            if (first) {
                first = false;
            } else {
                category = "; " + category;
            }
            listedCategories = listedCategories.concat(category);
        }
        return listedCategories;
    }

    @Override
    public void saveCategories(List<String> chosenCategories) {

        this.chosenCategories = chosenCategories;
        String listedCategories = getCategoriesString(chosenCategories);
        layoutCategory.getEditText().setText(listedCategories);
    }
}