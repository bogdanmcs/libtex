package com.ad_victoriam.libtex.admin.adapters;

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
import com.ad_victoriam.libtex.common.models.User;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

public class AdminAllLoanAdapter extends RecyclerView.Adapter<AdminAllLoanAdapter.BookLoanViewHolder> {

    private final FragmentActivity activity;
    private final List<AdminLoan> adminLoans;
    private User user;

    public AdminAllLoanAdapter(FragmentActivity activity, List<AdminLoan> adminLoans) {
        this.activity = activity;
        this.adminLoans = adminLoans;
    }

    public AdminAllLoanAdapter(FragmentActivity activity, List<AdminLoan> adminLoans, User user) {
        this.activity = activity;
        this.adminLoans = adminLoans;
        this.user = user;
    }

    @NonNull
    @Override
    public AdminAllLoanAdapter.BookLoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_all_loan, parent, false);
        return new AdminAllLoanAdapter.BookLoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAllLoanAdapter.BookLoanViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewLoanDetails(view, position));
        holder.tBookTitle.setText(adminLoans.get(position).getBook().getTitle());
        holder.tBookAuthorName.setText(adminLoans.get(position).getBook().getAuthorName());
        holder.tBookPublisher.setText(adminLoans.get(position).getBook().getPublisher());

        DateFormat Date = DateFormat.getDateInstance();
        Calendar calendar = Calendar.getInstance();

        String loanedOnText = adminLoans.get(position).getLoanTimestamp();
        LocalDateTime loanedOnDateTime = LocalDateTime.parse(loanedOnText);
        calendar.set(loanedOnDateTime.getYear(), loanedOnDateTime.getMonthValue() - 1, loanedOnDateTime.getDayOfMonth());
        String loanedOnTextFormatted = Date.format(calendar.getTime());
        holder.tLoanedOn.setText(loanedOnTextFormatted);

        String returnedOnText = adminLoans.get(position).getReturnTimestamp();
        if (returnedOnText != null) {
            LocalDateTime returnedOnDateTime = LocalDateTime.parse(returnedOnText);
            calendar.set(returnedOnDateTime.getYear(), returnedOnDateTime.getMonthValue() - 1, returnedOnDateTime.getDayOfMonth());
            String returnedOnTextFormatted = Date.format(calendar.getTime());
            holder.tReturnedOn.setText(returnedOnTextFormatted);
        } else {
            holder.tReturnedOn.setText("-");
        }
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
        TextView tLoanedOn;
        TextView tReturnedOn;

        public BookLoanViewHolder(@NonNull View itemView) {
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

    private void viewLoanDetails(View view, int position) {
        Intent intent = new Intent(activity, AdminBookDetailsActivity.class);
        intent.putExtra("book", adminLoans.get(position).getBook());
        activity.startActivity(intent);
    }
}
