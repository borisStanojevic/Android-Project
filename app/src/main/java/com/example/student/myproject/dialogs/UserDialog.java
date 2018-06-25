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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.myproject.R;
import com.example.student.myproject.UserActivity;
import com.example.student.myproject.UsersActivity;
import com.example.student.myproject.model.Role;
import com.example.student.myproject.model.User;
import com.example.student.myproject.util.TokenProvider;
import com.example.student.myproject.util.UserService;
import com.example.student.myproject.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDialog extends DialogFragment {

    private User user;
    private EditText etUsername;
    private EditText etEmail;
    private EditText etFullName;
    private EditText etPassword;
    private TextView tvActionConfirm;
    private TextView tvActionCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_dialog, container, false);
        etUsername = (EditText) view.findViewById(R.id.et_username);
        etEmail = (EditText) view.findViewById(R.id.et_email);
        etFullName = (EditText) view.findViewById(R.id.et_full_name);
        etPassword = (EditText) view.findViewById(R.id.et_password);
        tvActionConfirm = (TextView) view.findViewById(R.id.action_confirm);
        tvActionCancel = (TextView) view.findViewById(R.id.action_cancel);

        tvActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = null;
                getDialog().dismiss();
            }
        });

        try
        {
            user = (User) getArguments().getSerializable("user");
        }
        catch (Exception e)
        {
            user = null;
        }
        if (user == null) //Znaci da radim dodavanje korisnika
        {
            etUsername.setVisibility(View.VISIBLE);
            tvActionConfirm.setText("Add");

            tvActionConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String fullName = etFullName.getText().toString().trim();

                    UserService userService = Util.retrofit.create(UserService.class);
                    final Call<User> call = userService.create(TokenProvider.getToken(getActivity().getApplicationContext()),new User(username, password, email, fullName));
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response)
                        {
                            UsersActivity.users.add(response.body());
                            getDialog().dismiss();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t)
                        {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
        else //Znaci da radim update korisnika
        {
            etUsername.setVisibility(View.GONE);
            tvActionConfirm.setText("Update");

            etEmail.setText(user.getEmail());
            etFullName.setText(user.getFullName());
            etPassword.setText(user.getPassword());

            tvActionConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setPassword(etPassword.getText().toString().trim());
                    user.setEmail(etEmail.getText().toString().trim());
                    user.setFullName(etFullName.getText().toString().trim());

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
                        //Ako je update, ne moze da updateuje neko ko nije taj korisnik ili admin
                        if(user != null) {
                            if (!currentUser.getUsername().equals(user.getUsername()) && !currentUserIsAdmin) {
                                UserDialog.this.dismiss();
                                Toast.makeText(getActivity(), "You are unauthorized for this action", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        //Ako je dodavanje ne moze da doda niko ko nije admin
                        if(user == null)
                        {
                            if(!currentUserIsAdmin)
                            {
                                UserDialog.this.dismiss();
                                Toast.makeText(getActivity(), "You are unauthorized for this action", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //*
                    String jwtToken = sharedPreferences.getString("token", "");
                    String token = "Bearer " + jwtToken;

                    UserService userService = Util.retrofit.create(UserService.class);
                    final Call<User> call = userService.update(TokenProvider.getToken(getActivity().getApplicationContext()), user);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response)
                        {
                            user = response.body();
                            Intent intent = new Intent(getActivity(), UserActivity.class);
                            intent.putExtra("username", user.getUsername());
                            getActivity().startActivity(intent);
                            getDialog().dismiss();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t)
                        {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
        this.setCancelable(false);
        return view;
    }
}
