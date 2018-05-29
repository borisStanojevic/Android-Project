package com.example.student.myproject.util;

import com.example.student.myproject.model.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentService {

    @GET("posts/{postId}/comments")
    Call<List<Comment>> getAll(@Path("postId") int postId);

    @POST("posts/{postId}/comments")
    Call<Comment> create(@Path("postId") int postId, @Body Comment comment);
}
