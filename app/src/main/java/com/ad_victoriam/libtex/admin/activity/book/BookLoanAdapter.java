package com.ad_victoriam.libtex.admin.activity.book;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.model.Book;
import com.ad_victoriam.libtex.common.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.List;

public class BookLoanAdapter extends RecyclerView.Adapter<BookLoanAdapter.BookLoanViewHolder> {

    private final Context context;
    private final List<BookLoan> bookLoans;
    private User user;

    public BookLoanAdapter(Context context, List<BookLoan> bookLoans) {
        this.context = context;
        this.bookLoans = bookLoans;
    }

    public BookLoanAdapter(Context context, List<BookLoan> bookLoans, User user) {
        this.context = context;
        this.bookLoans = bookLoans;
        this.user = user;
    }

    @NonNull
    @Override
    public BookLoanAdapter.BookLoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_loan_adapter_row, parent, false);
        return new BookLoanAdapter.BookLoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookLoanAdapter.BookLoanViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> confirmReturn(view, position));
        holder.tBookTitle.setText(bookLoans.get(position).getBook().getTitle());
        holder.tBookAuthorName.setText(bookLoans.get(position).getBook().getAuthorName());
        String deadlineText = bookLoans.get(position).getDeadlineTimestamp();
        holder.tDeadline.setText(deadlineText);
        LocalDateTime deadline = LocalDateTime.parse(deadlineText);
        if (deadline.isBefore(LocalDateTime.now())) {
            holder.tDeadline.setBackgroundResource(R.drawable.deadline_exceeded);
        } else if (deadline.minusDays(3).isBefore(LocalDateTime.now())) {
            holder.tDeadline.setBackgroundResource(R.drawable.deadline_close);
        } else {
            holder.tDeadline.setBackgroundResource(R.drawable.deadline_not_exceeded);
        }
    }

    @Override
    public int getItemCount() {
//        return bookLoans.size();
        if (bookLoans != null) {
            return bookLoans.size();
        } else {
            return 0;
        }
    }

    public class BookLoanViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tDeadline;

        public BookLoanViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tDeadline = itemView.findViewById(R.id.tDeadline);
        }
    }

    private void confirmReturn(View view, int position) {
        new AlertDialog.Builder(context)
                .setMessage(
                        "Are you sure you want to confirm the return of " +
                                bookLoans.get(position).getBook().getTitle() + " by " +
                                bookLoans.get(position).getBook().getAuthorName() + " (" +
                                bookLoans.get(position).getBook().getPublisher() + ") " +
                                "by user " +
                                user.getFirstName() + " " +
                                user.getLastName() + " ?")
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
                                Book book = dataSnapshot.getValue(Book.class);

                                if (book != null) {
                                    book.setUid(dataSnapshot.getKey());

                                    if (book.getUid().equals(bookLoans.get(position).getBookUid())) {
                                        // found the book, increase AQ by 1
                                        databaseReference
                                                .child("books")
                                                .child(currentUser.getUid())
                                                .child(book.getUid())
                                                .child("availableQuantity")
                                                .setValue(book.getAvailableQuantity() + 1);
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
                .child(bookLoans.get(position).getBookLoanUid())
                .removeValue();
        // update book loan in loans history - return date
        LocalDateTime returnTimestamp = LocalDateTime.now();
        databaseReference
                .child("users")
                .child(user.getUid())
                .child("book-loans")
                .child("loans-history")
                .child(currentUser.getUid())
                .child(bookLoans.get(position).getBookLoanUid())
                .child("returnTimestamp")
                .setValue(returnTimestamp.toString());

        Snackbar.make(context, view, "Operation was successful", Snackbar.LENGTH_SHORT).show();
    }
}
