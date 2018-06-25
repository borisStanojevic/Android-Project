package com.example.student.myproject.util;

import com.example.student.myproject.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {

    @GET("users/{username}")
    Call<User> getByUsername(@Header("Authorization") String jwtToken, @Path("username") String username);

    @POST("users")
    Call<User> create(@Header("Authorization") String jwtToken, @Body User user);

    @Multipart
    @POST("users/upload")
    Call<User> uploadPhoto(@Header("Authorization") String jwtToken, @Part MultipartBody.Part file);

    @POST("users/register")
    Call<User> register(@Body User user);

    @PUT("users")
    Call<User> update(@Header("Authorization") String jwtToken, @Body User user);

    @DELETE("users/{username}")
    Call<Void> delete(@Header("Authorization") String jwtToken, @Path("username") String username);

    @GET("users")
    Call<List<User>> getAll(@Header("Authorization") String jwtToken);
}
