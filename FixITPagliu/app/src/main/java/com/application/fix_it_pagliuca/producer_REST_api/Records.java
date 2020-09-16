package com.application.fix_it_pagliuca.producer_REST_api;

import com.application.fix_it_pagliuca.mapped_objects.Report;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Records {
    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("value")
    @Expose
    private Report report;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Report getReport() {
        return report;
    }

    public void setValue(Report report) {
        this.report = report;
    }
}
