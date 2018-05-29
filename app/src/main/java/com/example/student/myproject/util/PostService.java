package com.example.student.myproject.util;

import com.example.student.myproject.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PostService {

    @GET("posts/{id}")
    Call<Post> doGetPostById(@Path("id") int id);

    @GET("posts")
    Call<List<Post>> doGetPosts();

    @POST("posts")
    Call<Post> doCreatePost(@Body Post post);

    @DELETE("posts/{id}")
    Call<Void> doDeletePost(@Path("id") int id);


}
