package com.example.kuku;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String userUid;

    // No-argument constructor required by Firebase
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    // Parameterized constructor
    public User(String firstName, String lastName, String email, String phoneNumber, String userUid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userUid = userUid;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
