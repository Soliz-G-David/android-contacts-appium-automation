package com.test.contacts.models;

public record Contact(String firstName,
                      String lastName,
                      String company,
                      String phone,
                      String email) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}