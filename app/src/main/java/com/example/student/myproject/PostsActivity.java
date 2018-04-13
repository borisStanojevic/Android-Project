package com.example.student.myproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PostsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
    }

    public void btnStartCreatePostActivity(View view) {
        Intent i = new Intent(this, CreatePostActivity.class);
        startActivity(i);
    }

    public void btnStartReadPostActivity(View view) {
        Intent i = new Intent(this, ReadPostActivity.class);
        startActivity(i);
    }

    public void btnStartSettingsActivity(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void  onRestart(){
        super.onRestart();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
