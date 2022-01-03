package com.ad_victoriam.libtex.admin.activities.book;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.ad_victoriam.libtex.model.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookLoan implements Parcelable {

    private String libraryUid;
    private String bookLoanUid;
    private String bookUid;
    private String timestamp;
    private String deadline;

    private Book book;

    public BookLoan() {

    }

    public BookLoan(String bookUid, String timestamp, String deadline, Book book) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        this.timestamp = timestamp;
        this.deadline = deadline;
        this.book = book;
    }

    public BookLoan(String bookUid) {
        this.libraryUid = null;
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        LocalDateTime timestamp = LocalDateTime.now();
        this.timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp);
        LocalDateTime deadline = timestamp.plusMonths(3);
        this.deadline = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(deadline);
        this.book = null;
    }

    protected BookLoan(Parcel in) {
        libraryUid = in.readString();
        bookLoanUid = in.readString();
        bookUid = in.readString();
        timestamp = in.readString();
        deadline = in.readString();
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

    public String getTimestamp() {
        return timestamp;
    }

    public String getDeadline() {
        return deadline;
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

    @NonNull
    @Override
    public String toString() {
        return "BookLoan{" +
                "libraryUid='" + libraryUid + '\'' +
                "bookLoanUid='" + bookLoanUid + '\'' +
                ", bookUid='" + bookUid + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", deadline='" + deadline + '\'' +
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
        parcel.writeString(timestamp);
        parcel.writeString(deadline);
        parcel.writeParcelable(book, i);
    }
}
