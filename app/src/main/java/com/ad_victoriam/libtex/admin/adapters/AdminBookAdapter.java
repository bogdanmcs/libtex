package com.ad_victoriam.libtex.admin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.admin.activities.books.AdminBookDetailsActivity;
import com.ad_victoriam.libtex.admin.models.AdminBook;
import com.ad_victoriam.libtex.admin.models.AdminLoan;
import com.ad_victoriam.libtex.common.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.BookViewHolder> {

    private final Context context;
    private final List<AdminBook> adminBooks;

    private final String intentAction;
    private User user;

    public AdminBookAdapter(Context context, List<AdminBook> adminBooks, String intentAction) {
        this.context = context;
        this.adminBooks = adminBooks;
        this.intentAction = intentAction;
    }

    public AdminBookAdapter(Context context, List<AdminBook> adminBooks, String intentAction, User user) {
        this.context = context;
        this.adminBooks = adminBooks;
        this.intentAction = intentAction;
        this.user = user;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> clicked(view, position));
        holder.tBookTitle.setText(adminBooks.get(position).getTitle());
        String text = "by " + adminBooks.get(position).getAuthorName();
        holder.tBookAuthorName.setText(text);
        holder.tBookPublisher.setText(adminBooks.get(position).getPublisher());

        if (position == adminBooks.size() - 1) {
            holder.div.setVisibility(View.INVISIBLE);
        }
    }

    private void clicked(View view, int position) {
        if (!intentAction.equals("BORROW")) {
            Intent intent = new Intent(context, AdminBookDetailsActivity.class);
            intent.putExtra("book", adminBooks.get(position));
            context.startActivity(intent);
        } else {
            // assign book to current user
            confirmLoan(view, position);
        }
    }

    @Override
    public int getItemCount() {
        return adminBooks.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;
        View div;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
            div = itemView.findViewById(R.id.div);
        }
    }

    private void confirmLoan(View view, int position) {
        new AlertDialog.Builder(context)
                .setMessage(
                        "Are you sure you want to assign this book to " +
                                user.getFullName() + " ?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    assignBookToUser(view, position);
                })
                .setNegativeButton("No", (dialogInterface, i) -> {})
                .show();
    }

    private void assignBookToUser(View view, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://libtex-a007e-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        AdminBook adminBook = adminBooks.get(position);
        databaseReference
                .child("books")
                .child(currentUser.getUid())
                .child(adminBook.getUid())
                .child("availableQuantity")
                .setValue(adminBook.getAvailableQuantity() - 1);

        AdminLoan adminLoan = new AdminLoan(adminBook.getUid());
        String bookLoanKey = databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .push()
                .getKey();

        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("current-loans")
                .child(currentUser.getUid())
                .child(bookLoanKey)
                .setValue(adminLoan);

        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("loans-history")
                .child(currentUser.getUid())
                .child(bookLoanKey)
                .setValue(adminLoan);

        Snackbar.make(view, "Book assigned successfully", Snackbar.LENGTH_SHORT).show();
    }
}
