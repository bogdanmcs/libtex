package com.ad_victoriam.libtex.model;

public class User {

    private String firstName;
    private String lastName;
    private String idCardSeries;
    private String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email) {
        this.firstName = null;
        this.lastName = null;
        this.idCardSeries = null;
        this.email = email;
    }

    public User(String lastName, String firstName, String idCardSeries, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idCardSeries = idCardSeries;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdCardSeries() {
        return idCardSeries;
    }

    public String getEmail() {
        return email;
    }
}
