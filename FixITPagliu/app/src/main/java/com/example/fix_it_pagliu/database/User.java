package com.example.fix_it_pagliu.database;

import java.util.Date;

public class User {

    public User(String fullname, String surname, String fiscalCode, String birthday, String email) {
        this.fullname = fullname;
        this.surname = surname;
        this.fiscalCode = fiscalCode;
        this.birthday = birthday;
        this.email = email;
        this.role = "user";
    }

    public String getFullname() {
        return fullname;
    }

    public String getSurname() {
        return surname;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() { return role; }

    private String fullname;
    private String surname;
    private String fiscalCode;
    private String birthday;
    private String email;
    private String role;
}
