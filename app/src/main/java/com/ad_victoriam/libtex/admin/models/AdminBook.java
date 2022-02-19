package com.ad_victoriam.libtex.admin.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AdminBook implements Parcelable {

    private String uid;
    private String title;
    private String authorName;
    private String publisher;
    private List<String> chosenCategories;
    private String noOfPages;
    private String description;
    private int availableQuantity;
    private int totalQuantity;


    public AdminBook() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    public AdminBook(String title, String authorName, String publisher, List<String> chosenCategories,
                     String noOfPages, String description, int totalQuantity) {
        this.title = title;
        this.authorName = authorName;
        this.publisher = publisher;
        this.chosenCategories = chosenCategories;
        this.noOfPages = noOfPages;
        this.description = description;
        this.availableQuantity = totalQuantity;
        this.totalQuantity = totalQuantity;
    }

    public AdminBook(String title, String authorName, String publisher, List<String> chosenCategories,
                     String noOfPages, String description, int totalQuantity, int availableQuantity) {
        this.title = title;
        this.authorName = authorName;
        this.publisher = publisher;
        this.chosenCategories = chosenCategories;
        this.noOfPages = noOfPages;
        this.description = description;
        this.availableQuantity = availableQuantity;
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
        parcel.writeList(chosenCategories);
        parcel.writeString(noOfPages);
        parcel.writeString(description);
        parcel.writeInt(availableQuantity);
        parcel.writeInt(totalQuantity);
    }

    protected AdminBook(Parcel in) {
        uid = in.readString();
        title = in.readString();
        authorName = in.readString();
        publisher = in.readString();
        chosenCategories = in.readArrayList(null);
        noOfPages = in.readString();
        description = in.readString();
        availableQuantity = in.readInt();
        totalQuantity = in.readInt();
    }

    public static final Creator<AdminBook> CREATOR = new Creator<AdminBook>() {
        @Override
        public AdminBook createFromParcel(Parcel in) {
            return new AdminBook(in);
        }

        @Override
        public AdminBook[] newArray(int size) {
            return new AdminBook[size];
        }
    };
}
