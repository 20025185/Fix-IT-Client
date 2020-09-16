package com.application.fix_it_pagliuca.producer_REST_api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface KafkaAPI {
    @Headers({"Host: com.example.fix_it_pagliu",
            "Content-Type: application/vnd.kafka.json.v2+json",
            "Accept: application/vnd.kafka.v2+json, application/vnd.kafka+json, application/json"
    })

    @POST("topics/input-ratings")
    Call<Void> createPost(@Body KafkaRecords kafkaRecords);

}