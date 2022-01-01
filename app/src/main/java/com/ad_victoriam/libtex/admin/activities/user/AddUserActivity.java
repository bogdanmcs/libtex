package com.ad_victoriam.libtex.admin.activities.user;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
            completeAccountSetup(user);
            Toast.makeText(getApplicationContext(), "User added successfully", Toast.LENGTH_SHORT).show();
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

        String email = eEmail.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eEmail.setError("Email address is not valid");
            eEmail.requestFocus();
            errorFlag = true;
        }

        return !errorFlag;
    }

    private void completeAccountSetup(User updatedUser) {
        // read list of users !once
        databaseReference.child("unverified-users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && user.getEmail().equals(updatedUser.getEmail())) {
                            String userUid = dataSnapshot.getKey();
                            // move to verified users
                            // verify completion
                            databaseReference.child("users").child(userUid).setValue(updatedUser);
                            databaseReference.child("unverified-users").child(userUid).removeValue();
                            break;
                        }

                    }
                } else {
                    System.out.println(task.getResult().getValue());
                }
            }
        });
    }
}
