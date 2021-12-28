package com.ad_victoriam.libtex.admin.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.AdminHomeActivity;
import com.ad_victoriam.libtex.admin.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText editTextLastName;
    private EditText editTextFirstName;
    private EditText editTextIdCardSeries;
    private EditText editTextEmail;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register_customer);

        editTextLastName = findViewById(R.id.editTextLastName);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextIdCardSeries = findViewById(R.id.editTextIdCardSeries);
        editTextEmail = findViewById(R.id.editTextEmail);

        databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    }

    private void registerUser(String lastName, String firstName, String idCardSeries, String email) {
        User user = new User(lastName, firstName, idCardSeries, email);
//        databaseReference.child("users").child();
        // put in db
        // if it has email, use unique id, if not, use idCardSeries

        //
        // return to admin home
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bRegisterUser:
                String lastName = editTextLastName.getText().toString();
                String firstName = editTextFirstName.getText().toString();
                String idCardSeries = editTextIdCardSeries.getText().toString();
                String email = editTextEmail.getText().toString();

                if (lastName.isEmpty()) {
                    editTextLastName.setError("cannot be empty");
                    return;
                }

                if (firstName.isEmpty()) {
                    editTextFirstName.setError("cannot be empty");
                    return;
                }

                if (idCardSeries.isEmpty()) {
                    editTextIdCardSeries.setError("cannot be empty");
                    return;
                }

                registerUser(lastName, firstName, idCardSeries, email);
                break;

            default:
        }
    }
}
