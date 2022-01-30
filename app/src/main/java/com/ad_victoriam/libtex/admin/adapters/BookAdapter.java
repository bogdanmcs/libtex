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
import com.ad_victoriam.libtex.admin.activities.BookDetailsActivity;
import com.ad_victoriam.libtex.common.models.BookLoan;
import com.ad_victoriam.libtex.common.models.Book;
import com.ad_victoriam.libtex.common.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final Context context;
    private final List<Book> books;

    private final String intentAction;
    private User user;

    public BookAdapter(Context context, List<Book> books, String intentAction) {
        this.context = context;
        this.books = books;
        this.intentAction = intentAction;
    }

    public BookAdapter(Context context, List<Book> books, String intentAction, User user) {
        this.context = context;
        this.books = books;
        this.intentAction = intentAction;
        this.user = user;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_adapter_row, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> clicked(view, position));
        holder.tBookTitle.setText(books.get(position).getTitle());
        holder.tBookAuthorName.setText(books.get(position).getAuthorName());
        holder.tBookPublisher.setText(books.get(position).getPublisher());
    }

    private void clicked(View view, int position) {
        if (!intentAction.equals("BORROW")) {
            Intent intent = new Intent(context, BookDetailsActivity.class);
            intent.putExtra("book", books.get(position));
            context.startActivity(intent);
        } else {
            // assign book to current user
            confirmLoan(view, position);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
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

        Book book = books.get(position);
        databaseReference
                .child("books")
                .child(currentUser.getUid())
                .child(book.getUid())
                .child("availableQuantity")
                .setValue(book.getAvailableQuantity() - 1);

        BookLoan bookLoan = new BookLoan(book.getUid());
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
                .setValue(bookLoan);

        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("loans-history")
                .child(currentUser.getUid())
                .child(bookLoanKey)
                .setValue(bookLoan);

        Snackbar.make(view, "Book assigned successfully", Snackbar.LENGTH_SHORT).show();
    }
}
