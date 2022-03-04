package com.ad_victoriam.libtex.user.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Book implements Parcelable {

    private String uid;
    private String title;
    private String authorName;
    private String publisher;
    private List<String> chosenCategories;
    private String noOfPages;
    private String description;
    private int availableQuantity;
    // (library uid, location name)
    private Map<String, String> locations = new HashMap<>();
    private Map<String, String> locationsOutOfStock = new HashMap<>();
    // (library uid, book uid)
    private Map<String, String> sameBookUids = new HashMap<>();

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
        this.sameBookUids = null;
    }

    public void setUniqueDetails(Book book) {
        this.chosenCategories = book.getChosenCategories();
        this.noOfPages = book.getNoOfPages();
        this.description = book.getDescription();
        this.availableQuantity = book.getAvailableQuantity();
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

    public Map<String, String> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, String> locations) {
        this.locations = locations;
    }

    public List<String> getLocationsNames() {
        List<String> locationsList = new ArrayList<>();
        for (Map.Entry<String, String> entry: locations.entrySet()) {
            locationsList.add(entry.getValue());
        }
        return locationsList;
    }

    public void addLocation(LibtexLibrary libtexLibrary) {
        locations.put(libtexLibrary.getUid(), libtexLibrary.getName());
    }

    public Map<String, String> getLocationsOutOfStock() {
        return locationsOutOfStock;
    }

    public void setLocationsOutOfStock(Map<String, String> locationsOutOfStock) {
        this.locationsOutOfStock = locationsOutOfStock;
    }

    public List<String> getLocationsOutOfStockNames() {
        List<String> locationsList = new ArrayList<>();
        for (Map.Entry<String, String> entry: locationsOutOfStock.entrySet()) {
            locationsList.add(entry.getValue());
        }
        return locationsList;
    }

    public void addLocationOutOfStock(LibtexLibrary libtexLibrary) {
        locationsOutOfStock.put(libtexLibrary.getUid(), libtexLibrary.getName());
    }

    public Map<String, String> getSameBookUids() {
        return sameBookUids;
    }

    public void setSameBookUids(Map<String, String> sameBookUids) {
        this.sameBookUids = sameBookUids;
    }

    public void addSameBookUid(String libraryUid, String bookUid) {
        this.sameBookUids.put(libraryUid, bookUid);
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
        parcel.writeMap(locations);
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
        locations = in.readHashMap(null);
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
