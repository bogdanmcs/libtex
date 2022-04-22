package com.ad_victoriam.libtex.user.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.fragments.books.BookDetailsFragment;
import com.ad_victoriam.libtex.user.models.Book;

import java.util.List;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.RecommendationsViewHolder> {

    private final FragmentActivity activity;
    private final List<Book> books;

    public RecommendationsAdapter(FragmentActivity activity, List<Book> books) {
        this.activity = activity;
        this.books = books;
    }

    @NonNull
    @Override
    public RecommendationsAdapter.RecommendationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recommendation, parent, false);
        return new RecommendationsAdapter.RecommendationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationsAdapter.RecommendationsViewHolder holder, int position) {
        Book book = books.get(position);
        holder.constraintLayout.setOnClickListener(view -> viewBookDetails(book));
        holder.tBookTitle.setText(book.getTitle());
    }

    private void viewBookDetails(Book book) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("book", book);

        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        bookDetailsFragment.setArguments(bundle);

        activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, bookDetailsFragment)
                .addToBackStack("homeFragment")
                .commit();
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class RecommendationsViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tBookTitle;

        public RecommendationsViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
        }
    }
}
