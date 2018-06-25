package com.example.student.myproject.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.example.student.myproject.model.Role;
import com.example.student.myproject.model.Tag;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.PostService;
import com.example.student.myproject.util.TokenProvider;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePostDialog extends DialogFragment {

    private Post post;
    private EditText etTitle;
    private EditText etContent;
    private EditText etTags;
    private TextView tvActionConfirm;
    private TextView tvActionCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_post_dialog, container, false);

        etTitle = (EditText) view.findViewById(R.id.et_title);
        etContent = (EditText) view.findViewById(R.id.et_content);
        etTags = (EditText) view.findViewById(R.id.et_tags);
        tvActionConfirm = (TextView) view.findViewById(R.id.action_confirm);
        tvActionCancel = (TextView) view.findViewById(R.id.action_cancel);

        post = (Post) getArguments().getSerializable("post");
        etTitle.setText(post.getTitle());
        etContent.setText(post.getContent());
        if(post.getTags() != null)
        {
            if(post.getTags().size() == 1)
                etTags.setText(post.getTags().get(0).getName());
            else
                {
                for (Tag tag : post.getTags())
                    etTags.append(tag.getName() + ";");
                }
        }
        if (etTags.getText().toString() != null && !"".equals(etTags.getText().toString()))
            etTags.setText(etTags.getText().toString().subSequence(0, etTags.getText().toString().length() - 1));

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
                post.getTags().clear();
                String tags = etTags.getText().toString();

                String[] tagsTokens;
                try
                {
                    tagsTokens = tags.split(";");
                }
                //Ako ne uspije tokenizacija znaci ili da nema tagova ili da ima jedan tag
                catch (Exception exc)
                {
                    //Ako ima jedan tag
                    if(tags != null && !"".equals(tags)) {
                        tagsTokens = new String[1];
                        tagsTokens[0] = tags;
                    }
                    else
                        tagsTokens = new String[0];
                }

                for(int i = 0 ; i < tagsTokens.length ; i++)
                {
                    if(tagsTokens[i] != null && !"".equals(tagsTokens[i]))
                    {
                        Tag tag = new Tag();
                        tag.setName(tagsTokens[i].trim());
                        post.getTags().add(tag);
                    }
                }

                SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
                //*
                String currentUserJson = sharedPreferences.getString("currentUser", "mitar123");
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    User currentUser = objectMapper.readValue(currentUserJson, User.class);
                    boolean currentUserIsAdmin = false;
                    for(Role role : currentUser.getRoles())
                    {
                        if(role.getRole().equalsIgnoreCase("ROLE_ADMIN"))
                        {
                            currentUserIsAdmin = true;
                            break;
                        }
                    }
                    if(!currentUser.getUsername().equals(post.getAuthor().getUsername()) && !currentUserIsAdmin)
                    {
                        UpdatePostDialog.this.dismiss();
                        Toast.makeText(getActivity(), "You are unauthorized for this action", Snackbar.LENGTH_LONG).show();;
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                PostService postService = Util.retrofit.create(PostService.class);
                final Call<Post> call = postService.update(TokenProvider.getToken(getActivity().getApplicationContext()), post);
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
