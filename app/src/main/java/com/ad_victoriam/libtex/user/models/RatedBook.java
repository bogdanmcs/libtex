package com.ad_victoriam.libtex.user.models;

public class RatedBook {

    private Book book;
    private double rating;

    public RatedBook(Book book, Double rating) {
        this.book = book;
        this.rating = rating;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
