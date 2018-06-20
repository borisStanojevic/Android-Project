package com.example.student.myproject.util;

import com.example.student.myproject.model.User;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @GET("users/{username}")
    Call<User> getByUsername(@Path("username") String username);

    @POST("users")
    Call<User> create(@Body User user);

    @PUT("users")
    Call<User> update(@Body User user);

    @DELETE("users/{username}")
    Call<Void> delete(@Path("username") String username);

    @GET("users")
    Call<List<User>> getAll();
}
