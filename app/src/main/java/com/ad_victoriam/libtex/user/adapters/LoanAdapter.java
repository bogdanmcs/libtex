package com.ad_victoriam.libtex.user.adapters;

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
import com.ad_victoriam.libtex.common.models.BookLoan;
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.user.activities.CurrentLoanDetailsActivity;

import java.time.LocalDateTime;
import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {

    private final Context context;
    private final List<BookLoan> bookLoans;
    private User user;
    private String fragmentType;

    public LoanAdapter(Context context, List<BookLoan> bookLoans) {
        this.context = context;
        this.bookLoans = bookLoans;
    }

    public LoanAdapter(Context context, List<BookLoan> bookLoans, User user) {
        this.context = context;
        this.bookLoans = bookLoans;
        this.user = user;
    }

    public LoanAdapter(Context context, List<BookLoan> bookLoans, String fragmentType) {
        this.context = context;
        this.bookLoans = bookLoans;
        this.fragmentType = fragmentType;
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
        String deadlineText = bookLoans.get(position).getDeadlineTimestamp();
        holder.tDeadline.setText(deadlineText);
        // get book loan deadline and format time
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

            if (fragmentType.equals("ALL")) {
                tDeadline.setVisibility(View.GONE);
                TextView deadlineTextView = itemView.findViewById(R.id.deadlineTextView);
                deadlineTextView.setVisibility(View.GONE);
            }
        }
    }

    private void viewBookLoan(View view, int position) {
        Intent intent = new Intent(context, CurrentLoanDetailsActivity.class);
        intent.putExtra("bookLoan", bookLoans.get(position));
        context.startActivity(intent);
    }
}
