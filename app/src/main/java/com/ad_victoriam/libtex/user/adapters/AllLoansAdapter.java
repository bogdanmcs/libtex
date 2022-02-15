package com.ad_victoriam.libtex.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.common.models.BookLoan;
import com.ad_victoriam.libtex.common.models.User;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class AllLoansAdapter extends RecyclerView.Adapter<AllLoansAdapter.LoanViewHolder> {

    private final Context context;
    private final List<BookLoan> loans;
    private User user;

    public AllLoansAdapter(Context context, List<BookLoan> loans) {
        this.context = context;
        this.loans = loans;
    }

    public AllLoansAdapter(Context context, List<BookLoan> loans, User user) {
        this.context = context;
        this.loans = loans;
        this.user = user;
    }

    @NonNull
    @Override
    public AllLoansAdapter.LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_loan, parent, false);
        return new AllLoansAdapter.LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllLoansAdapter.LoanViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewLoanDetails(view, position));
        holder.tBookTitle.setText(loans.get(position).getBook().getTitle());
        holder.tBookAuthorName.setText(loans.get(position).getBook().getAuthorName());
        holder.tBookPublisher.setText(loans.get(position).getBook().getPublisher());

        DateFormat Date = DateFormat.getDateInstance();
        Calendar calendar = Calendar.getInstance();

        String loanedOnText = loans.get(position).getLoanTimestamp();
        LocalDateTime loanedOnDateTime = LocalDateTime.parse(loanedOnText);
        calendar.set(loanedOnDateTime.getYear(), loanedOnDateTime.getMonthValue() - 1, loanedOnDateTime.getDayOfMonth());
        String loanedOnTextFormatted = Date.format(calendar.getTime());
        holder.tLoanedOn.setText(loanedOnTextFormatted);

        String returnedOnText = loans.get(position).getReturnTimestamp();
        if (returnedOnText != null) {
            LocalDateTime returnedOnDateTime = LocalDateTime.parse(returnedOnText);
            calendar.set(returnedOnDateTime.getYear(), returnedOnDateTime.getMonthValue() - 1, returnedOnDateTime.getDayOfMonth());
            String returnedOnTextFormatted = Date.format(calendar.getTime());
            holder.tReturnedOn.setText(returnedOnTextFormatted);
        } else {
            holder.tReturnedOn.setText("-");
        }
    }

    private void viewLoanDetails(View view, int position) {
        Snackbar.make(view, "details", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        if (loans != null) {
            return loans.size();
        } else {
            return 0;
        }
    }

    public class LoanViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        CardView cardView;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;
        TextView tLoanedOn;
        TextView tReturnedOn;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            cardView = itemView.findViewById(R.id.cardView);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
            tLoanedOn = itemView.findViewById(R.id.tLoanedOn);
            tReturnedOn = itemView.findViewById(R.id.tReturnedOn);
        }
    }
}
