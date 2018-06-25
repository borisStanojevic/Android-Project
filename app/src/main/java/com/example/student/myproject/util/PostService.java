package com.example.student.myproject.util;

import com.example.student.myproject.model.Post;
import com.example.student.myproject.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PostService {

    @GET("posts/{id}")
    Call<Post> getById(@Header("Authorization") String token,@Path("id") int id);

    @GET("posts")
    Call<List<Post>> getAll(@Header("Authorization") String token);

    @POST("posts")
    Call<Post> create(@Header("Authorization") String token,@Body Post post);

    @Multipart
    @POST("posts/upload")
    Call<Post> uploadPhoto(@Header("Authorization") String token, @Part MultipartBody.Part file);

    @PUT("posts")
    Call<Post> update(@Header("Authorization") String token, @Body Post post);

    @DELETE("posts/{id}")
    Call<Void> delete(@Header("Authorization") String token, @Path("id") int id);

}
