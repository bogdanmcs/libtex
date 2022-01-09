package com.ad_victoriam.libtex.common.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private String idCardSerialNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
        this.firstName = null;
        this.lastName = null;
        this.idCardSerialNumber = null;
    }

    public User(String email, String firstName, String lastName, String idCardSerialNumber) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCardSerialNumber = idCardSerialNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdCardSerialNumber() {
        return idCardSerialNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(email);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(idCardSerialNumber);
    }

    protected User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        idCardSerialNumber = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
