package com.example.student.myproject.util;

import com.example.student.myproject.model.User;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    @POST("auth/login")
    Call<UserTokenState> login(@Body User user);
}
