package com.example.student.myproject.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.R;
import com.example.student.myproject.ReadPostActivity;
import com.example.student.myproject.UserActivity;
import com.example.student.myproject.model.Post;
import com.example.student.myproject.util.PostService;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePostDialog extends DialogFragment {

    private Post post;
    private EditText etTitle;
    private EditText etContent;
    private TextView tvActionConfirm;
    private TextView tvActionCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_post_dialog, container, false);

        etTitle = (EditText) view.findViewById(R.id.et_title);
        etContent = (EditText) view.findViewById(R.id.et_content);
        tvActionConfirm = (TextView) view.findViewById(R.id.action_confirm);
        tvActionCancel = (TextView) view.findViewById(R.id.action_cancel);

        post = (Post) getArguments().getSerializable("post");
        etTitle.setText(post.getTitle());
        etContent.setText(post.getContent());

        tvActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post = null;
                getDialog().dismiss();
            }
        });

        tvActionConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                post.setTitle(etTitle.getText().toString().trim());
                post.setContent(etContent.getText().toString().trim());
                PostService postService = Util.retrofit.create(PostService.class);
                final Call<Post> call = postService.update(post);
                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response)
                    {
                        post = response.body();
                        Intent intent = new Intent(getActivity(), ReadPostActivity.class);
                        intent.putExtra("id", post.getId());
                        getActivity().startActivity(intent);
                        getDialog().dismiss();
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t)
                    {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }

}
