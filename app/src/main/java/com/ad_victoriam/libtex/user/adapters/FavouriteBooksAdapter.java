package com.ad_victoriam.libtex.user.adapters;

import android.content.Intent;
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
import com.ad_victoriam.libtex.user.activities.BookDetailsActivity;
import com.ad_victoriam.libtex.user.fragments.books.BookDetailsFragment;
import com.ad_victoriam.libtex.user.models.Book;

import java.util.List;

public class FavouriteBooksAdapter extends RecyclerView.Adapter<FavouriteBooksAdapter.BookViewHolder> {

    private final FragmentActivity activity;
    private final List<Book> favouriteBooks;


    public FavouriteBooksAdapter(FragmentActivity activity, List<Book> favouriteBooks) {
        this.activity = activity;
        this.favouriteBooks = favouriteBooks;
    }

    @NonNull
    @Override
    public FavouriteBooksAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_book, parent, false);
        return new FavouriteBooksAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteBooksAdapter.BookViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewBookDetails(view, position));
        holder.tBookTitle.setText(favouriteBooks.get(position).getTitle());
        holder.tBookAuthorName.setText(favouriteBooks.get(position).getAuthorName());
        holder.tBookPublisher.setText(favouriteBooks.get(position).getPublisher());
        holder.tStockStatus.setVisibility(View.INVISIBLE);

        if (position == favouriteBooks.size() - 1) {
            holder.div.setVisibility(View.INVISIBLE);
        }
    }

    private void viewBookDetails(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("book", favouriteBooks.get(position));

        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        bookDetailsFragment.setArguments(bundle);

        activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, bookDetailsFragment)
                .addToBackStack("booksFragment")
                .commit();
    }

    @Override
    public int getItemCount() {
        return favouriteBooks.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        TextView tBookTitle;
        TextView tBookAuthorName;
        TextView tBookPublisher;
        TextView tStockStatus;
        View div;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
            tStockStatus = itemView.findViewById(R.id.tStockStatus);
            div = itemView.findViewById(R.id.div);
        }
    }
}

