package com.ad_victoriam.libtex.admin.activities.book;

import androidx.annotation.NonNull;

import com.ad_victoriam.libtex.model.Book;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookLoan {

    private String bookLoanUid;
    private String bookUid;
    private String timestamp;
    private String deadline;

    private Book book;

    public BookLoan() {

    }

    public BookLoan(String bookUid, String timestamp, String deadline, Book book) {
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        this.timestamp = timestamp;
        this.deadline = deadline;
        this.book = book;
    }

    public BookLoan(String bookUid) {
        this.bookLoanUid = null;
        this.bookUid = bookUid;
        LocalDateTime timestamp = LocalDateTime.now();
        this.timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(timestamp);
        LocalDateTime deadline = timestamp.plusMonths(3);
        this.deadline = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(deadline);
        this.book = null;
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
                "bookLoanUid='" + bookLoanUid + '\'' +
                ", bookUid='" + bookUid + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", deadline='" + deadline + '\'' +
                ", book=" + book +
                '}';
    }
}
