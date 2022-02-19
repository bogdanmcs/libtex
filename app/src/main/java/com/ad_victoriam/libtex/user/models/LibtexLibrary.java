package com.ad_victoriam.libtex.user.models;

import java.util.ArrayList;
import java.util.List;

public class LibtexLibrary {

    private String uid;
    private final List<Book> books = new ArrayList<>();

    public LibtexLibrary() {
        // empty public constructor
    }

    public String getUid() {
        return uid;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
