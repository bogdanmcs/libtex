package com.ad_victoriam.libtex.admin.model;

public class User {
    private String id;
    private String lastName;
    private String firstName;
    private String idCardSeries;
    private String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String lastName, String firstName, String idCardSeries, String email) {
        this.id = null;
        this.lastName = lastName;
        this.firstName = firstName;
        this.idCardSeries = idCardSeries;
        if (email.isEmpty()) {
            this.email = null;
        } else {
            this.email = email;
        }
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getIdCardSeries() {
        return idCardSeries;
    }

    public String getEmail() {
        return email;
    }
}
