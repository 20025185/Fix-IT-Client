package com.example.fix_it_pagliu.user.reports;

public class OReport {
    String object;
    String id;
    String description;
    String date;
    String status;
    String type;

    public OReport() {
    }

    @Override
    public String toString() {
        return "OReport{" +
                "object='" + object + '\'' +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getType() {
        return type;
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

    public String getStatus() {
        return status.split("_")[0];
    }

    public String getDate() {
        return date;
    }
}
