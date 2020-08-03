package com.example.fix_it_pagliu.user.news;

public class Post {
    String object;
    String uid;
    String id;
    String status;
    String description;
    String date;

    public Post() {
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Post{" +
                "object='" + object + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
