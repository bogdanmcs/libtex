package com.ad_victoriam.libtex.user.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.fragments.books.BookDetailsFragment;
import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.user.models.Review;

import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private final FragmentActivity activity;
    private List<Book> books;
    private List<Review> reviews;

    public BooksAdapter(FragmentActivity activity, List<Book> books, List<Review> reviews) {
        this.activity = activity;
        this.books = books;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public BooksAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksAdapter.BookViewHolder holder, int position) {
        Book book = books.get(position);

        double rating = 0.0;
        for (Review review: reviews) {
            if (review.isBook(book)) {
                rating += review.getRating();
            }
        }
        holder.ratingBar.setProgress((int) (rating * 2));
        holder.constraintLayout.setOnClickListener(view -> viewDetails(book));
        holder.tBookTitle.setText(book.getTitle());
        String text = "by " + book.getAuthorName();
        holder.tBookAuthorName.setText(text);
        holder.tBookPublisher.setText(book.getPublisher());

        if (book.getAvailableQuantity() > 0) {
            holder.tStockStatus.setText(activity.getString(R.string.in_stock));
            int color = ContextCompat.getColor(activity, R.color.green);
            holder.tStockStatus.setTextColor(color);
        } else {
            holder.tStockStatus.setText(activity.getString(R.string.out_of_stock));
            int color = ContextCompat.getColor(activity, R.color.red);
            holder.tStockStatus.setTextColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(List<Book> filteredBooks) {
        this.books = filteredBooks;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;
        TextView tStockStatus;
        MaterialRatingBar ratingBar;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
            tStockStatus = itemView.findViewById(R.id.tStockStatus);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }

    private void viewDetails(Book book) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("book", book);

        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        bookDetailsFragment.setArguments(bundle);

        activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, bookDetailsFragment)
                .addToBackStack("booksFragment")
                .commit();
    }
}
