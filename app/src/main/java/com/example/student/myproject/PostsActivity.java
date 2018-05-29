package com.example.student.myproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
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

import com.example.student.myproject.adapters.PostsAdapter;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.PostService;
import com.example.student.myproject.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView postsListView;
    public  List<Post> postsList;
    private PostsAdapter postsAdapter;
    private ListView drawerList;
    private String[] drawerListItems;
    private ArrayAdapter<String> stringArrayAdapter;

    //Objekat ove klase predstavlja slusac dogadjaja klika na jednu od stavki ListViewa koji se nalazi u draweru
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            switch (position)
            {
                case 0:
                    startActivity(new Intent(PostsActivity.this, CreatePostActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(PostsActivity.this, PostsActivity.class));

                    break;
                case 2:
                    startActivity(new Intent(PostsActivity.this, SettingsActivity.class));
                    break;
                case 3:
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("loggedInUserUsername", null);
                                    editor.apply();
                                    startActivity(new Intent(PostsActivity.this, LoginActivity.class));
                                    finish();
                                    return;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostsActivity.this);
                    builder.setMessage("Are you sure you want to log out?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    break;
                default:
                    break;
            }
        }
    }

    private class PostsItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            int postId = postsList.get(position).getId();
            Intent intent = new Intent(PostsActivity.this, ReadPostActivity.class);
            intent.putExtra("id", postId);
            startActivity(intent);
        }
    }

//    private Comparator<Post> createPostComparator(String key) {
//        if ("date_asc".equalsIgnoreCase(key)) {
//            return new Comparator<Post>() {
//                @Override
//                public int compare(Post o1, Post o2) {
//                    return o1.getDate().compareTo(o2.getDate());
//                }
//            };
//        } else if ("pop_asc".equalsIgnoreCase(key)) {
//            return new Comparator<Post>() {
//                @Override
//                public int compare(Post o1, Post o2) {
//                    return o1.getLikes() - o2.getLikes();
//                }
//            };
//        } else if ("pop_desc".equalsIgnoreCase(key)) {
//            return new Comparator<Post>() {
//                @Override
//                public int compare(Post o1, Post o2) {
//                    return o2.getLikes() - o1.getLikes();
//                }
//            };
//        } else {
//            return new Comparator<Post>() {
//                @Override
//                public int compare(Post o1, Post o2) {
//                    return o2.getDate().compareTo(o1.getDate());
//                }
//            };
//        }
//    }

    private void sortPosts(List<Post> posts) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String postsSortingKey = sharedPreferences.getString("posts_preference", "date_desc");
//        Comparator<Post> postComparator = createPostComparator(postsSortingKey);
//        Collections.sort(posts, postComparator);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        //Postavljam defaultne vrijednosti podesavanja ( sortiranje komentara i objava po datumu )
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //Ukljucujem ugradjenu android 'toggle' ikonicu u toolbar koja ce da govori kad je drawer prevucen a kad ne
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.posts_drawer_layout);

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

    }

    //Dodavam svoj meni na toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.posts_menu, menu);
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
            case R.id.action_create_post:
                startActivity(new Intent(this, CreatePostActivity.class));
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

        //U sledecim linijama instanciram postsListView, listu objekata, adapter, postavljam adapter i eventualno postavljam slusac dogadjaja za klik na stavku
        postsListView = (ListView) findViewById(R.id.posts_list_view);

        PostService postService = Util.retrofit.create(PostService.class);
        final Call<List<Post>> call = postService.doGetPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response)
            {
                postsList = response.body();

                postsAdapter = new PostsAdapter(PostsActivity.this, postsList);

                postsListView.setAdapter(postsAdapter);

                postsListView.setOnItemClickListener(new PostsItemClickListener());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t)
            {
                Toast.makeText(PostsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

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
