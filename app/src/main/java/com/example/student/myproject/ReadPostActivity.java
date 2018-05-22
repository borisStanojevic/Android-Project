package com.example.student.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.student.myproject.adapters.CommentsAdapter;
import com.example.student.myproject.model.Comment;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.model.Tag;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.PostService;
import com.example.student.myproject.util.Util;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Post post;
    //Ovdje stade Rile Veliki
    private ListView commentsList;
    private List<Comment> comments;
    private CommentsAdapter commentsAdapter;

    private String[] drawerListItems;
    private ArrayAdapter<String> stringArrayAdapter;
    //Objekat ove klase predstavlja slusac dogadjaja klika na jednu od stavki ListViewa koji se nalazi u draweru
    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            Toast.makeText(ReadPostActivity.this, "Clicked Drawer List Item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);

        //Dobavljam 'svoj' toolbar 'app_bar' i postavljam ga kao defaultni toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //Ukljucujem ugradjenu android 'toggle' ikonicu u toolbar koja ce da govori kad je drawer prevucen a kad ne
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.read_post_drawer_layout);

        //Pravim objekat tipa ActionBarDrawerToggle koji je slusaj dogadjaja kad se drawer prevuce i kad se drawer 'svuce'
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            //Poziva se kad se drawer otvori
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            //Poziva se kad se drawer zatvori
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        //U sledece dvije linije ukljucujem drawer toggle indikator i dodajem drawerToggle objekat u drawerLayout
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);

        /*
        Konkretan ListView objekat,
        Niz stringova koji ce biti prikazani u ListView-u,
        Objekat adapter koji ce 'presuti' stringove iz niza u ListView da bi se prikazali graficki,
        Postavljanje objekta koji ce da slusa i obradi dogadjaje pritiska na neku stavku ListView-a
        */
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerListItems = getResources().getStringArray(R.array.activities_array);
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerListItems);
        drawerList.setAdapter(stringArrayAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        Intent i = getIntent();
        int postId = i.getIntExtra("postId", -1);
        for(Post p : PostsActivity.postsList)
        {
            if(p.getId() == postId)
            {
                post = p;
                break;
            }
        }
        if(post == null)
            post = new Post();

        post.showInLayout(this);
    }

    //Dodavam svoj meni na toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.read_post_menu, menu);

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if(getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null) != null
                && !(getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null).equals(post.getAuthor().getUsername())))
            deleteItem.setVisible(false);

        return true;
    }

    //Metoda koja se poziva kada kliknemo na neku od stavki naseg menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Ovdje aktiviram drawer toggle da bi moglo da se klikne na njega
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int itemClickedId = item.getItemId();
        switch (itemClickedId) {
            case R.id.action_delete:
                PostService postService = Util.retrofit.create(PostService.class);
                final Call<Void> call = postService.doDeletePost(post.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response)
                    {
                        Intent intent = new Intent(ReadPostActivity.this, PostsActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t)
                    {
                        Toast.makeText(ReadPostActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.action_settings:
                Toast.makeText(this, "You just clicked Settings action item", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Metoda koja govori drawer toggle indikatoru da prikaze razlicitu ikonicu u zavisnosti da li je drawer prevucen ili 'svucen', sinhronizacija
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    //Sinhronizuj drawer toggle indikator kad se konfiguracija promijeni, npr. kada promijenim orijentaciju uredjaja
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
