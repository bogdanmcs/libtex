package com.ad_victoriam.libtex.common.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String uid;
    private String email;
    private String fullName;
    private String idCardSeries;
    private String idCardNumber;
    private String dateOfBirthday;
    private String phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.email = email;
        this.fullName = null;
        this.idCardSeries = null;
        this.idCardNumber = null;
        this.dateOfBirthday = null;
        this.phoneNumber = null;
    }

    public User(
            String email,
            String fullName,
            String idCardSeries,
            String idCardNumber,
            String dateOfBirthday,
            String phoneNumber) {
        this.email = email;
        this.fullName = fullName;
        this.idCardSeries = idCardSeries;
        this.idCardNumber = idCardNumber;
        this.dateOfBirthday = dateOfBirthday;
        this.phoneNumber = phoneNumber;
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

    public String getFullName() {
        return fullName;
    }

    public String getIdCardSeries() {
        return idCardSeries;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public String getDateOfBirthday() {
        return dateOfBirthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(email);
        parcel.writeString(fullName);
        parcel.writeString(idCardSeries);
        parcel.writeString(idCardNumber);
        parcel.writeString(dateOfBirthday);
        parcel.writeString(phoneNumber);
    }

    protected User(Parcel in) {
        uid = in.readString();
        email = in.readString();
        fullName = in.readString();
        idCardSeries = in.readString();
        idCardNumber = in.readString();
        dateOfBirthday = in.readString();
        phoneNumber = in.readString();
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