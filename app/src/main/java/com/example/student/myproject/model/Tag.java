package com.example.student.myproject.model;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    private String name;
    private transient List<Post> posts;

    public Tag()
    {
        posts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
