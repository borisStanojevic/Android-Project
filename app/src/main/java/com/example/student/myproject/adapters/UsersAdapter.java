package com.example.student.myproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student.myproject.R;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {

    public UsersAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.users_list_item, parent, false);
        }

        ImageView ivUserPhoto = (ImageView)convertView.findViewById(R.id.user_photo);
        String url = Util.SERVICE_API_PATH + "images/" + user.getPhoto();
        Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(ivUserPhoto);
        TextView tvUserUsername = (TextView)convertView.findViewById(R.id.user_username);
        tvUserUsername.setText(user.getUsername().toString());
        TextView tvUserFullName = (TextView)convertView.findViewById(R.id.user_fullname);
        tvUserFullName.setText(user.getFullName().toString());

        return convertView;
    }
}
