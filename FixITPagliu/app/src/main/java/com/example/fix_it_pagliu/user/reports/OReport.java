package com.example.fix_it_pagliu.user.reports;

public class OReport {
    String object;
    String id;
    String description;
    String date;

    public OReport() {
    }

    @Override
    public String toString() {
        return "OReport{" +
                "object='" + object + '\'' +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getObject() {
        return object;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
