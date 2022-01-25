package com.ad_victoriam.libtex.admin.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.fragments.DatePickerFragment;
import com.ad_victoriam.libtex.admin.utils.County;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.common.utils.TopAppBarState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;

public class AddUserActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    DatabaseReference databaseReference;

    TextInputEditText eEmail;
    TextInputEditText eFullName;
    TextInputEditText eIdCardSeries;
    TextInputEditText eIdCardNumber;
    TextInputEditText eDob;
    LocalDate dobLocalDate;
    TextInputEditText ePhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TopAppBarState.get().setChildMode(this, topAppBar);
        TopAppBarState.get().setTitleMode(this, topAppBar, "Configure new user");
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

        eEmail = findViewById(R.id.eEmail);
        eFullName = findViewById(R.id.eFullName);
        eIdCardSeries = findViewById(R.id.eIdCardSeries);
        eIdCardNumber = findViewById(R.id.eIdCardNumber);
        eDob = findViewById(R.id.eDob);
        eDob.setOnClickListener(this::pickDate);
        eDob.setFocusable(false);
        ePhoneNumber = findViewById(R.id.ePhoneNumber);

        final Button bAddUser = findViewById(R.id.bConfirmAddition);
        bAddUser.setOnClickListener(this::bAddUser);
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

        TextView tEmailHelper = findViewById(R.id.tEmailHelper);
        TextView tFullNameHelper = findViewById(R.id.tFullNameHelper);
        TextView tIdCardSeriesHelper = findViewById(R.id.tIdCardSeriesHelper);
        TextView tIdCardNumberHelper = findViewById(R.id.tIdCardNumberHelper);
        TextView tDobHelper = findViewById(R.id.tDobHelper);
        TextView tPhoneNumberHelper = findViewById(R.id.tPhoneNumberHelper);
        tEmailHelper.setText("");
        tFullNameHelper.setText("");
        tIdCardSeriesHelper.setText("");
        tIdCardNumberHelper.setText("");
        tDobHelper.setText("");
        tPhoneNumberHelper.setText("");

        boolean isErrorEmail = true;
        boolean isErrorFullName = true;
        boolean isErrorIdCardSeries = true;
        boolean isErrorIdCardNumber = true;
        boolean isErrorDob = true;
        boolean isErrorPhoneNumber = true;
        int STANDARD_MAX_LIMIT = 50;
        int IC_NUMBER_LENGTH = 6;

        if (email.isEmpty()) {
            tEmailHelper.setText(R.string.empty_field);
        } else if (email.length() > STANDARD_MAX_LIMIT) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
            tEmailHelper.setText(fieldMaxLimitMessage);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tEmailHelper.setText(R.string.email_not_valid);
        } else {
            isErrorEmail = false;
        }

        if (fullName.isEmpty()) {
            tFullNameHelper.setText(R.string.empty_field);
        } else if (fullName.length() > STANDARD_MAX_LIMIT) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
            tFullNameHelper.setText(fieldMaxLimitMessage);
        } else {
            isErrorFullName = false;
        }
        if (idCardSeries.isEmpty()) {
            tIdCardSeriesHelper.setText(R.string.empty_field);
        } else if (!Arrays.stream(County.class.getEnumConstants()).anyMatch(c -> c.name().equals(idCardSeries))) {
            tIdCardSeriesHelper.setText(R.string.ic_series_not_valid);
        } else {
            isErrorIdCardSeries = false;
        }

        if (idCardNumber.isEmpty()) {
            tIdCardNumberHelper.setText(R.string.empty_field);
        } else if (idCardNumber.length() != IC_NUMBER_LENGTH || !idCardNumber.chars().allMatch(Character::isDigit)) {
            tIdCardNumberHelper.setText(R.string.ic_number_not_valid);
        } else {
            isErrorIdCardNumber = false;
        }

        if (dob.isEmpty()) {
            tDobHelper.setText(R.string.empty_field);
        } else {
            isErrorDob = false;
        }

        if (phoneNumber.isEmpty()) {
            tPhoneNumberHelper.setText(R.string.empty_field);
        } else if (phoneNumber.length() > STANDARD_MAX_LIMIT) {
            String fieldMaxLimitMessage = getString(R.string.field_max_limit) + " " + STANDARD_MAX_LIMIT;
            tPhoneNumberHelper.setText(fieldMaxLimitMessage);
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            tPhoneNumberHelper.setText(R.string.phone_number_not_valid);
        } else {
            isErrorPhoneNumber = false;
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
                    TextView tEmailHelper = findViewById(R.id.tEmailHelper);
                    tEmailHelper.setText(R.string.email_not_used);
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