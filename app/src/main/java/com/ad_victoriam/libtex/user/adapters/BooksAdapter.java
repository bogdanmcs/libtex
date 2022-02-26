package com.ad_victoriam.libtex.user.adapters;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.fragments.books.BookDetailsFragment;
import com.ad_victoriam.libtex.user.models.Book;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private final FragmentActivity activity;
    private final List<Book> books;

    public BooksAdapter(FragmentActivity activity, List<Book> books) {
        this.activity = activity;
        this.books = books;
    }


    @NonNull
    @Override
    public BooksAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksAdapter.BookViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewDetails(view, position));
        holder.tBookTitle.setText(books.get(position).getTitle());
        String text = "by " + books.get(position).getAuthorName();
        holder.tBookAuthorName.setText(text);
        holder.tBookPublisher.setText(books.get(position).getPublisher());

        if (books.get(position).getAvailableQuantity() > 0) {
            holder.tStockStatus.setText(activity.getString(R.string.in_stock));
            int color = ContextCompat.getColor(activity, R.color.green);
            holder.tStockStatus.setTextColor(color);
        } else {
            holder.tStockStatus.setText(activity.getString(R.string.out_of_stock));
            int color = ContextCompat.getColor(activity, R.color.red);
            holder.tStockStatus.setTextColor(color);
        }

        if (position == books.size() - 1) {
            holder.div.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
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

    private void viewDetails(View view, int position) {
        Bundle book = new Bundle();
        book.putParcelable("book", books.get(position));

        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        bookDetailsFragment.setArguments(book);

        activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, bookDetailsFragment)
                .addToBackStack("booksFragment")
                .commit();
    }
}
