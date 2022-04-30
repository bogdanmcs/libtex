package com.ad_victoriam.libtex.user.models;

public class Review {

    private String uid;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String userUid;
    private double rating = 0;
    private String description;

    public Review() {
    }

    public Review(String bookTitle, String bookAuthor, String bookPublisher, String userUid, double rating, String description) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.userUid = userUid;
        this.rating = rating;
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isBook(Book book) {
        return this.bookTitle.equals(book.getTitle()) &&
                this.bookAuthor.equals(book.getAuthorName()) &&
                this.bookPublisher.equals(book.getPublisher());
    }
}
