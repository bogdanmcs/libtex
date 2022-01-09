package com.ad_victoriam.libtex.common.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    private String uid;
    private String title;
    private String authorName;
    private String publisher;
    private String noOfPages;
    private String description;
    private int availableQuantity;
    private int totalQuantity;


    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Book(String title, String authorName, String publisher, String noOfPages, String description, int totalQuantity) {
        this.title = title;
        this.authorName = authorName;
        this.publisher = publisher;
        this.noOfPages = noOfPages;
        this.description = description;
        this.availableQuantity = totalQuantity;
        this.totalQuantity = totalQuantity;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getNoOfPages() {
        return noOfPages;
    }

    public void setNoOfPages(String noOfPages) {
        this.noOfPages = noOfPages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(title);
        parcel.writeString(authorName);
        parcel.writeString(publisher);
        parcel.writeString(noOfPages);
        parcel.writeString(description);
        parcel.writeInt(availableQuantity);
        parcel.writeInt(totalQuantity);
    }

    protected Book(Parcel in) {
        uid = in.readString();
        title = in.readString();
        authorName = in.readString();
        publisher = in.readString();
        noOfPages = in.readString();
        description = in.readString();
        availableQuantity = in.readInt();
        totalQuantity = in.readInt();
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
}
