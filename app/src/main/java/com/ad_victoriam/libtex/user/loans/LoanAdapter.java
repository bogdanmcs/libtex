package com.ad_victoriam.libtex.user.loans;

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
import com.ad_victoriam.libtex.admin.activities.book.BookLoan;
import com.ad_victoriam.libtex.model.User;

import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {

    private final Context context;
    private final List<BookLoan> bookLoans;
    private User user;

    public LoanAdapter(Context context, List<BookLoan> bookLoans) {
        this.context = context;
        this.bookLoans = bookLoans;
    }

    public LoanAdapter(Context context, List<BookLoan> bookLoans, User user) {
        this.context = context;
        this.bookLoans = bookLoans;
        this.user = user;
    }

    @NonNull
    @Override
    public LoanAdapter.LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_loan_adapter_row, parent, false);
        return new LoanAdapter.LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanAdapter.LoanViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewBookLoan(view, position));
        holder.tBookTitle.setText(bookLoans.get(position).getBook().getTitle());
        holder.tBookAuthorName.setText(bookLoans.get(position).getBook().getAuthorName());
        holder.tDeadline.setText(bookLoans.get(position).getDeadline());
    }

    @Override
    public int getItemCount() {
        return bookLoans.size();
    }

    public class LoanViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tDeadline;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tDeadline = itemView.findViewById(R.id.tDeadline);
        }
    }

    private void viewBookLoan(View view, int position) {
        Intent intent = new Intent(context, CurrentLoanDetailsActivity.class);
        intent.putExtra("bookLoan", bookLoans.get(position));
        context.startActivity(intent);
    }
}
