package com.ad_victoriam.libtex.admin.activities.users;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.AdminHomeActivity;
import com.ad_victoriam.libtex.admin.fragments.DatePickerFragment;
import com.ad_victoriam.libtex.admin.utils.County;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.admin.utils.TopAppBarAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;

public class AddUserActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    DatabaseReference databaseReference;

    private TextInputEditText eEmail;
    private TextInputEditText eFullName;
    private TextInputEditText eIdCardSeries;
    private TextInputEditText eIdCardNumber;
    private TextInputEditText eDob;
    private LocalDate dobLocalDate;
    private TextInputEditText ePhoneNumber;
    private TextInputLayout layoutEmail;
    private TextInputLayout layoutFullName;
    private TextInputLayout layoutIdCardSeries;
    private TextInputLayout layoutIdCardNumber;
    private TextInputLayout layoutDob;
    private TextInputLayout layoutPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarAdmin.get().setChildMode(this, topAppBar);
        TopAppBarAdmin.get().setTitleMode(this, topAppBar, "Configure new user");
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

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        findViews();

        final Button bAddUser = findViewById(R.id.bConfirmAddition);
        bAddUser.setOnClickListener(this::bAddUser);
    }

    private void findViews() {
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
    }

    private void pickDate(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    private void bAddUser(View view) {
        String email = eEmail.getText().toString();
        String fullName = eFullName.getText().toString();
        String idCardSeries = eIdCardSeries.getText().toString();
        String idCardNumber = eIdCardNumber.getText().toString();
        String dob = eDob.getText().toString();
        String phoneNumber = ePhoneNumber.getText().toString();

        if (areUserDetailsValid()) {
            User user = new User(email, fullName, idCardSeries, idCardNumber, dob, phoneNumber);
            completeAccountSetup(user);
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

    private void completeAccountSetup(User updatedUser) {
        databaseReference
                .child("unverified-users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && user.getEmail().equals(updatedUser.getEmail())) {
                            String userUid = dataSnapshot.getKey();
                            // move to verified users
                            databaseReference.child("unverified-users").child(userUid).removeValue();
                            databaseReference.child("users").child(userUid).setValue(updatedUser);
                            Toast.makeText(getApplicationContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                    }
                    layoutEmail.setError(getString(R.string.email_not_used));
                } else {
                    System.out.println(task.getResult().getValue());
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        try {
            String date = DateFormat.getDateInstance().format(calendar.getTime());
            dobLocalDate = LocalDate.of(year, month + 1, day);
            eDob.setText(date);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
        }
    }
}