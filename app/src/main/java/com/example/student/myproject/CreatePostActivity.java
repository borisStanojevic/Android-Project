package com.example.student.myproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;

public class CreatePostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String[] drawerListItems;
    private DrawerLayout drawerLayout;
    private ListView drawerList;

    private class DrawerItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
//            int selectedItemPosition = drawerList.getSelectedItemPosition();
//            switch (selectedItemPosition)
//            {
//                case 0:
//                    startActivity(new Intent(CreatePostActivity.this, CreatePostActivity.class));
//                    break;
//                case 1:
//                    startActivity(new Intent(CreatePostActivity.this, PostsActivity.class));
//                    break;
//                case 2:
//                    startActivity(new Intent(CreatePostActivity.this, SettingsActivity.class));
//                    break;
//                default:
//                    break;
//            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
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
