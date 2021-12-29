package com.ad_victoriam.libtex.model;

public class Book {

    // public
    private String title;
    private String authorName;
    private String publisher;
    private String noOfPages;


    public Book() {
        // data snapshot
    }

    public Book(String title, String authorName, String publisher, String noOfPages) {
        this.title = title;
        this.authorName = authorName;
        this.publisher = publisher;
        this.noOfPages = noOfPages;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getNoOfPages() {
        return noOfPages;
    }
}
