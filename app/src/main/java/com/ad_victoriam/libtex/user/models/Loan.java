package com.ad_victoriam.libtex.user.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class Loan implements Parcelable {

    private String libraryUid;
    private String loanUid;
    private String bookUid;
    private String loanTimestamp;
    private String deadlineTimestamp;
    private String returnTimestamp;

    private Book book;

    public Loan() {
    }

    public Loan(String bookUid, String loanTimestamp, String deadlineTimestamp, Book book) {
        this.libraryUid = null;
        this.loanUid = null;
        this.bookUid = bookUid;
        this.loanTimestamp = loanTimestamp;
        this.deadlineTimestamp = deadlineTimestamp;
        this.returnTimestamp = null;
        this.book = book;
    }

    public Loan(String bookUid, String loanTimestamp, String deadlineTimestamp, String returnTimestamp, Book book) {
        this.libraryUid = null;
        this.loanUid = null;
        this.bookUid = bookUid;
        this.loanTimestamp = loanTimestamp;
        this.deadlineTimestamp = deadlineTimestamp;
        this.returnTimestamp = returnTimestamp;
        this.book = book;
    }

    public Loan(String bookUid) {
        this.libraryUid = null;
        this.loanUid = null;
        this.bookUid = bookUid;
        LocalDateTime currentDateTime = LocalDateTime.now();
        this.loanTimestamp = currentDateTime.toString();
        this.deadlineTimestamp = currentDateTime.plusMonths(3).toString();
        this.returnTimestamp = null;
        this.book = null;
    }

    protected Loan(Parcel in) {
        libraryUid = in.readString();
        loanUid = in.readString();
        bookUid = in.readString();
        loanTimestamp = in.readString();
        deadlineTimestamp = in.readString();
        returnTimestamp = in.readString();
        book = in.readParcelable(Book.class.getClassLoader());
    }

    public static final Creator<Loan> CREATOR = new Creator<Loan>() {
        @Override
        public Loan createFromParcel(Parcel in) {
            return new Loan(in);
        }

        @Override
        public Loan[] newArray(int size) {
            return new Loan[size];
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

    public String getReturnTimestamp() {
        return returnTimestamp;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBookLoanUid() {
        return loanUid;
    }

    public void setBookLoanUid(String loanUid) {
        this.loanUid = loanUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(libraryUid);
        parcel.writeString(loanUid);
        parcel.writeString(bookUid);
        parcel.writeString(loanTimestamp);
        parcel.writeString(deadlineTimestamp);
        parcel.writeString(returnTimestamp);
        parcel.writeParcelable(book, i);
    }
}

