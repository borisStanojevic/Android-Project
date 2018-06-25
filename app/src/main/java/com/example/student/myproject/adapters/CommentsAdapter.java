package com.example.student.myproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.PostsActivity;
import com.example.student.myproject.R;
import com.example.student.myproject.UsersActivity;
import com.example.student.myproject.fragments.CommentsFragment;
import com.example.student.myproject.model.Comment;
import com.example.student.myproject.model.Role;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.CommentService;
import com.example.student.myproject.util.TokenProvider;
import com.example.student.myproject.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    public CommentsAdapter(Context context, List<Comment> comments)
    {
        super(context, 0, comments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final Comment comment = getItem(position);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.comments_list_view_item, parent, false);
        }

        convertView.setClickable(true);
        convertView.setFocusable(true);

        TextView tvCommentId = (TextView) convertView.findViewById(R.id.tv_comment_id);
        TextView tvCommentAuthor = (TextView) convertView.findViewById(R.id.tv_comment_author);
        TextView tvCommentContent = (TextView) convertView.findViewById(R.id.tv_comment_content);
        final TextView tvCommentLikes = (TextView) convertView.findViewById(R.id.tv_comment_likes);
        final TextView tvCommentDislikes = (TextView) convertView.findViewById(R.id.tv_comment_dislikes);

        tvCommentId.setText(String.valueOf(comment.getId()));
        tvCommentAuthor.setText(comment.getAuthor().getUsername());
        tvCommentAuthor.setTypeface(tvCommentAuthor.getTypeface(), Typeface.BOLD);
        tvCommentAuthor.append(" posted on ");
        tvCommentAuthor.append(comment.getDate());
        tvCommentContent.setText(comment.getContent());
        tvCommentLikes.setText(String.valueOf(comment.getLikes()) + " ");
        tvCommentDislikes.setText(" " + String.valueOf(comment.getDislikes()));

        final ImageButton btnLike = (ImageButton)convertView.findViewById(R.id.btn_like_comment);
        final ImageButton btnDislike = (ImageButton)convertView.findViewById(R.id.btn_dislike_comment);
        final ImageButton btnDelete = (ImageButton) convertView.findViewById(R.id.btn_delete_comment);

        btnLike.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Ako ulogovani nije autor komentara ne moze like
                if(getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null)
                        .equals(comment.getAuthor().getUsername()))
                {
                    Snackbar.make(parent,"You can not like your own comments", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //U suprotnom uvecaj like za 1, umanji dislike za 1 ako je pozitivan, onemoguci like dugme, omoguci dislajk
                int currentLikes = Integer.parseInt(tvCommentLikes.getText().toString().trim());
                int newLikes = currentLikes + 1;
                int currentDislikes = Integer.parseInt(tvCommentDislikes.getText().toString().trim());
                int newDislikes = currentDislikes > 0 ? currentDislikes - 1 : 0;
                comment.setLikes(newLikes);
                comment.setDislikes(newDislikes);
                tvCommentLikes.setText(String.valueOf(newLikes) + " ");
                tvCommentDislikes.setText(" " + String.valueOf(newDislikes));
                btnLike.setClickable(false);
                btnDislike.setClickable(true);
            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Ako ulogovani nije autor posta ne moze dislike
                if(getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE).getString("loggedInUserUsername", null)
                        .equals(comment.getAuthor().getUsername()))
                {
                    Snackbar.make(parent, "You can not dislike your own comments", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //U suprotnom uvecaj dislike za 1, umanji like za 1 ako je pozitivan, onemoguci dislike, omoguci lajk
                int currentDislikes = Integer.parseInt(tvCommentDislikes.getText().toString().trim());
                int newDislikes = currentDislikes + 1;
                int currentLikes = Integer.parseInt(tvCommentLikes.getText().toString().trim());
                int newLikes = currentLikes > 0 ? currentLikes - 1 : 0;
                comment.setLikes(newLikes);
                comment.setDislikes(newDislikes);
                tvCommentDislikes.setText(" " + String.valueOf(newDislikes));
                tvCommentLikes.setText(String.valueOf(newLikes) + " ");
                btnLike.setClickable(true);
                btnDislike.setClickable(false);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                String currentUserJson = sharedPreferences.getString("currentUser", "mitar123");
                ObjectMapper objectMapper = new ObjectMapper();
                User currentUser = null;
                boolean currentUserIsAdmin = false;
                try {
                    currentUser = objectMapper.readValue(currentUserJson, User.class);
                    currentUserIsAdmin = false;
                    for (Role role : currentUser.getRoles()) {
                        if (role.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                            currentUserIsAdmin = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!currentUserIsAdmin)
                {
                    Snackbar.make(parent, "Unauthorized : Only admins can delete comments", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:
                                CommentService commentService = Util.retrofit.create(CommentService.class);
                                final Call<Void> call = commentService.delete(TokenProvider.getToken(getContext()), -1 , comment.getId());
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response)
                                    {
                                        if(response.code() == 200) {
                                            CommentsFragment.comments.remove(comment);
                                            CommentsAdapter.this.notifyDataSetChanged();
                                            Snackbar.make(parent, "Comment deleted", Snackbar.LENGTH_SHORT).show();
                                        }

                                    }
                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t)
                                    {
                                        Toast.makeText(parent.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Are you sure ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        return convertView;
    }

}