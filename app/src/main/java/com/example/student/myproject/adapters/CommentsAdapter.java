package com.example.student.myproject.adapters;

import android.content.Context;
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

import com.example.student.myproject.R;
import com.example.student.myproject.model.Comment;

import java.util.List;

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


        return convertView;
    }

}