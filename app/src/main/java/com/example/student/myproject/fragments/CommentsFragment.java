package com.example.student.myproject.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.student.myproject.R;
import com.example.student.myproject.SettingsActivity;
import com.example.student.myproject.adapters.CommentsAdapter;
import com.example.student.myproject.dialogs.PostCommentDialog;
import com.example.student.myproject.model.Comment;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.CommentService;
import com.example.student.myproject.util.TokenProvider;
import com.example.student.myproject.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsFragment extends ListFragment implements AdapterView.OnItemClickListener{

    private static int lastItemClickedPosition = 0;

    public static List<Comment> comments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(this);
        setListAdapter(new CommentsAdapter(getActivity(), comments));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity().getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
        lastItemClickedPosition = position;
//        view.setBackgroundResource(R.color.colorPrimaryDark);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        lastItemClickedPosition = 0;
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, 0, Menu.NONE, "Comment").setIcon(R.drawable.baseline_comment_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        lastItemClickedPosition = 0;

        Bundle bundle = getArguments();
        int postId = bundle.getInt("postId");

        CommentService commentService = Util.retrofit.create(CommentService.class);
        final Call<List<Comment>> call = commentService.getAll(TokenProvider.getToken(getContext()), postId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response)
            {
                if(response.body() != null)
                {
                    comments.clear();
                    comments.addAll(response.body());
                    sortComments(comments);
                    ((ArrayAdapter<Comment>) getListAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t)
            {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClickedId = item.getItemId();
        switch (itemClickedId)
        {
            case 0:
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                PostCommentDialog postCommentDialog = new PostCommentDialog();

                String loggedInUserUsername = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null);
                int postId = getArguments().getInt("postId");

                User author = new User();
                author.setUsername(loggedInUserUsername);
                Post post = new Post();
                post.setId(postId);
                Comment comment = new Comment();
                comment.setAuthor(author);

                Bundle bundle = new Bundle();
                bundle.putSerializable("comment", comment);
                bundle.putSerializable("post", post);
                postCommentDialog.setArguments(bundle);

                postCommentDialog.show(fragmentManager, "Comment");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Comparator<Comment> createCommentComparator(String key) {
        if ("date_asc".equalsIgnoreCase(key)) {
            return new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            };
        } else if ("pop_asc".equalsIgnoreCase(key)) {
            return new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    return o1.getLikes() - o2.getLikes();
                }
            };
        } else if ("pop_desc".equalsIgnoreCase(key)) {
            return new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    return o2.getLikes() - o1.getLikes();
                }
            };
        } else {
            return new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    return o2.getDate().compareTo(o1.getDate());
                }
            };
        }
    }

    private void sortComments(List<Comment> commentsList) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String postsSortingKey = sharedPreferences.getString("comments_preference", "date_desc");
        Comparator<Comment> commentComparator = createCommentComparator(postsSortingKey);
        Collections.sort(commentsList, commentComparator);
    }

}
