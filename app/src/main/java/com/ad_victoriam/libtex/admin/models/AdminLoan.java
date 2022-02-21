package com.ad_victoriam.libtex.admin.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class AdminLoan implements Parcelable {

    private String libraryUid;
    private String bookLoanUid;
    private String bookUid;
    private String loanTimestamp;
    private String deadlineTimestamp;
    private String returnTimestamp;

    private AdminBook adminBook;

    public AdminLoan() {
    }

    public AdminLoan(String bookUid, String loanTimestamp, String deadlineTimestamp, AdminBook adminBook) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        this.loanTimestamp = loanTimestamp;
        this.deadlineTimestamp = deadlineTimestamp;
        this.returnTimestamp = null;
        this.adminBook = adminBook;
    }

    public AdminLoan(String bookUid, String loanTimestamp, String deadlineTimestamp, String returnTimestamp, AdminBook adminBook) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        this.loanTimestamp = loanTimestamp;
        this.deadlineTimestamp = deadlineTimestamp;
        this.returnTimestamp = returnTimestamp;
        this.adminBook = adminBook;
    }

    public AdminLoan(String bookUid) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        LocalDateTime currentDateTime = LocalDateTime.now();
        this.loanTimestamp = currentDateTime.toString();
        this.deadlineTimestamp = currentDateTime.plusMonths(3).toString();
        this.returnTimestamp = null;
        this.adminBook = null;
    }

    protected AdminLoan(Parcel in) {
        libraryUid = in.readString();
        bookLoanUid = in.readString();
        bookUid = in.readString();
        loanTimestamp = in.readString();
        deadlineTimestamp = in.readString();
        returnTimestamp = in.readString();
        adminBook = in.readParcelable(AdminBook.class.getClassLoader());
    }

    public static final Creator<AdminLoan> CREATOR = new Creator<AdminLoan>() {
        @Override
        public AdminLoan createFromParcel(Parcel in) {
            return new AdminLoan(in);
        }

        @Override
        public AdminLoan[] newArray(int size) {
            return new AdminLoan[size];
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
