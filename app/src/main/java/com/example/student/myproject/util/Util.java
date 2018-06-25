package com.example.student.myproject.util;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public final class Util {

    public static final String SERVICE_API_PATH = "http://192.168.0.105:8080/posts-portal/";

    public static final Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
}
