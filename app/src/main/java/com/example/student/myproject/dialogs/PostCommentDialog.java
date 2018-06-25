package com.example.student.myproject.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.R;
import com.example.student.myproject.fragments.CommentsFragment;
import com.example.student.myproject.model.Comment;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.CommentService;
import com.example.student.myproject.util.TokenProvider;
import com.example.student.myproject.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCommentDialog extends DialogFragment {

    private Comment comment;
    private EditText etCommentContent;
    private TextView actionDoPostComment, actionDoCancelPosting;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_post_comment, container, false);
        etCommentContent = (EditText) view.findViewById(R.id.dialog_post_comment_input);
        actionDoPostComment = (TextView) view.findViewById(R.id.action_do_post_comment);
        actionDoCancelPosting = (TextView) view.findViewById(R.id.action_do_cancel_posting);

        comment = (Comment) getArguments().getSerializable("comment");
        comment.setPost((Post) getArguments().getSerializable("post"));

        actionDoCancelPosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                comment = null;
                getDialog().dismiss();
            }
        });

        actionDoPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"".equals(etCommentContent.getText().toString()))
                {
                    comment.setContent(etCommentContent.getText().toString());
                    CommentService commentService = Util.retrofit.create(CommentService.class);
                    final Call<Comment> call = commentService.create(TokenProvider.getToken(getActivity().getApplicationContext()),comment.getPost().getId(), comment);
                    call.enqueue(new Callback<Comment>() {
                        @Override
                        public void onResponse(Call<Comment> call, Response<Comment> response)
                        {
                            comment = response.body();
                            CommentsFragment.comments.add(0,comment);
                            getDialog().dismiss();
                        }

                        @Override
                        public void onFailure(Call<Comment> call, Throwable t)
                        {
                            comment = null;
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        this.setCancelable(false);
        return view;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
