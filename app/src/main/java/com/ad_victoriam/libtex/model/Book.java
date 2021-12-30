package com.ad_victoriam.libtex.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

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

    protected Book(Parcel in) {
        title = in.readString();
        authorName = in.readString();
        publisher = in.readString();
        noOfPages = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(authorName);
        parcel.writeString(publisher);
        parcel.writeString(noOfPages);
    }
}
