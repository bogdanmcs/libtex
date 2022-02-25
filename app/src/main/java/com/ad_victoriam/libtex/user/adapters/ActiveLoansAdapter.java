package com.ad_victoriam.libtex.user.adapters;

import android.os.Bundle;
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
import com.ad_victoriam.libtex.common.models.User;
import com.ad_victoriam.libtex.user.fragments.books.BookDetailsFragment;
import com.ad_victoriam.libtex.user.models.Loan;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class ActiveLoansAdapter extends RecyclerView.Adapter<ActiveLoansAdapter.LoanViewHolder> {

    private final FragmentActivity activity;
    private final List<Loan> loans;
    private User user;

    private static final String LOAN_TYPE_ACTIVE = "active";

    public ActiveLoansAdapter(FragmentActivity activity, List<Loan> loans) {
        this.activity = activity;
        this.loans = loans;
    }

    public ActiveLoansAdapter(FragmentActivity activity, List<Loan> loans, User user) {
        this.activity = activity;
        this.loans = loans;
        this.user = user;
    }

    @NonNull
    @Override
    public ActiveLoansAdapter.LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_active_loan, parent, false);
        return new ActiveLoansAdapter.LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveLoansAdapter.LoanViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewBookDetails(view, position));
        holder.tBookTitle.setText(loans.get(position).getBook().getTitle());
        holder.tBookAuthorName.setText(loans.get(position).getBook().getAuthorName());
        holder.tBookPublisher.setText(loans.get(position).getBook().getPublisher());

        DateFormat Date = DateFormat.getDateInstance();
        Calendar calendar = Calendar.getInstance();

        String deadlineText = loans.get(position).getDeadlineTimestamp();
        LocalDateTime deadlineDateTime = LocalDateTime.parse(deadlineText);
        calendar.set(deadlineDateTime.getYear(), deadlineDateTime.getMonthValue() - 1, deadlineDateTime.getDayOfMonth());
        String deadlineTextFormatted = Date.format(calendar.getTime());
        holder.tDeadline.setText(deadlineTextFormatted);

        String loanedOnText = loans.get(position).getLoanTimestamp();
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
    }

    private void viewBookDetails(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("book", loans.get(position).getBook());

        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        bookDetailsFragment.setArguments(bundle);

        activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, bookDetailsFragment)
                .addToBackStack("activeLoansFragment")
                .commit();
    }

    @Override
    public int getItemCount() {
        if (loans != null) {
            return loans.size();
        } else {
            return 0;
        }
    }

    public static class LoanViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        CardView cardView;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;
        TextView tDeadline;
        TextView tLoanedOn;
        MaterialButton bReturnBook;

        public LoanViewHolder(@NonNull View itemView) {
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
}
