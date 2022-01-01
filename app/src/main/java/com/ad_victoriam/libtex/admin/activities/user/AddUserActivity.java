package com.ad_victoriam.libtex.admin.activities.user;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddUserActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    TextInputEditText eEmail;
    TextInputEditText eFirstName;
    TextInputEditText eLastName;
    TextInputEditText eIDCardSerialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = database.getReference();

        eEmail = findViewById(R.id.eEmail);
        eFirstName = findViewById(R.id.eFirstName);
        eLastName = findViewById(R.id.eLastName);
        eIDCardSerialNumber = findViewById(R.id.eIdCardSerialNumber);

        final Button bAddUser = findViewById(R.id.bConfirmAddition);

        bAddUser.setOnClickListener(this::bAddUser);
    }

    private void bAddUser(View view) {

        String userEmail = eEmail.getText().toString();
        String userFirstName = eFirstName.getText().toString();
        String userLastName = eLastName.getText().toString();
        String userIdCardSerialNumber = eIDCardSerialNumber.getText().toString();

        if (areUserDetailsValid()) {
            User user = new User(userEmail, userFirstName, userLastName, userIdCardSerialNumber);
            databaseReference.child("users").push().setValue(user);
            Toast.makeText(getApplicationContext(), "User added successfully.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean areUserDetailsValid() {
        boolean errorFlag = false;

        String idCardSerialNumber = eIDCardSerialNumber.getText().toString();
        if (idCardSerialNumber.isEmpty()) {
            eIDCardSerialNumber.setError("Please fill this field");
            eIDCardSerialNumber.requestFocus();
            errorFlag = true;
        } else if (idCardSerialNumber.length() > 8) {
            eIDCardSerialNumber.setError("Maximum length is 8 characters");
            eIDCardSerialNumber.requestFocus();
            errorFlag = true;
        }

        String email = eEmail.getText().toString();
        if (email.isEmpty()) {
            eEmail.setError("Please fill this field");
            eEmail.requestFocus();
            errorFlag = true;
        } else if (email.length() > 8) {
            eEmail.setError("Maximum length is 8 characters");
            eEmail.requestFocus();
            errorFlag = true;
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eEmail.setError("Email address is not valid");
            eEmail.requestFocus();
            errorFlag = true;
        }

        List<TextInputEditText> userDetails = new ArrayList<>();
        userDetails.add(eLastName);
        userDetails.add(eFirstName);

        for (TextInputEditText ud: userDetails) {
            String textToString = ud.getText().toString();
            if (textToString.isEmpty()) {
                ud.setError("Please fill this field");
                ud.requestFocus();
                errorFlag = true;
            } else if (textToString.length() > 50) {
                ud.setError("Maximum length is 50 characters");
                ud.requestFocus();
                errorFlag = true;
            }
        }

        return !errorFlag;
    }
}
