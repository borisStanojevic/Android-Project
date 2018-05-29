package com.example.student.myproject.util;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Util {

    private static final String SERVICE_API_PATH = "http://192.168.0.105:8080/posts-portal/android-test/";

    public static final Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
