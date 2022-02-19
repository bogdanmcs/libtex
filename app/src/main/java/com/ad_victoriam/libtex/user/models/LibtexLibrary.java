package com.ad_victoriam.libtex.user.models;

import java.util.ArrayList;
import java.util.List;

public class LibtexLibrary {

    private String uid;
    private String name;
    private final List<Book> books = new ArrayList<>();

    public LibtexLibrary() {
        // empty public constructor
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        books.add(book);
    }
}
