package com.example.student.myproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.adapters.PostsAdapter;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.model.Tag;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.PostService;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.Util;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private String[] drawerListItems;
    private ArrayAdapter<String> stringArrayAdapter;
    private String loggedInUserUsername;
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
                    startActivity(new Intent(CreatePostActivity.this, CreatePostActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(CreatePostActivity.this, PostsActivity.class));

                    break;
                case 2:
                    startActivity(new Intent(CreatePostActivity.this, SettingsActivity.class));
                    break;
                case 3:
                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loggedInUserUsername", null);
                    editor.apply();
                    startActivity(new Intent(CreatePostActivity.this, LoginActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String locationProvider = LocationManager.NETWORK_PROVIDER;
    private Geocoder geocoder;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //Dobavljam 'svoj' toolbar 'app_bar' i postavljam ga kao defaultni toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //Ukljucujem ugradjenu android 'toggle' ikonicu u toolbar koja ce da govori kad je drawer prevucen a kad ne
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.create_post_drawer_layout);

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

        post = new Post();

        loggedInUserUsername = getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                post.setLocationLatitude(location.getLatitude());
                post.setLocationLongitude(location.getLongitude());
                post.setLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        geocoder = new Geocoder(this);

    }

    //Dodavam svoj meni na toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.create_post_menu, menu);
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
            case R.id.action_do_post:
                //Pokusavam da dobavim lokaciju
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        )
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 99);
                }
                else
                {
                    locationManager.requestSingleUpdate(locationProvider, locationListener, null);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                    post.setLocation(lastKnownLocation);
                    post.setLocationLatitude(lastKnownLocation.getLatitude());
                    post.setLocationLongitude(lastKnownLocation.getLongitude());
                }

                //Uzimam naslov, sadrzaj, tagove
                String title = ((EditText)findViewById(R.id.et_post_title)).getText().toString();
                String content = ((EditText)findViewById(R.id.et_post_content)).getText().toString();
                String tags = ((EditText)findViewById(R.id.et_post_tags)).getText().toString();
                String[] tagsTokens;
                try
                {
                    tagsTokens = tags.split(",");
                }
                catch (Exception exc)
                {
                    tagsTokens = new String[0];
                }

                post.setTitle(title);
                post.setContent(content);
                User author = new User();
                author.setUsername(loggedInUserUsername);
                post.setAuthor(author);
                for(int i = 0 ; i < tagsTokens.length ; i++)
                {
                    if(tagsTokens[i] != null && !"".equals(tagsTokens[i]))
                    {
                        Tag tag = new Tag();
                        tag.setName(tagsTokens[i].trim());
                        post.getTags().add(tag);
                    }
                }

                PostService postService = Util.retrofit.create(PostService.class);
                final Call<Post> call = postService.doCreatePost(post);
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response)
                    {
                        Intent intent = new Intent(CreatePostActivity.this, PostsActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(Call<Post> call, Throwable t)
                    {
                        Toast.makeText(CreatePostActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.action_do_cancel_post:
                ((EditText)findViewById(R.id.et_post_content)).getText().clear();
                ((EditText)findViewById(R.id.et_post_tags)).getText().toString();
                Intent intent = new Intent(CreatePostActivity.this, PostsActivity.class );
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        locationManager.requestSingleUpdate(locationProvider, locationListener, null);                        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                        post.setLocation(lastKnownLocation);
                    } catch (SecurityException exc) {
                        post.setLocation(null);
                    }
                } else { post.setLocation(null);}

                return;
            }

        }
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
