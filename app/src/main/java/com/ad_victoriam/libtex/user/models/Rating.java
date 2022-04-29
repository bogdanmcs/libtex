package com.ad_victoriam.libtex.user.models;

public class Rating {

    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private double rating = 0;

    public Rating() {
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isBook(Book book) {
        return this.bookTitle.equals(book.getTitle()) &&
                this.bookAuthor.equals(book.getAuthorName()) &&
                this.bookPublisher.equals(book.getPublisher());
    }
}
