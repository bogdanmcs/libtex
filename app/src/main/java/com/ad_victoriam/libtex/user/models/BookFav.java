package com.ad_victoriam.libtex.user.models;

public class BookFav {

    private String title;
    private String authorName;
    private String publisher;

    public BookFav () {

    }

    public BookFav (String title, String authorName, String publisher) {
        this.title = title;
        this.authorName = authorName;
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public boolean isSame (Book book) {
        return title.equals(book.getTitle()) &&
                authorName.equals(book.getAuthorName()) &&
                publisher.equals(book.getPublisher());
    }
}
