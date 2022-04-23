package com.ad_victoriam.libtex.user.fragments.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.ad_victoriam.libtex.R;
import com.ad_victoriam.libtex.user.fragments.books.BookDetailsFragment;
import com.ad_victoriam.libtex.user.models.Book;

public class WhatToReadResultDialogFragment extends AppCompatDialogFragment {

    private View mainView;
    private FragmentActivity activity;

    private Book book;

    public WhatToReadResultDialogFragment(Book book) {
        this.book = book;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mainView = inflater.inflate(R.layout.dialog_what_to_read_result, null);

        activity = requireActivity();

        builder.setView(mainView)
                .setTitle("Have you thought about ...")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        if (book == null) {
            showFailure();
        } else {
            showBook();
        }

        return builder.create();
    }

    private void showFailure() {
        ImageView iBook = mainView.findViewById(R.id.iBook);
        TextView tBookTitle = mainView.findViewById(R.id.tBookTitle);
        TextView tBookAuthor= mainView.findViewById(R.id.tBookAuthor);
        iBook.setVisibility(View.GONE);
        tBookTitle.setVisibility(View.GONE);
        tBookAuthor.setVisibility(View.GONE);

        TextView tError = mainView.findViewById(R.id.tError);
        tError.setVisibility(View.VISIBLE);
        tError.setText(activity.getString(R.string.what_to_read_not_found));
    }

    private void showBook() {
        TextView tBookTitle = mainView.findViewById(R.id.tBookTitle);
        TextView tBookAuthor= mainView.findViewById(R.id.tBookAuthor);
        tBookTitle.setText(book.getTitle());
        String bookAuthor = "by " + book.getAuthorName();
        tBookAuthor.setText(bookAuthor);

        TextView tError = mainView.findViewById(R.id.tError);
        tError.setVisibility(View.GONE);

        mainView.findViewById(R.id.constraintLayout).setOnClickListener(this::viewBookDetails);
    }

    private void viewBookDetails(View view) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("book", book);

        BookDetailsFragment bookDetailsFragment = new BookDetailsFragment();
        bookDetailsFragment.setArguments(bundle);

        activity
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, bookDetailsFragment)
                .addToBackStack("whatToReadResultDialogFragment")
                .commit();

        dismiss();
    }
}
