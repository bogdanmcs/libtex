package com.ad_victoriam.libtex.admin.activities.users;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.admin.activities.books.BooksActivity;
import com.ad_victoriam.libtex.admin.fragments.DatePickerFragment;
import com.ad_victoriam.libtex.admin.utils.County;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserActionsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private DatabaseReference databaseReference;
    private User user;

    private MaterialButton bSetUserDetailsState;
    private boolean areUserDetailsShown = false;

    private SwitchMaterial sEditMode;

    private MaterialButton bSaveEditChanges;

    private TextInputEditText eEmail;
    private TextInputEditText eFullName;
    private TextInputEditText eIdCardSeries;
    private TextInputEditText eIdCardNumber;
    private TextInputEditText eDob;
    private TextInputEditText ePhoneNumber;
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

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Actions");
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

        bSaveEditChanges = findViewById(R.id.bSaveEditChanges);
        bSaveEditChanges.setEnabled(false);
        bSaveEditChanges.setOnClickListener(this::bSaveEditChanges);

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

        final MaterialButton bAddNewLoan = findViewById(R.id.bAddNewLoan);
        final MaterialButton bViewActiveLoans = findViewById(R.id.bViewActiveLoans);
        final MaterialButton bViewAllLoans = findViewById(R.id.bViewAllLoans);
        final MaterialButton bViewReservations = findViewById(R.id.bViewReservations);
        final MaterialButton bDeleteUser = findViewById(R.id.bDeleteUser);
        bAddNewLoan.setOnClickListener(this::assignBook);
        bViewActiveLoans.setOnClickListener(this::viewActiveLoans);
        bViewAllLoans.setOnClickListener(this::viewAllLoans);
        bViewReservations.setOnClickListener(this::viewReservations);
        bDeleteUser.setOnClickListener(this::deleteUser);
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
        eDob.setOnClickListener(this::pickDate);
        eDob.setFocusable(false);
        ePhoneNumber = findViewById(R.id.ePhoneNumber);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutFullName = findViewById(R.id.layoutFullName);
        layoutIdCardSeries = findViewById(R.id.layoutIdCardSeries);
        layoutIdCardNumber = findViewById(R.id.layoutIdCardNumber);
        layoutDob = findViewById(R.id.layoutDob);
        layoutPhoneNumber = findViewById(R.id.layoutPhoneNumber);
        setEditableState(false);
        hideUserDetails();
    }

    private void pickDate(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        try {
            String date = DateFormat.getDateInstance().format(calendar.getTime());
            eDob.setText(date);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEditableState(boolean isEditable) {
        if (isEditable) {
            bSaveEditChanges.setEnabled(true);
            bSaveEditChanges.setBackgroundColor(getResources().getColor(R.color.dark_sea_green, getTheme()));
        } else {
            bSaveEditChanges.setEnabled(false);
            bSaveEditChanges.setBackgroundColor(getResources().getColor(R.color.light_grey, getTheme()));
        }
        sEditMode.setChecked(isEditable);
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
            bSetUserDetailsState.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_arrow_forward_ios_24));
        } else {
            showUserDetails();
            bSetUserDetailsState.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_arrow_drop_down_circle_24));
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
        bSaveEditChanges.setVisibility(View.VISIBLE);
    }

    private void hideUserDetails() {
        sEditMode.setVisibility(View.GONE);
        layoutEmail.setVisibility(View.GONE);
        layoutFullName.setVisibility(View.GONE);
        layoutIdCardSeries.setVisibility(View.GONE);
        layoutIdCardNumber.setVisibility(View.GONE);
        layoutDob.setVisibility(View.GONE);
        layoutPhoneNumber.setVisibility(View.GONE);
        bSaveEditChanges.setVisibility(View.GONE);
    }

    private void deleteUser(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to remove this user?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    // verify if deletion is successful or not
                    databaseReference
                            .child("users")
                            .child(user.getUid())
                            .removeValue();
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

    private void viewActiveLoans(View view) {
        Intent intent = new Intent(this, ActiveLoansActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
    private void viewAllLoans(View view) {
        Intent intent = new Intent(this, AllLoansActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void viewReservations(View view) {
        Intent intent = new Intent(this, ReservationsActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void bSaveEditChanges(View view) {
        String fullName = eFullName.getText().toString();
        String idCardSeries = eIdCardSeries.getText().toString();
        String idCardNumber = eIdCardNumber.getText().toString();
        String dob = eDob.getText().toString();
        String phoneNumber = ePhoneNumber.getText().toString();

        if (areUserDetailsValid()) {
            setEditableState(false);
            Map<String, Object> childUpdates =  new HashMap<>();
            childUpdates.put("users/" + user.getUid() + "/fullName", fullName);
            childUpdates.put("users/" + user.getUid() + "/idCardSeries", idCardSeries);
            childUpdates.put("users/" + user.getUid() + "/idCardNumber", idCardNumber);
            childUpdates.put("users/" + user.getUid() + "/dateOfBirthday", dob);
            childUpdates.put("users/" + user.getUid() + "/phoneNumber", phoneNumber);
            databaseReference.updateChildren(childUpdates);
            Snackbar.make(view, "User has been updated successfully", Snackbar.LENGTH_SHORT).show();
        }
    }

    private boolean areUserDetailsValid() {
        String email = eEmail.getText().toString();
        String fullName = eFullName.getText().toString();
        String idCardSeries = eIdCardSeries.getText().toString();
        String idCardNumber = eIdCardNumber.getText().toString();
        String dob = eDob.getText().toString();
        String phoneNumber = ePhoneNumber.getText().toString();

        boolean isErrorEmail = true;
        boolean isErrorFullName = true;
        boolean isErrorIdCardSeries = true;
        boolean isErrorIdCardNumber = true;
        boolean isErrorDob = true;
        boolean isErrorPhoneNumber = true;
        int STANDARD_MAX_LIMIT = 50;
        int IC_NUMBER_LENGTH = 6;

        if (email.isEmpty()) {
            layoutEmail.setError(getString(R.string.empty_field));
        } else if (email.length() > STANDARD_MAX_LIMIT) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
            layoutEmail.setError(fieldMaxLimitMessage);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError(getString(R.string.email_not_valid));
        } else {
            isErrorEmail = false;
            layoutEmail.setError(null);
        }

        if (fullName.isEmpty()) {
            layoutFullName.setError(getString(R.string.empty_field));
        } else if (fullName.length() > STANDARD_MAX_LIMIT) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
            layoutFullName.setError(fieldMaxLimitMessage);
        } else {
            isErrorFullName = false;
            layoutFullName.setError(null);
        }
        if (idCardSeries.isEmpty()) {
            layoutIdCardSeries.setError(getString(R.string.empty_field));
        } else if (!Arrays.stream(County.class.getEnumConstants()).anyMatch(c -> c.name().equals(idCardSeries))) {
            layoutIdCardSeries.setError(getString(R.string.ic_series_not_valid));
        } else {
            isErrorIdCardSeries = false;
            layoutIdCardSeries.setError(null);
        }

        if (idCardNumber.isEmpty()) {
            layoutIdCardNumber.setError(getString(R.string.empty_field));
        } else if (idCardNumber.length() != IC_NUMBER_LENGTH || !idCardNumber.chars().allMatch(Character::isDigit)) {
            layoutIdCardNumber.setError(getString(R.string.ic_number_not_valid));
        } else {
            isErrorIdCardNumber = false;
            layoutIdCardNumber.setError(null);
        }

        if (dob.isEmpty()) {
            layoutDob.setError(getString(R.string.empty_field));
        } else {
            isErrorDob = false;
            layoutDob.setError(null);
        }

        if (phoneNumber.isEmpty()) {
            layoutPhoneNumber.setError(getString(R.string.empty_field));
        } else if (phoneNumber.length() > STANDARD_MAX_LIMIT) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
            layoutPhoneNumber.setError(fieldMaxLimitMessage);
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            layoutPhoneNumber.setError(getString(R.string.phone_number_not_valid));
        } else {
            isErrorPhoneNumber = false;
            layoutPhoneNumber.setError(null);
        }

        if (isErrorEmail || isErrorFullName || isErrorIdCardSeries || isErrorIdCardNumber || isErrorDob || isErrorPhoneNumber) {
            return false;
        }

        return true;
    }
}