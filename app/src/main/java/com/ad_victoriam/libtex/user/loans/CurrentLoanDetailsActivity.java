package com.ad_victoriam.libtex.user.loans;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.book.BookLoan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CurrentLoanDetailsActivity extends AppCompatActivity {

    private BookLoan bookLoan;

    private TextView tBookTitle;
    private TextView tBookAuthorName;
    private TextView tBookPublisher;
    private TextView tTimestamp;
    private TextView tDeadline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_current_loan_details);

        tBookTitle = findViewById(R.id.tBookTitle);
        tBookAuthorName = findViewById(R.id.tBookAuthorName);
        tBookPublisher = findViewById(R.id.tBookPublisher);
        tTimestamp = findViewById(R.id.tTimestamp);
        tDeadline = findViewById(R.id.tDeadline);

        if (getIntent().hasExtra("bookLoan")) {
            bookLoan = getIntent().getParcelableExtra("bookLoan");

            tBookTitle.setText(bookLoan.getBook().getTitle());
            tBookAuthorName.setText(bookLoan.getBook().getAuthorName());
            tBookPublisher.setText(bookLoan.getBook().getPublisher());
            tTimestamp.setText(bookLoan.getLoanTimestamp());
            tDeadline.setText(bookLoan.getDeadlineTimestamp());
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }

        final Button bViewLocation = findViewById(R.id.bViewLocation);
        bViewLocation.setOnClickListener(this::viewLocation);

    }

    private void viewLocation(View view) {
        String libraryUid = bookLoan.getLibraryUid();

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();

        databaseReference
                .child("admins")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {

                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                                    Double latitude = dataSnapshot1.child("latitude").getValue(Double.class);
                                    Double longitude = dataSnapshot1.child("longitude").getValue(Double.class);

                                    if (latitude != null && longitude != null &&
                                            libraryUid.equals(dataSnapshot.getKey())) {

                                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                        intent.putExtra("libraryLatitude", latitude);
                                        intent.putExtra("libraryLongitude", longitude);
                                        startActivity(intent);
                                        return;
                                    }
                                }
                            }
                        } else {
                            System.out.println(task.getResult().getValue());
                        }
                    }
                });
    }


}
