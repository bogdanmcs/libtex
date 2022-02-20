package com.ad_victoriam.libtex.user.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.BundleCompat;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.models.Book;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private final Context context;
    private final List<Book> books;

    public BooksAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }


    @NonNull
    @Override
    public BooksAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_admin_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksAdapter.BookViewHolder holder, int position) {
        holder.constraintLayout.setOnClickListener(view -> viewDetails(view, position));
        holder.tBookTitle.setText(books.get(position).getTitle());
        String text = "by " + books.get(position).getAuthorName();
        holder.tBookAuthorName.setText(text);
        holder.tBookPublisher.setText(books.get(position).getPublisher());
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

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
            tBookPublisher = itemView.findViewById(R.id.tBookPublisher);
        }
    }

    private void viewDetails(View view, int position) {
        Bundle book = new Bundle();
        book.putParcelable("book", books.get(position));
        Navigation.findNavController(view).navigate(R.id.action_booksFragment_to_bookDetailsFragment, book);
    }
}
