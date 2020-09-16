package com.application.fix_it_pagliuca.mapped_objects;

@SuppressWarnings("ALL")
public class User {
    public User(String username, String fullname, String surname, String uid, String fiscalCode, String birthday, String email, String imageURL) {
        this.username = username;
        this.fullname = fullname;
        this.surname = surname;
        this.uid = uid;
        this.fiscalCode = fiscalCode;
        this.birthday = birthday;
        this.email = email;
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullname='" + fullname + '\'' +
                ", surname='" + surname + '\'' +
                ", fiscalCode='" + fiscalCode + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", imageURL='" + imageURL + '\'' +
                '}';
    }

    private String username;
    private String fullname;
    private String surname;
    private String fiscalCode;
    private String birthday;
    private String email;
    private String role;
    private String imageURL;
    private String uid;
}
