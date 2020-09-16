package com.application.fix_it_pagliuca.user.reports.recycle_view_menus;

public class RecycleReportItem {
    String object;
    String id;
    String description;
    String date;
    String status;
    String type;

    public RecycleReportItem() {
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
