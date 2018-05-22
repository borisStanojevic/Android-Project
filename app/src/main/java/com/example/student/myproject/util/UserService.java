package com.example.student.myproject.util;

import com.example.student.myproject.model.User;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {

    @GET("users/{username}")
    Call<User> doGetById(@Path("username") String username);
}
