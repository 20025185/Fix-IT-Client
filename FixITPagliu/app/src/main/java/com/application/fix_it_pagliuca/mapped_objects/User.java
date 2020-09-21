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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
