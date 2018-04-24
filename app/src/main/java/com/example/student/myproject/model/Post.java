package com.example.student.myproject.model;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.student.myproject.R;
import com.example.student.myproject.ReadPostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

    private int id;
    private String title;
    private String content;
    private Bitmap photo;
    private User author;
    private Date date;
    private Location location;
    private List<Tag> tags;
    private List<Comment> comments;
    private int likes;
    private int dislikes;

    public Post() {
        tags = new ArrayList<Tag>();
        comments = new ArrayList<Comment>();
    }

    //Konstruktor kojim ce se od JSON-a koji ce dolaziti sa servera konstruisati Post objekat
    //Trebam doraditi ovaj konstruktor trenutno cu ga koristiti samo za PostsActivity
    public Post(JSONObject json)
    {
        try
        {
            this.id = json.getInt("id");
            this.title = json.getString("title");
            this.content = json.getString("content");
            this.author = new User();
            this.author.setUsername(json.getString("authorUsername"));
            this.likes = json.getInt("likes");
            this.dislikes = json.getInt("dislikes");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    //Metoda koja vraca Java listu objekata Post za dobijeni JSON niz
    public static List<Post> getPostsFromJSON(JSONArray jsonArray)
    {
        ArrayList<Post> posts = new ArrayList<>();
        for (int i = 0 ; i < jsonArray.length() ; i++)
        {
            try
            {
                posts.add(new Post(jsonArray.getJSONObject(i)));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return posts;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public void showInLayout(ReadPostActivity activity) {
        TextView tvTitle = (TextView) activity.findViewById(R.id.tv_post_title);
        tvTitle.setText(this.title);
        TextView tvContent = (TextView) activity.findViewById(R.id.tv_post_content);
        tvContent.setText(this.content);
        TextView tvPostAuthor = (TextView) activity.findViewById(R.id.tv_post_author);
        tvPostAuthor.setText(this.author.getUsername());
        TextView tvDatePosted = (TextView) activity.findViewById(R.id.tv_date_posted);
        tvDatePosted.setText(new SimpleDateFormat("yyyy/mm/dd").format(this.date));
        TextView tvLocationPosted = (TextView) activity.findViewById(R.id.tv_location_posted);
        tvLocationPosted.setText("Daaaaleko daaaleko");
        TextView tvTags = (TextView) activity.findViewById(R.id.tv_post_tags);
        for(Tag tag : this.tags)
        {
            tvTags.append(tag.getName() + " ,");
        }
    }


}
