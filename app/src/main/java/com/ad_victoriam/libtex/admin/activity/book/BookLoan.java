package com.ad_victoriam.libtex.admin.activity.book;

import android.os.Parcel;
import android.os.Parcelable;

import com.ad_victoriam.libtex.common.model.Book;

import java.time.LocalDateTime;

public class BookLoan implements Parcelable {

    private String libraryUid;
    private String bookLoanUid;
    private String bookUid;
    private String loanTimestamp;
    private String deadlineTimestamp;

    private Book book;

    public BookLoan() {

    }

    public BookLoan(String bookUid, String loanTimestamp, String deadlineTimestamp, Book book) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        this.loanTimestamp = loanTimestamp;
        this.deadlineTimestamp = deadlineTimestamp;
        this.book = book;
    }

    public BookLoan(String bookUid) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        LocalDateTime currentDateTime = LocalDateTime.now();
        this.loanTimestamp = currentDateTime.toString();
        this.deadlineTimestamp = currentDateTime.plusMonths(3).toString();
        this.book = null;
    }

    protected BookLoan(Parcel in) {
        libraryUid = in.readString();
        bookLoanUid = in.readString();
        bookUid = in.readString();
        loanTimestamp = in.readString();
        deadlineTimestamp = in.readString();
        book = in.readParcelable(Book.class.getClassLoader());
    }

    public static final Creator<BookLoan> CREATOR = new Creator<BookLoan>() {
        @Override
        public BookLoan createFromParcel(Parcel in) {
            return new BookLoan(in);
        }

        @Override
        public BookLoan[] newArray(int size) {
            return new BookLoan[size];
        }
    };

    public String getLibraryUid() {
        return libraryUid;
    }

    public void setLibraryUid(String libraryUid) {
        this.libraryUid = libraryUid;
    }

    public String getBookUid() {
        return bookUid;
    }

    public String getLoanTimestamp() {
        return loanTimestamp;
    }

    public String getDeadlineTimestamp() {
        return deadlineTimestamp;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBookLoanUid() {
        return bookLoanUid;
    }

    public void setBookLoanUid(String bookLoanUid) {
        this.bookLoanUid = bookLoanUid;
    }

    @Override
    public String toString() {
        return "BookLoan{" +
                "libraryUid='" + libraryUid + '\'' +
                ", bookLoanUid='" + bookLoanUid + '\'' +
                ", bookUid='" + bookUid + '\'' +
                ", loanTimestamp='" + loanTimestamp + '\'' +
                ", deadlineTimestamp='" + deadlineTimestamp + '\'' +
                ", book=" + book +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(libraryUid);
        parcel.writeString(bookLoanUid);
        parcel.writeString(bookUid);
        parcel.writeString(loanTimestamp);
        parcel.writeString(deadlineTimestamp);
        parcel.writeParcelable(book, i);
    }
}
