package com.ad_victoriam.libtex.user.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Book implements Parcelable {

    private String uid;
    private String title;
    private String authorName;
    private String publisher;
    private List<String> chosenCategories;
    private String noOfPages;
    private String description;
    private int availableQuantity;
    private List<String> locations = new ArrayList<>();

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    public Book(String title, String authorName, String publisher, List<String> chosenCategories,
                String noOfPages, String description, int availableQuantity) {
        this.title = title;
        this.authorName = authorName;
        this.publisher = publisher;
        this.chosenCategories = chosenCategories;
        this.noOfPages = noOfPages;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.locations = null;
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

    public List<String> getChosenCategories() {
        return chosenCategories;
    }

    public void setChosenCategories(List<String> chosenCategories) {
        this.chosenCategories = chosenCategories;
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

    public List<String> getLocations() {
        return locations;
    }

    public void addLocation(String libraryLocation) {
        locations.add(libraryLocation);
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
        parcel.writeList(chosenCategories);
        parcel.writeString(noOfPages);
        parcel.writeString(description);
        parcel.writeInt(availableQuantity);
        parcel.writeList(locations);
    }

    protected Book(Parcel in) {
        uid = in.readString();
        title = in.readString();
        authorName = in.readString();
        publisher = in.readString();
        chosenCategories = in.readArrayList(null);
        noOfPages = in.readString();
        description = in.readString();
        availableQuantity = in.readInt();
        locations = in.readArrayList(null);
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

    public boolean isSame(Book book) {
        return this.title.equals(book.title) &&
                this.authorName.equals(book.authorName) &&
                this.publisher.equals(book.publisher);
    }
}
