package com.ad_victoriam.libtex.admin.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailsActivity extends AppCompatActivity {

    private User user;


    private MaterialButton bSetUserDetailsState;
    private boolean areUserDetailsShown = false;

    private SwitchMaterial sEditMode;

    private MaterialButton bCommitEditChanges;

    private TextInputEditText eEmail;
    private TextInputEditText eFullName;
    private TextInputEditText eIdCardSeries;
    private TextInputEditText eIdCardNumber;
    private TextInputEditText eDob;
    private TextInputEditText ePhoneNumber;
    private TextView tEmailHelper;
    private TextView tFullNameHelper;
    private TextView tIdCardSeriesHelper;
    private TextView tIdCardNumberHelper;
    private TextView tDobHelper;
    private TextView tPhoneNumberHelper;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutFullName;
    private TextInputLayout layoutIdCardSeries;
    private TextInputLayout layoutIdCardNumber;
    private TextInputLayout layoutDob;
    private TextInputLayout layoutPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_details);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarState.get().setChildMode(this, topAppBar);
        TopAppBarState.get().setTitleMode(this, topAppBar, "User details");
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

        bSetUserDetailsState = findViewById(R.id.bSetUserDetailsState);
        bSetUserDetailsState.setOnClickListener(this::setUserDetailsState);

        sEditMode = findViewById(R.id.sEditMode);
        sEditMode.setOnCheckedChangeListener(this::setEditMode);

        bCommitEditChanges = findViewById(R.id.bCommitEditChanges);
        bCommitEditChanges.setOnClickListener(this::commitEditChanges);

        initializeDetailsUi();

        if (user == null && getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");

            eEmail.setText(user.getEmail());
            eFullName.setText(user.getFullName());
            eIdCardSeries.setText(user.getIdCardSeries());
            eIdCardNumber.setText(user.getIdCardNumber());
            eDob.setText(user.getDateOfBirthday());
            ePhoneNumber.setText(user.getPhoneNumber());
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        final MaterialButton bDeleteUser = findViewById(R.id.bDeleteUser);
        final MaterialButton bAssignBook = findViewById(R.id.bAssignBook);
        final MaterialButton bReturnBook = findViewById(R.id.bReturnBook);
        bDeleteUser.setOnClickListener(this::deleteUser);
        bAssignBook.setOnClickListener(this::assignBook);
        bReturnBook.setOnClickListener(this::returnBook);
    }

    private void setEditMode(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isChecked()) {
            setEditableState(true);
        } else {
            setEditableState(false);
        }
    }

    private void initializeDetailsUi() {
        eEmail = findViewById(R.id.eEmail);
        eFullName = findViewById(R.id.eFullName);
        eIdCardSeries = findViewById(R.id.eIdCardSeries);
        eIdCardNumber = findViewById(R.id.eIdCardNumber);
        eDob = findViewById(R.id.eDob);
        ePhoneNumber = findViewById(R.id.ePhoneNumber);
        tEmailHelper = findViewById(R.id.tEmailHelper);
        tFullNameHelper = findViewById(R.id.tFullNameHelper);
        tIdCardSeriesHelper = findViewById(R.id.tIdCardSeriesHelper);
        tIdCardNumberHelper = findViewById(R.id.tIdCardNumberHelper);
        tDobHelper = findViewById(R.id.tDobHelper);
        tPhoneNumberHelper = findViewById(R.id.tPhoneNumberHelper);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutFullName = findViewById(R.id.layoutFullName);
        layoutIdCardSeries = findViewById(R.id.layoutIdCardSeries);
        layoutIdCardNumber = findViewById(R.id.layoutIdCardNumber);
        layoutDob = findViewById(R.id.layoutDob);
        layoutPhoneNumber = findViewById(R.id.layoutPhoneNumber);
        setEditableState(false);
        hideUserDetails();
    }

    private void setEditableState(boolean isEditable) {
        layoutEmail.setEnabled(false);
        layoutFullName.setEnabled(isEditable);
        layoutIdCardSeries.setEnabled(isEditable);
        layoutIdCardNumber.setEnabled(isEditable);
        layoutDob.setEnabled(isEditable);
        layoutPhoneNumber.setEnabled(isEditable);
    }

    private void setUserDetailsState(View view) {
        if (areUserDetailsShown) {
            hideUserDetails();
            bSetUserDetailsState.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_arrow_right_24));
        } else {
            showUserDetails();
            bSetUserDetailsState.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_arrow_drop_down_24));
        }
        areUserDetailsShown = !areUserDetailsShown;
    }

    private void showUserDetails() {
        sEditMode.setVisibility(View.VISIBLE);
        layoutEmail.setVisibility(View.VISIBLE);
        layoutFullName.setVisibility(View.VISIBLE);
        layoutIdCardSeries.setVisibility(View.VISIBLE);
        layoutIdCardNumber.setVisibility(View.VISIBLE);
        layoutDob.setVisibility(View.VISIBLE);
        layoutPhoneNumber.setVisibility(View.VISIBLE);
        tEmailHelper.setVisibility(View.VISIBLE);
        tFullNameHelper.setVisibility(View.VISIBLE);
        tIdCardSeriesHelper.setVisibility(View.VISIBLE);
        tIdCardNumberHelper.setVisibility(View.VISIBLE);
        tDobHelper.setVisibility(View.VISIBLE);
        tPhoneNumberHelper.setVisibility(View.VISIBLE);
        bCommitEditChanges.setVisibility(View.VISIBLE);
    }

    private void hideUserDetails() {
        sEditMode.setVisibility(View.GONE);
        layoutEmail.setVisibility(View.GONE);
        layoutFullName.setVisibility(View.GONE);
        layoutIdCardSeries.setVisibility(View.GONE);
        layoutIdCardNumber.setVisibility(View.GONE);
        layoutDob.setVisibility(View.GONE);
        layoutPhoneNumber.setVisibility(View.GONE);
        tEmailHelper.setVisibility(View.GONE);
        tFullNameHelper.setVisibility(View.GONE);
        tIdCardSeriesHelper.setVisibility(View.GONE);
        tIdCardNumberHelper.setVisibility(View.GONE);
        tDobHelper.setVisibility(View.GONE);
        tPhoneNumberHelper.setVisibility(View.GONE);
        bCommitEditChanges.setVisibility(View.GONE);
    }

    private void deleteUser(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to remove this user?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    // verify if deletion is successful or not
                    databaseReference.child("users").child(user.getUid()).removeValue();
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }

    private void assignBook(View view) {
        Intent intent = new Intent(this, BooksActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("action", "BORROW");
        startActivity(intent);
    }

    private void returnBook(View view) {
        // view current loaned books for this user
        Intent intent = new Intent(this, UserCurrentLoansActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void commitEditChanges(View view) {
        Toast.makeText(this, "commiiiiiiiit", Toast.LENGTH_SHORT).show();
    }
}