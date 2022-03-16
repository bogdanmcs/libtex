package com.ad_victoriam.libtex.common.models;

import com.ad_victoriam.libtex.user.models.Book;
import com.ad_victoriam.libtex.common.utils.ReservationStatus;

public class Reservation {

    private String userUid;

    private String libraryUid;
    private String locationName;
    private Book book;

    private String uid;
    private String bookUid;
    private String startDate;
    private String endDate;
    private ReservationStatus status;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Reservation.class)
    }

    public Reservation(String userUid, String libraryUid, String bookUid, String startDate, String endDate, ReservationStatus status) {
        this.userUid = userUid;
        this.libraryUid = libraryUid;
        this.bookUid = bookUid;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getLibraryUid() {
        return libraryUid;
    }

    public void setLibraryUid(String libraryUid) {
        this.libraryUid = libraryUid;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBookUid() {
        return bookUid;
    }

    public void setBookUid(String bookUid) {
        this.bookUid = bookUid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
