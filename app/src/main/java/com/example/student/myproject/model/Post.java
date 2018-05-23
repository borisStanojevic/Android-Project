package com.example.student.myproject.model;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.student.myproject.R;
import com.example.student.myproject.ReadPostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post {

    private int id;
    private String title;
    private String content;
    private User author;
    private Bitmap photo;
    private String date;
    private double locationLatitude;
    private double locationLongitude;
    private transient Location location;
    private List<Tag> tags;
    private transient List<Comment> comments;
    private int likes;
    private int dislikes;

    public Post() {
        tags = new ArrayList<Tag>();
        comments = new ArrayList<Comment>();
    }

/*
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
*/

    public Location getLocation() {
        return this.location;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
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

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void showInLayout(ReadPostActivity activity) {
        TextView tvTitle = (TextView) activity.findViewById(R.id.tv_post_title);
        tvTitle.setText(this.title);
        TextView tvContent = (TextView) activity.findViewById(R.id.tv_post_content);
        tvContent.setText(this.content);
        TextView tvPostAuthor = (TextView) activity.findViewById(R.id.tv_post_author);
        tvPostAuthor.setText(this.author.getUsername());
        TextView tvDatePosted = (TextView) activity.findViewById(R.id.tv_date_posted);
        tvDatePosted.setText(this.getDate());
        TextView tvTags = (TextView) activity.findViewById(R.id.tv_post_tags);
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(this.getLocationLatitude(), this.getLocationLongitude(), 1);
            String locality = addresses.get(0).getLocality();
            String countryName = addresses.get(0).getCountryName();
            ((TextView) activity.findViewById(R.id.tv_location_posted)).setText("Near " + locality + " , " + countryName);
        } catch (IOException exc) {
            exc.printStackTrace();
        } catch (IndexOutOfBoundsException exc) {
            ((TextView) activity.findViewById(R.id.tv_location_posted)).setText("Unknown");
            for (Tag tag : this.tags) {
                tvTags.append(tag.getName() + " ,");
            }
            if (tvTags.getText() != null && !"".equals(tvTags.getText()))
                tvTags.setText(tvTags.getText().subSequence(0, tvTags.length() - 1));
        }
    }

}
