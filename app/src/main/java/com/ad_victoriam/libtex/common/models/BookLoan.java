package com.ad_victoriam.libtex.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.ad_victoriam.libtex.admin.models.AdminBook;

import java.time.LocalDateTime;

public class BookLoan implements Parcelable {

    private String libraryUid;
    private String bookLoanUid;
    private String bookUid;
    private String loanTimestamp;
    private String deadlineTimestamp;
    private String returnTimestamp;

    private AdminBook adminBook;

    public BookLoan() {
    }

    public BookLoan(String bookUid, String loanTimestamp, String deadlineTimestamp, AdminBook adminBook) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        this.loanTimestamp = loanTimestamp;
        this.deadlineTimestamp = deadlineTimestamp;
        this.returnTimestamp = null;
        this.adminBook = adminBook;
    }

    public BookLoan(String bookUid, String loanTimestamp, String deadlineTimestamp, String returnTimestmap, AdminBook adminBook) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        this.loanTimestamp = loanTimestamp;
        this.deadlineTimestamp = deadlineTimestamp;
        this.returnTimestamp = returnTimestmap;
        this.adminBook = adminBook;
    }

    public BookLoan(String bookUid) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        LocalDateTime currentDateTime = LocalDateTime.now();
        this.loanTimestamp = currentDateTime.toString();
        this.deadlineTimestamp = currentDateTime.plusMonths(3).toString();
        this.returnTimestamp = null;
        this.adminBook = null;
    }

    protected BookLoan(Parcel in) {
        libraryUid = in.readString();
        bookLoanUid = in.readString();
        bookUid = in.readString();
        loanTimestamp = in.readString();
        deadlineTimestamp = in.readString();
        returnTimestamp = in.readString();
        adminBook = in.readParcelable(AdminBook.class.getClassLoader());
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

    public String getReturnTimestamp() {
        return returnTimestamp;
    }

    public AdminBook getBook() {
        return adminBook;
    }

    public void setBook(AdminBook adminBook) {
        this.adminBook = adminBook;
    }

    public String getBookLoanUid() {
        return bookLoanUid;
    }

    public void setBookLoanUid(String bookLoanUid) {
        this.bookLoanUid = bookLoanUid;
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
        parcel.writeString(returnTimestamp);
        parcel.writeParcelable(adminBook, i);
    }
}
