package com.ad_victoriam.libtex.admin.activities.book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ad_victoriam.libtex.R;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    Context context;
    private List<String> bookTitle;
    private List<String> bookAuthorName;
//    private String[] bookTitle;
//    private String[] bookAuthorName;

    public BookAdapter(Context context, List<String> bookTitle, List<String> bookAuthorName) {
        this.context = context;
        this.bookTitle = bookTitle;
        this.bookAuthorName = bookAuthorName;
    }

    public void addItemsToList(String bookTitle, String bookAuthorName) {
        this.bookTitle.add(bookTitle);
        this.bookAuthorName.add(bookAuthorName);
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_adapter_row, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
//        holder.tBookTitle.setText(bookTitle[position]);
//        holder.tBookAuthorName.setText(bookAuthorName[position]);
//        System.out.println("Setting text tBookTitle to: " + bookTitle[position]);
//        System.out.println("Setting text tBookAuthorName to: " + bookAuthorName[position]);
        holder.tBookTitle.setText(bookTitle.get(position));
        holder.tBookAuthorName.setText(bookAuthorName.get(position));
    }

    @Override
    public int getItemCount() {
        return bookTitle.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {

        TextView tBookTitle;
        TextView tBookAuthorName;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tBookTitle = itemView.findViewById(R.id.tBookTitle);
            tBookAuthorName = itemView.findViewById(R.id.tBookAuthorName);
        }
    }
}
