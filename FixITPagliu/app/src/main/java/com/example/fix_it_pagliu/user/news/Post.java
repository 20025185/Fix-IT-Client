package com.example.fix_it_pagliu.user.news;

@SuppressWarnings("NullableProblems")
public class Post {
    String object;
    String uid;
    String id;
    String status;
    String description;
    String date;

    public Post() {
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

    public String getObject() {
        return object;
    }

    public String getDate() {
        return date;
    }
}
