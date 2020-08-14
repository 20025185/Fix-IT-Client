package com.example.fix_it_pagliu.database;

public class Report {
    public Report(String uid, String id, String object, String date, String time, String position, boolean social, String description, String type) {
        this.uid = uid;
        this.id = id;
        this.object = object;
        this.date = date;
        this.time = time;
        this.position = position;
        this.social = social;
        this.description = description;
        this.type = type;
        this.status = "Aperta" + "_" + id;
        this.priority = "0";
    }

    @Override
    public String toString() {
        return "Report{" +
                "uid='" + uid + '\'' +
                ", id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", position='" + position + '\'' +
                ", social=" + social +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                '}';
    }

    public Report(String id, String position, String priority, String description) {
        this.id = id;
        this.position = position;
        this.priority = priority;
        this.description = description;
    }

    public Report(String id, String position, String priority, String description, String object) {
        this.id = id;
        this.position = position;
        this.priority = priority;
        this.description = description;
        this.object = object;
    }

    public Report(String id, String position, String priority, String description, String object, String date, String time, String type, String status) {
        this.id = id;
        this.object = object;
        this.date = date;
        this.time = time;
        this.position = position;
        this.description = description;
        this.type = type;
        this.status = status;
        this.priority = priority;
    }

    public String getUid() {
        return uid;
    }

    public String getObject() {
        return object;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPosition() {
        return position;
    }

    public boolean isSocial() {
        return social;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSocial(boolean social) {
        this.social = social;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    private String uid;

    public String getId() {
        return id;
    }

    private String id;
    private String object;
    private String date;
    private String time;
    private String position;
    private boolean social;
    private String description;
    private String type;
    private String status = "undefined";
    private String priority = "-1";


    //private Image img = new Image("C:\\Users\\Manuel\\Pictures\\Immagine.png");

    //  Allegato
}
