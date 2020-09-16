package com.example.fix_it_pagliu.kafka.api;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KafkaRecords {
    @SerializedName("records")
    @Expose
    private List<Records> recordsList = null;

    public void setRecordsList(List<Records> recordsList) {
        this.recordsList = recordsList;
    }
}
