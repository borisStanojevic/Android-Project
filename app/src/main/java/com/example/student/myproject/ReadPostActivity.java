package com.example.student.myproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.student.myproject.adapters.CommentsAdapter;
import com.example.student.myproject.fragments.CommentsFragment;
import com.example.student.myproject.fragments.PostFragment;
import com.example.student.myproject.model.Comment;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.util.CommentService;
import com.example.student.myproject.util.PostService;
import com.example.student.myproject.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return fragmentTitleList.get(position);
        }

    }
    private PostFragment postFragment;
    private ListView commentsListView;
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
            switch (position)
            {
                case 0:
                    startActivity(new Intent(ReadPostActivity.this, CreatePostActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(ReadPostActivity.this, PostsActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(ReadPostActivity.this, UsersActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(ReadPostActivity.this, SettingsActivity.class));
                    break;
                case 4:
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("loggedInUserUsername", null);
                                    editor.apply();
                                    startActivity(new Intent(ReadPostActivity.this, LoginActivity.class));
                                    finish();
                                    return;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReadPostActivity.this);
                    builder.setMessage("Are you sure you want to log out?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    break;
                default:
                    break;
            }
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




    }

    //Dodavam svoj meni na toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.read_post_menu, menu);
        
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
                return false;
            case R.id.action_settings:
                return  false;
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
    protected void onResume()
    {
        super.onResume();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter((getSupportFragmentManager()));
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Intent i = getIntent();
        int postId = i.getIntExtra("id", -1);
        PostFragment postFragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", postId);
        postFragment.setArguments(bundle);
        CommentsFragment commentsFragment = new CommentsFragment();
        bundle = new Bundle();
        bundle.putInt("postId", postId);
        commentsFragment.setArguments(bundle);
        viewPagerAdapter.addFragment(postFragment, "Post");
        viewPagerAdapter.addFragment(commentsFragment, "Comments");
        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        drawerLayout.closeDrawer(Gravity.LEFT, false);
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
