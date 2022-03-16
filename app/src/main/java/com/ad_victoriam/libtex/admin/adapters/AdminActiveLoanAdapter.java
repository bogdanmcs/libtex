package com.ad_victoriam.libtex.admin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.books.AdminBookDetailsActivity;
import com.ad_victoriam.libtex.admin.models.AdminLoan;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.common.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class AdminActiveLoanAdapter extends RecyclerView.Adapter<AdminActiveLoanAdapter.BookLoanViewHolder> {

    private final FragmentActivity activity;
    private final List<AdminLoan> adminLoans;
    private User user;

    public AdminActiveLoanAdapter(FragmentActivity activity, List<AdminLoan> adminLoans) {
        this.activity = activity;
        this.adminLoans = adminLoans;
    }

    public AdminActiveLoanAdapter(FragmentActivity activity, List<AdminLoan> adminLoans, User user) {
        this.activity = activity;
        this.adminLoans = adminLoans;
        this.user = user;
    }

    @NonNull
    @Override
    public AdminActiveLoanAdapter.BookLoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_active_loan, parent, false);
        return new AdminActiveLoanAdapter.BookLoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminActiveLoanAdapter.BookLoanViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewLoanDetails(view, position));
        holder.tBookTitle.setText(adminLoans.get(position).getBook().getTitle());
        holder.tBookAuthorName.setText(adminLoans.get(position).getBook().getAuthorName());
        holder.tBookPublisher.setText(adminLoans.get(position).getBook().getPublisher());

        DateFormat Date = DateFormat.getDateInstance();
        Calendar calendar = Calendar.getInstance();

        String deadlineText = adminLoans.get(position).getDeadlineTimestamp();
        LocalDateTime deadlineDateTime = LocalDateTime.parse(deadlineText);
        calendar.set(deadlineDateTime.getYear(), deadlineDateTime.getMonthValue() - 1, deadlineDateTime.getDayOfMonth());
        String deadlineTextFormatted = Date.format(calendar.getTime());
        holder.tDeadline.setText(deadlineTextFormatted);

        String loanedOnText = adminLoans.get(position).getLoanTimestamp();
        LocalDateTime loanedOnDateTime = LocalDateTime.parse(loanedOnText);
        calendar.set(loanedOnDateTime.getYear(), loanedOnDateTime.getMonthValue() - 1, loanedOnDateTime.getDayOfMonth());
        String loanedOnTextFormatted = Date.format(calendar.getTime());
        holder.tLoanedOn.setText(loanedOnTextFormatted);

        if (deadlineDateTime.isBefore(LocalDateTime.now())) {
            int color = activity.getResources().getColor(R.color.indian_red, activity.getTheme());
            holder.cardView.setCardBackgroundColor(color);
        } else if (deadlineDateTime.minusDays(3).isBefore(LocalDateTime.now())) {
            int color = activity.getResources().getColor(R.color.yellow, activity.getTheme());
            holder.cardView.setCardBackgroundColor(color);
        } else {
            int color = activity.getResources().getColor(R.color.light_green, activity.getTheme());
            holder.cardView.setCardBackgroundColor(color);
        }
        holder.bReturnBook.setOnClickListener(view -> confirmReturn(view, position));
    }

    @Override
    public int getItemCount() {
        if (adminLoans != null) {
            return adminLoans.size();
        } else {
            return 0;
        }
    }

    public class BookLoanViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        CardView cardView;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;
        TextView tDeadline;
        TextView tLoanedOn;
        MaterialButton bReturnBook;

        public BookLoanViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            cardView = itemView.findViewById(R.id.cardView);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
            tDeadline = itemView.findViewById(R.id.tDeadline);
            tLoanedOn = itemView.findViewById(R.id.tLoanedOn);
            bReturnBook = itemView.findViewById(R.id.bReturnBook);
        }
    }

    private void confirmReturn(View view, int position) {
        new AlertDialog.Builder(activity)
                .setMessage(
                        "Are you sure you want to return " +
                                adminLoans.get(position).getBook().getTitle() + " - " +
                                adminLoans.get(position).getBook().getAuthorName() + " " +
                                "by user " +
                                user.getFullName() + " ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    returnBook(view, position);
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }

    private void returnBook(View view, int position) {
        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // increase available quantity of the loaned book
        databaseReference
                .child("books")
                .child(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot: task.getResult().getChildren()) {
                                AdminBook adminBook = dataSnapshot.getValue(AdminBook.class);

                                if (adminBook != null) {
                                    adminBook.setUid(dataSnapshot.getKey());

                                    if (adminBook.getUid().equals(adminLoans.get(position).getBookUid())) {
                                        // found the book, increase AQ by 1
                                        databaseReference
                                                .child("books")
                                                .child(currentUser.getUid())
                                                .child(adminBook.getUid())
                                                .child("availableQuantity")
                                                .setValue(adminBook.getAvailableQuantity() + 1);
                                        break;
                                    }
                                }
                            }
                        } else {
                            System.out.println(task.getResult().getValue());
                        }
                    }
                });
        // remove current book loan from this user
        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("current-loans")
                .child(currentUser.getUid())
                .child(adminLoans.get(position).getBookLoanUid())
                .removeValue();
        // update book loan in loans history - return date
        LocalDateTime returnTimestamp = LocalDateTime.now();
        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("loans-history")
                .child(currentUser.getUid())
                .child(adminLoans.get(position).getBookLoanUid())
                .child("returnTimestamp")
                .setValue(returnTimestamp.toString());

        Snackbar.make(activity, view, "Operation was successful", Snackbar.LENGTH_SHORT).show();
    }

    private void viewLoanDetails(View view, int position) {
        Intent intent = new Intent(activity, AdminBookDetailsActivity.class);
        intent.putExtra("book", adminLoans.get(position).getBook());
        activity.startActivity(intent);
    }
}
