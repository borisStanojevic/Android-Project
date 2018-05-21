package com.example.student.myproject.util;

import com.example.student.myproject.model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PostService {

    @GET("posts")
    Call<List<PostService>> getAll();

    @GET("posts/{id}")
    Call<Post> getById(@Path("id") int id);

}
